package cn.arvix.matterport.client

import java.security.spec.ECField;

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

public class FetchServiceImpl implements FetchService{
	private static final Logger log = LoggerFactory
	.getLogger(FetchServiceImpl.class);
	def static Long EXPIRE_TIME = 240000
	UploadDataService uploadDataService;
	def RETRY_TIMES = 3;
	def FETCH_MAP = [:];
	@Override
	public void fetchData(String serverUrl, String apiKey, String workDir, String[] fetchUrlArray) {
		fetchUrlArray.each {
			fetchData(serverUrl,apiKey,workDir,it)
		}
	}
	
	public void fetchData(String serverUrl, String apiKey, String workDir, String sourceUrl) {
		if(sourceUrl){
			def subIndex = sourceUrl.indexOf("?m=");
			if(subIndex>0){
				FETCH_MAP.put(sourceUrl,sourceUrl)
				def caseId = sourceUrl.substring(subIndex+3)
				ModelDataClient modelData = new ModelDataClient();
				def calendar = Calendar.getInstance()
				modelData.setCaseId(caseId);
				modelData.setSourceUrl(sourceUrl);
				def fileSaveDir = workDir +caseId+"/"
				try{
					genModelData(caseId,modelData);
				}catch(e){
					log.error("fetch caseId {} genModelData error:",caseId,e)
				}
				try{
					Thread.start {
						genFiles(caseId,fileSaveDir,modelData,serverUrl,apiKey);
						MainJFrame.setWorkFlag(false);
					}
				}catch(e){
					log.error("fetch caseId {} genFiles error:",caseId,e)
				}
			}else{
				log.warn("源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
			}
		}else{
			log.warn("源url不能为空");
		}
	} 
	
	
	public  void genFiles(String caseId,String fileSaveDir,ModelDataClient modelData,String serverUrl,String apiKey){
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
		def newUrl = serverUrl+"files/${caseId}/{{filename}}";
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
					UILog.getInstance().log("抓取第 "+fetchFileCount+"/"+fileSumCount+" 文件完成");
					fetchFileCount ++ ;
					modelData.setFileFetchedCount(fetchFileCount)
					modelData.setCurrentFetchFileKey(fetchFilePath)
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
						modelData.setFetchErrorMsg("抓取${fetchFilePath}时出错 retryTimes ${retryTimes}，"+e.getMessage());
						UILog.getInstance().log("抓取${fetchFilePath}时出错 retryTimes ${retryTimes}，"+"请开启VPN!");
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
		String zipFilePath = fileSaveDir.substring(0,fileSaveDir.length()-1)+".zip"
		log.info("fileSaveDir-->"+fileSaveDir);
		log.info("zipFilePath-->"+zipFilePath);
		try{
			UILog.getInstance().log("正在压缩文件....");
			AntZipUtil.writeByApacheZipOutputStream(fileSaveDir,zipFilePath,caseId)
			UILog.getInstance().log("上传文件....");
			uploadDataService.uploadData(serverUrl,apiKey,zipFilePath,modelData)
		}catch(e){
		     log.error("zip error",e);
		}
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
	
	
	public  genModelData(String caseId,ModelDataClient modelData){
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
				modelData.setModelDataClient(script);
			}
		}
		modelData.setDescription(doc.getElementById("meta-description").toString());
		modelData.setTitle(doc.title());
	}
	
	public void setUploadDataService(UploadDataService uploadDataService){
		this.uploadDataService =  uploadDataService;
	}

}
