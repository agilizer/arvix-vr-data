package cn.arvix.vrdata.service

import org.apache.commons.io.FileUtils
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskRejectedException
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service

import cn.arvix.vrdata.bootstrap.AutoSyncAndUpdateTask
import cn.arvix.vrdata.consants.ArvixDataConstants
import cn.arvix.vrdata.domain.ModelData
import cn.arvix.vrdata.domain.SyncTaskContent
import cn.arvix.vrdata.domain.SyncTaskContent.TaskLevel
import cn.arvix.vrdata.domain.SyncTaskContent.TaskType
import cn.arvix.vrdata.repository.ConfigDomainRepository
import cn.arvix.vrdata.repository.ModelDataRepository
import cn.arvix.vrdata.repository.SyncTaskContentRepository

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * Created by wanghaiyang on 16/4/26.
 */
@Service
public class FetchDataServiceImpl implements FetchDataService {
    private static final Logger log = LoggerFactory
            .getLogger(FetchDataServiceImpl.class);
    @Autowired
    private ConfigDomainService configDomainService;
    @Autowired
    private ModelDataRepository modelDataRepository;
    @Autowired
    private UploadDataService uploadDataService;
    @Autowired
    private ConfigDomainRepository configDomainRepository;
    @Autowired
    private JpaShareService jpaShareService;
    @Autowired
    private SyncTaskContentRepository syncTaskContentRepository;
	 @Autowired
    private SyncTaskContentService syncTaskContentService;
	@Autowired
	UploadDataService uploadDataService;
    @Autowired
    private UserService userService;
	@Autowired
	MessageBroadcasterService messageBroadcasterService
    @Autowired
    @Qualifier("syncTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private AutoSyncAndUpdateTask autoSyncAndUpdateTask;
    private static final String ERROR = "Msg";
    private static final String STATUS = "Status";
    public static final String SERVER_URL = "http://vr.arvix.cn/";
    def static Long EXPIRE_TIME = 240000
    def RETRY_TIMES = 3;
    Map FETCH_MAP = [:];

    public void removeUrlAndFlagCheck(String url) {
        FETCH_MAP.remove(url)
    }

    public  Map<String, Object> fetchData(SyncTaskContent syncTaskContent) {
		String sourceUrl = syncTaskContent.getSourceUrl();
		String workDir = configDomainService.getConfig(ArvixDataConstants.FILE_STORE_PATH).toString()
		String dstUrl = syncTaskContent.getDstUrl();
		TaskType taskType = syncTaskContent.getTaskType();
		TaskLevel taskLevel = syncTaskContent.getTaskLevel();
		String serverUrl =  SERVER_URL;
		Map<String, Object> result = new HashMap<>();
        StringBuilder errStringBuilder = (StringBuilder)result.get(ERROR);
        if (errStringBuilder == null) {
            errStringBuilder = new StringBuilder();
            result.put(ERROR, errStringBuilder);
        }
        if(sourceUrl){
            def subIndex = sourceUrl.indexOf("?m=");
            def caseId = sourceUrl.substring(subIndex+3).trim()
            if(FETCH_MAP.containsKey(sourceUrl)){
                errStringBuilder.append("正在抓取${sourceUrl}，请不要重复提交！");
                removeUrlAndFlagCheck(sourceUrl)
				syncTaskContentService.failed(syncTaskContent, errStringBuilder.toString());
				if(syncTaskContent.getTaskType()==TaskType.FETCH_UPDATE){
					uploadDataService.uploadData(syncTaskContent);
				}
                return result;
            }
            if(subIndex>0){
                FETCH_MAP.put(sourceUrl,sourceUrl)
                println serverUrl
                ModelData modelDataExit = modelDataRepository.findByCaseId(caseId);
                if(modelDataExit){
                    errStringBuilder.append("服务器已经存在此数据，忽略抓取："+sourceUrl);
                    removeUrlAndFlagCheck(sourceUrl);
					if(syncTaskContent.getTaskType()==TaskType.FETCH_UPDATE){
						uploadDataService.uploadData(syncTaskContent);
					}else{
						syncTaskContentService.failed(syncTaskContent, errStringBuilder.toString());
					}
                    return result;
                }
                ModelData modelData = new ModelData();
                modelData.setCaseId(caseId);
                modelData.setSourceUrl(sourceUrl);
                def fileSaveDir = workDir +caseId+"/"
                try {
					FetchDataServiceImpl fetchDataService = this;
					//线程执行任务代码 start
                    Runnable worker = new Runnable() {
                        public void run() {
                            messageBroadcasterService.send("正在抓取: " + sourceUrl);
                            try {
                                fetchDataService.genModelData(caseId, modelData, fileSaveDir);
                            } catch (e) {
                                log.error("fetch caseId {} genModelData error:", caseId, e);
                                messageBroadcasterService.send("正在抓取${sourceUrl}数据出错，请检查网络后重试！");
                                fetchDataService.removeUrlAndFlagCheck(sourceUrl)
                                syncTaskContentService.failed(syncTaskContent, "请检查网络后重试");
                                return;
                            }
                            try {
                                fetchDataService.genFiles(caseId, fileSaveDir, modelData, serverUrl, syncTaskContent);
                                fetchDataService.removeUrlAndFlagCheck(sourceUrl)
                                //修改数据库set
                                modelData.fetchStatus = ModelData.FetchStatus.FINISH;
                                modelDataRepository.saveAndFlush(modelData);
                                //移除记录
								syncTaskContentService.finish(syncTaskContent);
								//如果任务需要同步,继续同步数据
								if(syncTaskContent.getTaskType()==TaskType.FETCH_UPDATE){
									uploadDataService.uploadData(syncTaskContent);
								}
                            } catch (Exception e) {
								log.error("fetch caseId {} genModelData error:", caseId, e);
                                messageBroadcasterService.send("正在抓取${sourceUrl}数据出: " + e.getMessage() + ", " + e.getCause());
								syncTaskContentService.failed(syncTaskContent, e.getMessage());
                                syncTaskContentRepository.saveAndFlush(syncTaskContent);
                            }                         
                        }
                    }
					//线程执行任务代码 end
                    try {
                        if (syncTaskContent.taskLevel == TaskLevel.HIGH) {
                            new Thread(worker).start();
                        } else {
                            taskExecutor.execute(worker);
                        }
                        result.put(STATUS, 1);
                        errStringBuilder.append("正在抓取: " + sourceUrl);
                        log.info("正在抓取: " + sourceUrl);
                        syncTaskContent.setWorking(true);
                        syncTaskContent.setTaskStatus(SyncTaskContent.TaskStatus.WORKING);
                    } catch (TaskRejectedException e) {
                        result.put(STATUS, 1);
                        errStringBuilder.append("正在排队: " + sourceUrl);
                        log.info("正在排队: " + sourceUrl);
                        syncTaskContent.setTaskStatus(SyncTaskContent.TaskStatus.WAIT);
                        autoSyncAndUpdateTask.addToWait(worker);
                    }
                    syncTaskContentRepository.saveAndFlush(syncTaskContent);
                }catch(e){
                    log.error("fetch caseId {} genFiles error:",caseId,e)
                    removeUrlAndFlagCheck(sourceUrl)
                }
            }else{
                errStringBuilder.append("源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
                log.warn("源url不正确，格式为：https://my.matterport.com/show/?m=MXfJvWQecHT");
                removeUrlAndFlagCheck(sourceUrl)
				syncTaskContentService.failed(syncTaskContent, errStringBuilder.toString());
            }
        }else{
            errStringBuilder.append("源url不能为空");
            log.warn("源url不能为空");
            removeUrlAndFlagCheck(sourceUrl)
			syncTaskContentService.failed(syncTaskContent, errStringBuilder.toString());
        }
        return result;
    }


    public  void genFiles(String caseId,String fileSaveDir,ModelData modelData,String serverUrl, SyncTaskContent syncTaskContent){
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
                    messageBroadcasterService.send("抓取第 "+fetchFileCount+"/"+fileSumCount+" 文件完成，caseId:${caseId}");
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
                        messageBroadcasterService.send("抓取${fetchFilePath}时出错 retryTimes ${retryTimes}，"+"请开启VPN!");
                        log.error("fetch file   return--",e);
                      	syncTaskContentService.failed(syncTaskContent, "抓取${fetchFilePath}时出错 retryTimes ${retryTimes}，"+"请开启VPN!");
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
        messageBroadcasterService.send(caseId+"完成");
    }

    def static fetchFile(String fileSaveDir,String baseUrl,fetchFilePath){
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
			log.info("fetchUrl:{}",fetchUrl);
			if(fetchUrl.indexOf("/2k/")>0){
				log.info("fetchUrl:2k...............");
				return 0
			}
			if(fetchUrl.indexOf("semantic/roomdata.csv")>0){
				log.info("fetchUrl:semantic/roomdata.csv...............");
				return 0
			}
			
			if(fetchUrl.indexOf("tesselate.dam.lzma")>0){
				log.info("fetchUrl:tesselate.dam.lzma...............");
				return 0
			}
			if(fetchUrl.indexOf("tesselate.v2.dam.lzma")>0){
				log.info("fetchUrl:tesselate.v2.dam.lzma...............");
				return 0
			}
			try{
				resultFile = Jsoup.connect(fetchUrl).ignoreContentType(true).timeout(300000).maxBodySize(1024*1024*10*2)
						.userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0").execute();
				tempBytes= resultFile.bodyAsBytes()
				FileUtils.writeByteArrayToFile(file, tempBytes)
				result = tempBytes.length
			}catch(e){
				log.error("error",e)
			}
		}
		return result
    }

    public static JSONObject genFileJson(String caseId){
        def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
        log.info("url:{}",url)
		String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
        return  JSON.parseObject(body);
    }

    public static  String genBaseUrl(String caseId){
        def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
        String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
        return  JSON.parseObject(body).get("base.url");
    }

    public genModelData(String caseId,ModelData modelData,String fileSaveDir){
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
                //modelData.setModelData(script);

                script = script.replace("window.MP_PREFETCHED_MODELDATA = ", "")
                script =script.substring(0,script.length()-1)
                JSONObject object = JSON.parseObject(script);

                def objectModel = object.getJSONObject("model")
                JSONArray objectAr = objectModel.getJSONArray("images")
                if(objectAr!=null&&objectAr.size()>0){
                    def saveDir  = fileSaveDir+"playerImages/"
                    def fetchFileKeyList = ["signed_src","download_url","src","thumbnail_signed_src"]
                    def sumPlayImage = objectAr.size()*4
                     messageBroadcasterService.send("发现自动播放,共要抓取 "+sumPlayImage+" 个文件,caseId: " + caseId);
                    def activeReel = []
                    def showIndex = 1
                    objectAr.each  {jsonObject->
                        //{"reel": [{"sid": "3UbRzyE9ic3"}, {"sid": "mEEbq29Eqku"}, {"sid": "rAhHyo6oj5h"}, {"sid": "9qtbuZWrZMr"}, {"sid": "KSo6NahgXpA"}, {"sid": "W3VgF9nTG9n"}, {"sid": "W9yF5jfbjUK"}, {"sid": "weQBQpVU7Bz"}, {"sid": "Ai5Mc2GUfJ6"}, {"sid": "zG4bFnVegiA"}]}
                        activeReel.add(["sid":jsonObject.get("sid")])
                        fetchFileKeyList.each{fileUrlKey->
                            fetchPlayerImage(saveDir,caseId,jsonObject,fileUrlKey)
                            messageBroadcasterService.send("自动播放文件${showIndex}/${sumPlayImage}完成,caseId: " + caseId);
                            showIndex++;
                        }
                    }
                    modelData.setActiveReel(JSON.toJSONString(["reel":activeReel]))
                }
				modelData.setModelData("window.MP_PREFETCHED_MODELDATA = "+JSON.toJSONString(object));
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
                //System.err.println("----------------" + sourceObject.get(filePathKey));
                Connection.Response resultFile = Jsoup.connect(sourceObject.get(filePathKey)).ignoreContentType(true).timeout(300000).execute();
                // output here
                if(!file.exists()){
                    FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
                }
            }
            ///upload/playerImages/ZujWX1srahK/playerImages/
			// TODO  存储播放图片到分布式文件系统
            sourceObject.put(filePathKey, SERVER_URL+"upload/playerImages/"+caseId+"/playerImages/"+fileName)
        }
    }
}
