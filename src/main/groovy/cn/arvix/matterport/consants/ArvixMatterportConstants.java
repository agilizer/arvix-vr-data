package cn.arvix.matterport.consants;

public interface ArvixMatterportConstants {
	
	String ERROR_MSG = "errorMsg";
	String SUCCESS="success";
	String DATA = "data";
	/**
	 * 短信替换
	 */
	String SMS_VAR_CHECK_CODE="##smcCode##";
	
	String API_UPLOAD_MODELDATA_KEY = "apiUploadModelDataKey";
	/**
	 * 注册
	 */
	String SMS_REGISTER_NOTE="您好!您的注册验证码是:##smcCode##";  	
	/**
	 * 重置密码
	 */
	String SMS_FINDPW_NOTE="您好!您重置密码的验证码是:##smcCode##";
	/**
	 * 存入到session时用到的key
	 */
	String SMS_CODE_SESSION_NAME = "smsCode";
	/**
	 * 验证码长度
	 */
	int DEFAULT_LENGTH = 6;
	/**
	 * 验证码随机在（0-9）里取
	 */
	String DEFAULT_CAPTCHA_CHARS = "0123456789";
	
	
	String FILE_STORE_PATH = "fileStroePath";
	
	String NGINX_URL = "ngnixUrl";
	
	String NGINX_FILE_PATH = "nginxPath";
	
	String SITE_URL = "siteUrl";
	
	String TEAM_DESCRIPTION = "teamDescription";
	
	String ERROR_CODE_EXIST = "exist";
	String ERROR_CODE = "errorCode";
}
