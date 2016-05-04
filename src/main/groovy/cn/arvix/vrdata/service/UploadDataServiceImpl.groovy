package cn.arvix.vrdata.service

import cn.arvix.vrdata.been.Status
import cn.arvix.vrdata.consants.ArvixDataConstants
import cn.arvix.vrdata.domain.ConfigDomain
import cn.arvix.vrdata.domain.ModelData
import cn.arvix.vrdata.repository.ConfigDomainRepository
import cn.arvix.vrdata.repository.ModelDataRepository
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileProgressBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.beans.BeanInfo
import java.beans.IntrospectionException
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

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
    private static final String ERROR = "Msg";
    private static final String STATUS = "Status";
    public static final String urlSep = "\\n";
    public static final String curlSep = "\n";
    public static final String UPDATE_PREFIX = "UPDATE-SERVICE-";
    private static ConcurrentHashMap<String, String> caseMap = new ConcurrentHashMap<>();
    // caseId --> status
    private static ConcurrentHashMap<String, Status> deferedMessage = new ConcurrentHashMap<String, Status>();

    /**
     * @Param sourceUrl 包含caseId的url
     * @Param dstUrl 上传目标服务器的url
     */
    public Map<String, Object> uploadData(String sourceUrl, String dstUrl, Status status = null) {
        Map<String, Object> result = new HashMap<>();
        result.put(STATUS, -1);
        if (sourceUrl.contains(curlSep)) {
            String[] serverUrls = sourceUrl.split(urlSep);
            if (serverUrls == null || serverUrls.length == 0) {
                result.put(ERROR, "Empty sourceUrl.");
            } else {
                //多个sourceUrl放入configDomain中,依次抓取
                for (String surl : serverUrls) {
                    surl = surl.trim();
                    if (surl != "") {
                        addUrlToConfig(surl, dstUrl);
                    }
                }
                for (String surl : serverUrls) {
                    if (surl != "") {
                        uploadData(surl, dstUrl, result, status);
                    }
                }
            }
        } else {
            addUrlToConfig(sourceUrl, dstUrl);
            uploadData(sourceUrl, dstUrl, result, status);
        }

        return result;
    }

    public Map<String, Object> uploadData(String sourceUrl, String dstUrl, Map<String, Object> result, Status status = null) {

        StringBuilder stringBuilder = getErrorMsg(result);
        def subIndex = sourceUrl.indexOf("?m=");
        if (subIndex < 0) {
            stringBuilder.append("名称不合法: " + sourceUrl);
            log.warn("名称不合法: " + sourceUrl);
            //校验未通过,移除记录
            removeConfig(sourceUrl);
        } else {
            def caseId = sourceUrl.substring(subIndex + 3).trim();
            ModelData modelData = modelDataRepository.findByCaseId(caseId);
            if (modelData != null) {
                String tmpModelData = modelData.getModelData();
                //替换成目标服务器域名
                String dstServerDomain = dstUrl.replace("admin/updateModelData", "")
                modelData.setModelData(tmpModelData.replaceAll(FetchDataServiceImpl.SERVER_URL, dstServerDomain));
            }
            //result.put("script", modelData.getModelData());
            if (modelData == null) {
                stringBuilder.append("数据库中不存在此数据: " + sourceUrl);
                log.warn("数据库中不存在此数据: " + sourceUrl);
                //校验未通过,移除记录
                removeConfig(sourceUrl);
            } else {
                //判断目标服务器是否存在
                if (modelExits(dstUrl, caseId)) {
                    stringBuilder.append("目标服务器已存在此数据: " + sourceUrl);
                    log.warn("目标服务器已存在此数据: " + sourceUrl);
                    //校验未通过,移除记录
                    removeConfig(sourceUrl);
                } else {
                    String filePath = configDomainService.getConfig(ArvixDataConstants.FILE_STORE_PATH);
                    if (!filePath.endsWith("/")) {
                        filePath += "/";
                    }
                    filePath += caseId + ".zip";
                    File rootFile = new File(filePath);
                    if (caseMap.contains(caseId)) {
                        stringBuilder.append("正在同步数据: " + sourceUrl + ", 请勿重复操作.");
                        return result;
                    }
                    if (rootFile.exists() && rootFile.isFile()) {
                        String apiKey = configDomainService.getConfig(ArvixDataConstants.API_UPLOAD_MODELDATA_KEY);
                        Thread worker = Thread.start {
                            caseMap.put(caseId, caseId);
                            try {
                                upload(dstUrl, apiKey, filePath, modelData, caseId, sourceUrl, status);
                            } finally {
                                clearCaseMap(caseId);
                            }
                        }
                        result.put(STATUS, 1);
                        result.put("WORKER", worker);
                        stringBuilder.append("正在同步数据: " + sourceUrl);
                    } else {
                        stringBuilder.append("服务器上不存在此数据: " + sourceUrl);
                        log.warn("服务器上不存在此数据: " + sourceUrl);
                        //校验未通过,移除记录
                        removeConfig(sourceUrl);
                    }
                }
            }
        }
        return result;
    }

    def uploadFileRecursive(File rootFile,def method) {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String filePath = file.getCanonicalPath();
                method(filePath);
            } else if (file.isDirectory()) {
                uploadFileRecursive(file, method);
            }
        }
    }

    public Status getCaseStatus(String caseId) {
        return deferedMessage.get(caseId.trim());
    }

    private Status getStatus(String caseId, Status status = null) {
        if (status == null) {
            status = deferedMessage.get(caseId);
            if (status == null) {
                status = new Status();
                deferedMessage.put(caseId, status);
            }
        }
        return status;
    }

    private void clearStatus(String caseId) {
        deferedMessage.remove(caseId);
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

    private void addUrlToConfig(String sourceUrl, String dstUrl) {
        if (sourceUrl == null || dstUrl == null) {
            return;
        }
        sourceUrl = sourceUrl.trim();
        dstUrl = dstUrl.trim();
        def configDomain = new ConfigDomain()
        configDomain.setEditable(true)
        configDomain.setDescription("fetch source url")
        configDomain.setMapName(UPDATE_PREFIX + sourceUrl)
        //记录自动同步信息
        configDomain.setMapValue(sourceUrl + "|" + dstUrl);
        configDomain.setValueType(ConfigDomain.ValueType.String);
        //config在服务器启动时加载完成
        try {
            configDomainRepository.saveAndFlush(configDomain)
        } catch (Exception e) {
            //Duplicate entry, ignore
        }
    }

    public void removeConfig(String sourceUrl) {
        String hql = "delete from ConfigDomain c where c.mapName=:key ";
        jpaShareService.updateForHql(hql, ["key" : UPDATE_PREFIX + sourceUrl.trim()]);
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

    private void upload(String dstUrl, String apiKey, String filePath, ModelData modelData, String caseId, String sourceUrl, Status status = null) {
        clearStatus(caseId);
        if (status == null) {
            status = getStatus(caseId);
        }
        log.info("upload start..,serverUrl:{}",dstUrl);
        status.addMessage("开始同步... : " + caseId);
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

            FileProgressBody zipFileData = new FileProgressBody(new File(filePath));
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
                status.addMessage("成功发送同步请求！" + response.getStatusLine().toString())
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
                            status.addMessage(modelData.getCaseId()+" 数据上传成功，请访问服务器地址查看结果！")
                            //同步完成,从configDomain中移除记录
                            removeConfig(sourceUrl);
                        }else{
                            if(jsonObject.getString("errorCode")=="exist"){
                                status.addMessage(modelData.getCaseId()+" 服务器已经存在相同的数据，请不要重复上传!")
                                removeConfig(sourceUrl);
                            }
                            else{
                                status.addMessage(modelData.getCaseId() + " 数据上传失败，请联系管理员，返回结果为:\n"+text)
                            }
                        }
                    }
                } else {
                    if (code == 403) {
                        status.addMessage("apiKey不正确！")
                    }
                    if (code == 404) {
                        status.addMessage("服务器地址不正确！")
                    } else {
                        status.addMessage("同步异常！")
                    }
                }
                EntityUtils.consume(resEntity);

            }catch(e){
                log.error("upload failed",e);
                if(e instanceof org.apache.http.conn.HttpHostConnectException){
                    status.addMessage("数据上传失败,服务器地址不正确，连接失败！")
                }else{
                    status.addMessage("数据上传失败,请联系管理员:错误信息 " + e)
                }
            } finally {
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
