package cn.arvix.vr.client;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
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

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

public class UploadDataServiceImpl implements UploadDataService{
	private static final Logger log = LoggerFactory
			.getLogger(UploadDataServiceImpl.class);
	@Override
	public void uploadData(String serverUrl, String apiKey, String filePath, ModelDataClient modelData) {
		log.info("upload start..,serverUrl:{}",serverUrl);
		Map<String, String> params = BeanMapUtils.toMapValueString(modelData);
		params.put("apiKey",apiKey);
		log.info("params:{}",params);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(serverUrl);

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
				UILog.getInstance().log("上传文件完成！");
				log.info(response.getStatusLine().toString());
				int code = response.getStatusLine().getStatusCode()
				HttpEntity resEntity = response.getEntity();
				if(code == 200){
					if (resEntity != null) {
						log.info("Response content length: " + resEntity.getContentLength());
						String text = IOUtils.toString(resEntity.getContent());
						log.info(text);
						JSONObject jsonObject = JSON.parseObject(text);
						if(jsonObject.getBoolean("success")){
							UILog.getInstance().log(modelData.getCaseId()+" 数据上传成功，请访问服务器地址查看结果！");
						}else{
							if(jsonObject.getString("errorCode")=="exist"){
								UILog.getInstance().log(modelData.getCaseId()+" 服务器已经存在相同的数据，请不要重复上传!");
							}
							else{
								UILog.getInstance().log(modelData.getCaseId() + " 数据上传失败，请联系管理员，返回结果为:\n"+text);
							}
						}
					}
				}
				if(code == 403){
					UILog.getInstance().log("apiKey不正确！");
					JOptionPane.showMessageDialog(null, "apiKey不正确！","错误", JOptionPane.ERROR_MESSAGE);
				}
				if(code == 404){
					UILog.getInstance().log("服务器地址不正确！");
					JOptionPane.showMessageDialog(null, "服务器地址不正确！","错误", JOptionPane.ERROR_MESSAGE);
				}
				EntityUtils.consume(resEntity);
				
			}catch(e){
				log.error("upload failed",e);
				if(e instanceof org.apache.http.conn.HttpHostConnectException){
					UILog.getInstance().log("数据上传失败,服务器地址不正确，连接失败！");
					JOptionPane.showMessageDialog(null, "数据上传失败,服务器地址不正确，连接失败！ ","错误", JOptionPane.ERROR_MESSAGE);
				}else{
					UILog.getInstance().log("数据上传失败,请联系管理员");
				}
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		
	}

}
