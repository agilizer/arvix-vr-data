package cn.arvix.vrdata.service

import java.beans.BeanInfo
import java.beans.IntrospectionException
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileProgressBody
import org.apache.http.entity.mime.content.FileProgressListenDefault
import org.apache.http.entity.mime.content.FileProgressListenInter
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskRejectedException
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service

import cn.arvix.vrdata.consants.ArvixDataConstants
import cn.arvix.vrdata.domain.ModelData
import cn.arvix.vrdata.domain.SyncTaskContent
import cn.arvix.vrdata.domain.SyncTaskContent.TaskLevel
import cn.arvix.vrdata.repository.ConfigDomainRepository
import cn.arvix.vrdata.repository.ModelDataRepository
import cn.arvix.vrdata.repository.SyncTaskContentRepository
import cn.arvix.vrdata.util.AntZipUtil
import cn.arvix.vrdata.util.StaticMethod

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 * Created by wanghaiyang on 16/4/26.
 */
@Service
public class UploadDataServiceImpl implements UploadDataService {
	private static final Logger log = LoggerFactory
	.getLogger(UploadDataServiceImpl.class);
	@Autowired
	private ConfigDomainService configDomainService;
	@Autowired
	private ModelDataRepository modelDataRepository;
	@Autowired
	JpaShareService jpaShareService;
	@Autowired
	private ConfigDomainRepository configDomainRepository;
	@Autowired
	private SyncTaskContentRepository syncTaskContentRepository;
	@Autowired
	private UserService userService;
	@Autowired
	@Qualifier("syncTaskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private SyncTaskContentService syncTaskContentService;
	@Autowired
	MessageBroadcasterService messageBroadcasterService
	private static final String ERROR = "Msg";
	private static final String STATUS = "Status";
	public static final String urlSep = "\\n";
	public static final String curlSep = "\n";
	private static ConcurrentHashMap<String, String> caseMap = new ConcurrentHashMap<>();

	public Map<String, Object> uploadData(SyncTaskContent syncTaskContent) {
		String sourceUrl = syncTaskContent.getSourceUrl()
		String dstUrl = syncTaskContent.getDstUrl()
		Map<String, Object> result = StaticMethod.getResult()
		TaskLevel taskLevel = syncTaskContent.getTaskLevel()
		StringBuilder stringBuilder = getErrorMsg(result);
		def caseId = syncTaskContent.getCaseId();
		ModelData modelData = modelDataRepository.findByCaseId(caseId);
		if (modelData != null) {
			String tmpModelData = modelData.getModelData();
			//替换成目标服务器域名
			String dstServerDomain = dstUrl.replace("api/v1/updateModelData", "")
			modelData.setModelData(tmpModelData.replaceAll(FetchDataServiceImpl.SERVER_URL, dstServerDomain));
		}
		//result.put("script", modelData.getModelData());
		if (modelData == null) {
			stringBuilder.append("数据库中不存在此数据: " + sourceUrl);
			syncTaskContentService.failed(syncTaskContent, stringBuilder.toString());
			log.warn("数据库中不存在此数据: " + sourceUrl);
		} else {
			//判断目标服务器是否存在
			if (modelExits(dstUrl, caseId)) {
				stringBuilder.append("目标服务器已存在此数据: " + sourceUrl);
				syncTaskContentService.failed(syncTaskContent, stringBuilder.toString());
				log.warn("目标服务器已存在此数据: " + sourceUrl);
			} else {
				if (caseMap.contains(caseId)) {
					stringBuilder.append("正在同步数据: " + sourceUrl + ", 请勿重复操作.");
					syncTaskContentService.failed(syncTaskContent, stringBuilder.toString());
					return result;
				}
				UploadDataServiceImpl uploadDataService = this;
				Runnable worker = new Runnable() {
							public void run() {
								caseMap.put(caseId, caseId);
								try {
									uploadDataService.upload( modelData, syncTaskContent);
								} catch(Exception e){
									log.error("upload failed",e)
									syncTaskContentService.failed(syncTaskContent, e.getMessage());
								}finally {
									uploadDataService.clearCaseMap(caseId);
								}
							}
				};
				try {
					if (syncTaskContent.taskLevel == SyncTaskContent.TaskLevel.HIGH) {
						new Thread(worker).start();
					} else {
						taskExecutor.execute(worker);
					}
					result.put(STATUS, 1);
					stringBuilder.append("正在同步数据: " + sourceUrl);
					log.info("正在同步数据: " + sourceUrl);
					syncTaskContent.setWorking(true);
					syncTaskContent.setTaskStatus(SyncTaskContent.TaskStatus.WORKING);
				} catch (TaskRejectedException e) {
					syncTaskContentService.failed(syncTaskContent, "任务队列已满!!!!!!!");
				}
			}
		}
		return result;
	}


	private void clearCaseMap(String caseId) {
		caseMap.remove(caseId);
	}

	private StringBuilder getErrorMsg(Map<String, Object> result) {
		StringBuilder stringBuilder = result.get(ERROR);
		if (stringBuilder == null) {
			stringBuilder = new StringBuilder();
			result.put(ERROR, stringBuilder);
		}
		return stringBuilder;
	}

	private boolean modelExits(String dstUrl, String caseId) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		dstUrl = dstUrl.replace("updateModelData", "isExist/" + caseId);
		HttpGet httpGet = new HttpGet(dstUrl);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		HttpEntity resEntity = response.getEntity();
		String text = IOUtils.toString(resEntity.getContent());
		return Boolean.valueOf(text);
	}

