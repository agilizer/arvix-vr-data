package cn.arvix.matterport.service

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import cn.arvix.matterport.consants.ArvixMatterportConstants
import cn.arvix.matterport.domain.ModelData
import cn.arvix.matterport.domain.ModelData.FetchStatus
import cn.arvix.matterport.repository.ModelDataRepository
import cn.arvix.matterport.util.StaticMethod

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

@Service
class FetchDataServiceImpl implements FetchDataService{
	private static final Logger log = LoggerFactory
	.getLogger(FetchDataServiceImpl.class);
	@Autowired
	ConfigDomainService configDomainService;
	@Autowired
	ModelDataRepository modelDataRepository;
	@Autowired
	JpaShareService jpaShareService;
	def static Long EXPIRE_TIME = 240000
	def RETRY_TIMES = 3;
	/**
	 * 实现不支持多个图片同时抓取。
	 */
	@Override
	public Map<String, Object> fetch(String sourceUrl,boolean force) {
		//url:https://my.matterport.com/show/?m=MXfJvWQecHT

		//System.getProperties().setProperty("proxySet", "true");
		//用的代理服务器
		//System.getProperties().setProperty("http.proxyHost", "207.91.10.234");
		//代理端口
		//System.getProperties().setProperty("http.proxyPort", "8080");
		
		def result = StaticMethod.getResult();
		if(sourceUrl){
			def subIndex = sourceUrl.indexOf("?m=");
			if(subIndex>0){
				def caseId = sourceUrl.substring(subIndex+3)
				ModelData modelData = modelDataRepository.findByCaseId(caseId);
				log.info("force:--->{}",force)
				if(force&&modelData!=null){
					modelDataRepository.delete(modelData.getId());
					modelData = null;
				}
				/**
				 * 已经存在抓取记录
				 */
				if(modelData!=null){
					if(modelData.getFetchStatus()==FetchStatus.FINISH){
						result.put(ArvixMatterportConstants.ERROR_MSG, "已经抓取完成，请不要重复抓取！");
					}
					if(modelData.getFetchStatus()==FetchStatus.FETCHING){
						result.put(ArvixMatterportConstants.ERROR_MSG, "正在抓取文件，共"+modelData.getFileSumCount()
								+"文件，当前抓取到"+modelData.getFileFetchedCount()+"！");
					}
				}else{
					modelData = new ModelData();
					def calendar = Calendar.getInstance()
					modelData.setDateCreated(calendar);
					modelData.setCaseId(caseId);
					modelData.setSourceUrl(sourceUrl);
					modelData.setFetchStatus(FetchStatus.FETCHING)
					modelData.setLastUpdated(calendar)
					modelData = modelDataRepository.saveAndFlush(modelData);
					def fileSaveDir =configDomainService.getConfigString(ArvixMatterportConstants.FILE_STORE_PATH)+caseId+"/"
					if(log.infoEnabled){
						log.info("fileSaveDir is:{}",fileSaveDir);
					}
					try{
						genModelData(caseId,modelData);
					}catch(e){
						log.error("fetch caseId {} genModelData error:",caseId,e)
						modelData.setFetchStatus(FetchStatus.ERROR);
						modelData.setFetchErrorMsg("抓取modelData时出错，"+e.getMessage());
						result.put(ArvixMatterportConstants.ERROR_MSG, "抓取网页数据出错");
						return result;
					}
					try{
						Thread.start {
							if(log.infoEnabled){
								log.info("start fetch files  sourceUrl:{}",sourceUrl);
							}
							genFiles(caseId,fileSaveDir,modelData);
						}
					}catch(e){
						log.error("fetch caseId {} genFiles error:",caseId,e)
						modelData.setFetchStatus(FetchStatus.ERROR);
						modelData.setFetchErrorMsg("抓取genFiles时出错，"+e.getMessage());
					}
					modelData = modelDataRepository.save(modelData);
					result.put(ArvixMatterportConstants.SUCCESS, true)
					result.put(ArvixMatterportConstants.DATA, "抓取已经开始，请等待！");
				}
			}else{
				result.put(ArvixMatterportConstants.ERROR_MSG, "源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
			}
		}else{
			result.put(ArvixMatterportConstants.ERROR_MSG, "源url不能为空");
		}
		return result;
	}

	public  void genFiles(String caseId,String fileSaveDir,ModelData modelData){
		def jsonObject =  genFileJson(caseId);
		def fileList = []
		def baseUrl = ""
		def fileSumCount = 0
		jsonObject.each{key,value->
			if(key!='base.url'){
				fileList.add(key)
				fileSumCount ++ ;
			}else{
				baseUrl = value
			}
		}
		/**
		 * 生成fileJson
		 */
		def fileJson = [:]
		jsonObject.each{key,value->
			fileJson.put(key, value)
		}
		def newUrl = configDomainService.getConfigString(ArvixMatterportConstants.SITE_URL)+"files/${caseId}/{{filename}}";
		fileJson.put("base.url",newUrl );
		modelData.setFileSumCount(fileSumCount)
		modelData.setFileJson(JSON.toJSONString(fileJson))

		if(log.infoEnabled){
			log.info("fileJson-->"+modelData.getFileJson());
		}

		if(log.infoEnabled){
			log.info("file should fetch size:"+fileList.size());
		}
		long timeoutSart
		long start = System.currentTimeMillis()
		long fileTotalSize = 0
		def fetchFileCount = 0
		def fetchTempList = fileList
		def tempFileLength = 0
		String fetchFilePath
		boolean fetchSuccessTag  = false
		int retryTimes = 0
		for(int i=0;i<fetchTempList.size();i++){
			retryTimes = 0
			fetchSuccessTag =false
			while(retryTimes<RETRY_TIMES&&fetchSuccessTag==false){
				try{
					if((System.currentTimeMillis()-timeoutSart)>EXPIRE_TIME){
						baseUrl = genBaseUrl(caseId);
						timeoutSart  = System.currentTimeMillis()
					}
					fetchFilePath = fetchTempList[i]
					tempFileLength = fetchFile(fileSaveDir,baseUrl,fetchFilePath);
					fileTotalSize = tempFileLength+fileTotalSize
					if(log.infoEnabled){
						log.info("fetch file "+fetchFileCount+"  finish")
					}
					fetchFileCount ++ ;
					modelData.setFileFetchedCount(fetchFileCount)
					modelData.setCurrentFetchFileKey(fetchFilePath)
					modelData = modelDataRepository.saveAndFlush(modelData);
					fetchSuccessTag = true;
				}catch(e){
					log.error("fetch file",e);
					if((e instanceof java.net.UnknownHostException|| e instanceof  java.net.SocketTimeoutException || e instanceof java.net.SocketException) && retryTimes < RETRY_TIMES){
						Thread.sleep(3000);
						if(retryTimes==0){
							i--
						}
						retryTimes++;
						log.error("fetch file {} retry ${RETRY_TIMES} times current is  {}",fetchFilePath,retryTimes);
					}else{
						fetchFileCount ++
						modelData.setFetchStatus(FetchStatus.ERROR);
						modelData.setFetchErrorMsg("抓取${fetchFilePath}时出错 retryTimes ${retryTimes}，"+e.getMessage());
						modelDataRepository.save(modelData);
						log.error("fetch file   return--",e);
						return
					}
				}
			}
		}
		if(log.infoEnabled){
			log.info("file  fetch size:"+fetchFileCount);
		}
		modelData.setFileFetchedCount(fetchFileCount)
		long useTime = (long)((System.currentTimeMillis()-start)/1000)
		modelData.setUserTimeSec(useTime);
		modelData.setFileTotalSize(fileTotalSize)
		modelData.setFetchStatus(FetchStatus.FINISH)
		modelData = modelDataRepository.save(modelData);
	}

	def fetchFile(String fileSaveDir,String baseUrl,fetchFilePath){
		/**
		 * time out处理，amazon cdn超时后会报403错误
		 **/
		Response  resultFile
		File file
		byte[] tempBytes
		def result = 0
		
		// output here
		file = new File(fileSaveDir+fetchFilePath)
		
		if(!file.exists()){
			def fetchUrl  = baseUrl.replace("{{filename}}", fetchFilePath)
			log.info("fetchUrl:"+fetchUrl)
			resultFile = Jsoup.connect(fetchUrl).ignoreContentType(true).timeout(300000)
					.userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0").execute();
			tempBytes= resultFile.bodyAsBytes()
			FileUtils.writeByteArrayToFile(file, tempBytes)
			result = tempBytes.length
		}
		return result
	}

	public  JSONObject genFileJson(String caseId){
		def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
		String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
		return  JSON.parseObject(body);
	}

	public  String genBaseUrl(String caseId){
		def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
		String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
		return  JSON.parseObject(body).get("base.url");
	}

	public  genModelData(String caseId,ModelData modelData){
		def url = "https://my.matterport.com/show/?m=${caseId}"
		Document doc = Jsoup.parse(new URL(url), 30000);
		// 取得所有的script tag
		Elements eles = doc.getElementsByTag("script");
		for (Element ele : eles) {
			//取出需要的变量
			String script = ele.toString();
			if (script.indexOf("window.MP_PREFETCHED_MODELDATA") > -1) {
				// 只取得script的內容
				script = ele.childNode(0).toString();
				modelData.setModelData(script);
			}
		}
		modelData.setDescription(doc.getElementById("meta-description").toString());
		modelData.setTitle(doc.title());
	}

}
