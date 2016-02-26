package cn.arvix.matterport.client

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

public class FetchServiceImpl implements FetchService{
	private static final Logger log = LoggerFactory
	.getLogger(FetchServiceImpl.class);
	def static Long EXPIRE_TIME = 240000
	UploadDataService uploadDataService;
	def RETRY_TIMES = 3;
	Map FETCH_MAP = [:];
	@Override
	public void fetchData(String serverUrl, String apiKey, String workDir, String[] fetchUrlArray) {
		fetchUrlArray.each {
			fetchData(serverUrl,apiKey,workDir,it)
		}
	}
	
	public void fetchData(String serverUrl, String apiKey, String workDir, String sourceUrl) {
		if(sourceUrl){
			def subIndex = sourceUrl.indexOf("?m=");
			if(FETCH_MAP.containsKey(sourceUrl)){
				UILog.getInstance().log("正在抓取${sourceUrl}，请不要重复提交！");
				return;
			}
			if(subIndex>0){
				FETCH_MAP.put(sourceUrl,sourceUrl)
				def caseId = sourceUrl.substring(subIndex+3)
				ModelDataClient modelData = new ModelDataClient();
				def calendar = Calendar.getInstance()
				modelData.setCaseId(caseId);
				modelData.setSourceUrl(sourceUrl);
				def fileSaveDir = workDir +caseId+"/"
				try{
					Thread.start {
						try{
							genModelData(caseId,modelData,fileSaveDir);
						}catch(e){
							log.error("fetch caseId {} genModelData error:",caseId,e);
							UILog.getInstance().log("正在抓取${sourceUrl}数据出错，请检查网络后重试！");
						}
						genFiles(caseId,fileSaveDir,modelData,serverUrl,apiKey);
						FETCH_MAP.remove(sourceUrl);
						if(FETCH_MAP.size()==0){
							MainJFrame.setWorkFlag(false);
						}
					}
				}catch(e){
					log.error("fetch caseId {} genFiles error:",caseId,e)
				}
			}else{
				UILog.getInstance().log("源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
				log.warn("源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
			}
		}else{
			UILog.getInstance().log("源url不能为空");
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
					UILog.getInstance().log("抓取第 "+fetchFileCount+"/"+fileSumCount+" 文件完成，caseId:${caseId}");
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
	
	
	public  genModelData(String caseId,ModelDataClient modelData,String fileSaveDir){
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
				
				script = script.replace("window.MP_PREFETCHED_MODELDATA = ", "")
				script =script.substring(0,script.length()-1)
				JSONObject object = JSON.parseObject(script);
				
				def objectModel = object.getJSONObject("model")
				JSONArray objectAr = objectModel.getJSONArray("images")
				if(objectAr!=null&&objectAr.size()>0){
					def saveDir  = fileSaveDir+"playerImages/"
					def fetchFileKeyList = ["signed_src","download_url","src","thumbnail_signed_src"]
					def sumPlayImage = objectAr.size()*4
					UILog.getInstance().log("发现自动播放,共要抓取 "+sumPlayImage+" 个文件");
					def activeReel = []
					def showIndex = 1
					objectAr.each  {jsonObject->
						//{"reel": [{"sid": "3UbRzyE9ic3"}, {"sid": "mEEbq29Eqku"}, {"sid": "rAhHyo6oj5h"}, {"sid": "9qtbuZWrZMr"}, {"sid": "KSo6NahgXpA"}, {"sid": "W3VgF9nTG9n"}, {"sid": "W9yF5jfbjUK"}, {"sid": "weQBQpVU7Bz"}, {"sid": "Ai5Mc2GUfJ6"}, {"sid": "zG4bFnVegiA"}]}
						activeReel.add(["sid":jsonObject.get("sid")])
						fetchFileKeyList.each{fileUrlKey->
							fetchPlayerImage(saveDir,caseId,jsonObject,fileUrlKey)
							UILog.getInstance().log("自动播放文件${showIndex}/${sumPlayImage}完成");
							showIndex++;
						}
					}
					modelData.setActiveReel(JSON.toJSONString(["reel":activeReel]))
					modelData.setModelDataClient("window.MP_PREFETCHED_MODELDATA = "+JSON.toJSONString(object));
				}
			}
		}
		modelData.setDescription(doc.getElementById("meta-description").text());
		modelData.setTitle(doc.title().replace("Matterport 3D Showcase",""));
	}
	
	private void fetchPlayerImage(String saveDir,String caseId,JSONObject sourceObject,String filePathKey){
		if(filePathKey){
			def fileName = sourceObject.get("sid")+".jpg"
			def file = new File(saveDir,fileName)
			if(!file.exists()){
				Response resultFile = Jsoup.connect(sourceObject.get(filePathKey)).ignoreContentType(true).timeout(300000).execute();
				// output here
				if(!file.exists()){
					FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
				}
				sourceObject.put(filePathKey,ClientStaticVar.SERVER_URL+"upload/playerImage/"+caseId+"/"+fileName)
			}
		}
	}
	
	public void setUploadDataService(UploadDataService uploadDataService){
		this.uploadDataService =  uploadDataService;
	}

}