	private void upload( ModelData modelData, SyncTaskContent syncTaskContent) {
		String dstUrl = syncTaskContent.getDstUrl();
		String sourceUrl = syncTaskContent.getSourceUrl()
		TaskLevel taskLevel = syncTaskContent.getTaskLevel()
		String caseId = syncTaskContent.getCaseId();
		String apiKey = configDomainService.getConfig(ArvixDataConstants.API_UPLOAD_MODELDATA_KEY);
		
		log.info("upload start..,serverUrl:{}",dstUrl);
		messageBroadcasterService.send("开始同步... : " + caseId);
		Map<String, String> params = toMapValueString(modelData);
		params.put("apiKey",apiKey);
		params.put("modelDataClient", params.get("modelData"));
		params.remove("modelData");
		/**
		 * 调用接口, Spring String 转 Calendar失败 ???
		 */
		//TODO: Spring String 转 Calendar失败
		params.remove("lastUpdated");
		params.remove("dateCreated");

		log.info("params:{}",params);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(dstUrl);
			String filePath = configDomainService.getConfig(ArvixDataConstants.FILE_STORE_PATH);
			if (!filePath.endsWith("/")) {
				filePath += "/";
			}
			String zipFilePath = filePath+caseId + ".zip"
			File zipFile = new File(zipFilePath);
			if(!zipFile.exists()){
				String fileSaveDir = filePath+caseId
				log.info("fileSaveDir-->"+fileSaveDir);
				log.info("zipFilePath-->"+zipFilePath);
				try{
				   messageBroadcasterService.send(caseId+"正在压缩文件....");
					AntZipUtil.writeByApacheZipOutputStream(fileSaveDir,zipFilePath,caseId)
				}catch(e){
					log.error("zip error",e);
					messageBroadcasterService.send(caseId+"zip error: " + e.getMessage());
					syncTaskContentService.failed(syncTaskContent, "zip error: " + e.getMessage());
				}
			}
			FileProgressBody zipFileData = new FileProgressBody(new File(zipFilePath));
			FileProgressListenInter progressListen = new FileProgressListenDefault(messageBroadcasterService,syncTaskContent);
			zipFileData.setFileProgressListenInter(progressListen);
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
					.addPart("zipFileData", zipFileData)
			StringBody stringBody = null;
			params.each{key,value->
				if(value&&value!=''){
					stringBody = new StringBody(value, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart(key,stringBody);
					log.info("add key:{},value:{}",key,value);
				}
			}
			CloseableHttpResponse response = null;
			try {
				HttpEntity reqEntity = multipartEntityBuilder.build();
				httppost.setEntity(reqEntity);
				log.info("executing request " + httppost.getRequestLine());
				response = httpclient.execute(httppost);
				messageBroadcasterService.send(caseId+"成功发送同步请求！" + response.getStatusLine().toString())
				log.info(response.getStatusLine().toString());
				int code = response.getStatusLine().getStatusCode()
				HttpEntity resEntity = response.getEntity();
				String text = IOUtils.toString(resEntity.getContent());
				log.info(text);
				if(code == 200){
					if (resEntity != null) {
						log.info("Response content length: " + resEntity.getContentLength());
						JSONObject jsonObject = JSON.parseObject(text);
						if(jsonObject.getBoolean("success")){
							messageBroadcasterService.send(modelData.getCaseId()+" 数据上传成功，请访问服务器地址查看结果！");
							syncTaskContentService.finish(syncTaskContent);
						}else{
							if(jsonObject.getString("errorCode")=="exist"){
								messageBroadcasterService.send(caseId+"服务器已经存在相同的数据，请不要重复上传!");
								syncTaskContentService.failed(syncTaskContent, " 服务器已经存在相同的数据，请不要重复上传!");
							}
							else{
								messageBroadcasterService.send(caseId + " 数据上传失败，请联系管理员，返回结果为:\n"+text)
								syncTaskContentService.failed(syncTaskContent,  " 数据上传失败，请联系管理员，返回结果为:\n"+text);
							}
						}
					}
				} else {
					if (code == 403) {
						messageBroadcasterService.send(caseId +"apiKey不正确！")
					}
					if (code == 404) {
						messageBroadcasterService.send(caseId +"服务器地址不正确！")
					} else {
						messageBroadcasterService.send(caseId +"同步异常！")
					}
				 	syncTaskContentService.failed(syncTaskContent, "同步失败  403 or 404 or 同步异常");
				}
				EntityUtils.consume(resEntity);

			}catch(e){
				log.error("upload failed",e);
				if(e instanceof org.apache.http.conn.HttpHostConnectException){
					messageBroadcasterService.send("数据上传失败,服务器地址不正确，连接失败！caseId: " + caseId)
				}else{
					messageBroadcasterService.send("数据上传失败,请联系管理员:错误信息 " + e + ", caseId: " + caseId)
				}
			 	syncTaskContentService.failed(syncTaskContent, "数据上传失败"+caseId);
			} finally {
				zipFile.deleteOnExit();
				if (response != null) {
					response.close();
				}
			}
		} finally {
			if (httpclient != null) {
				httpclient.close();
			}
		}

	}

	public static final Map<String, String> toMapValueString(Object bean)
	throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Map<String, String> returnMap = new HashMap<String, String>();
		BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result.toString());
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

}
