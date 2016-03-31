package cn.arvix.vrdata.util;

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import cn.arvix.vrdata.consants.ArvixMatterportConstants
import cn.arvix.vrdata.domain.ConfigDomain

public class StaticMethod {
	public static Map<String,Object> getResult(){
		Map<String,Object> result = new HashMap<String,Object>(); 
		result.put(ArvixMatterportConstants.SUCCESS, false);
		return result;
	}
	public static String genUUID(){
		return  UUID.randomUUID().toString().replaceAll("-", "");
	}
	public static Map addToConfigMap(Map configMap, ConfigDomain configDomain) {
		if (configDomain.valueType == ConfigDomain.ValueType.Integer) {
			configMap.put(configDomain.mapName, Integer.parseInt(configDomain.mapValue))
		} else if(configDomain.valueType == ConfigDomain.ValueType.Boolean){
			if(configDomain.mapValue=="true"){
				configMap.put(configDomain.mapName, true)
			}else{
				configMap.put(configDomain.mapName, false)
			}
		} else if (configDomain.valueType == ConfigDomain.ValueType.DATE){
			configMap.put(configDomain.mapName, parseDate(configDomain.getMapValue(),"yyyy-MM-dd"))
		}else{
			configMap.put(configDomain.mapName, configDomain.mapValue)
		}
		return configMap
	}
	
	static Map<String,DateFormat> dateFormatMap = new HashMap<String,DateFormat>()
	public static Date parseDate(String dateStr,String format){
		if(null==dateStr||dateStr==""){
			return null;
		}
		DateFormat fmt =  dateFormatMap.get(format)
		if(dateFormatMap.get(format)==null){
			fmt =new SimpleDateFormat(format);
			dateFormatMap.put(dateStr,format)
		}
		return fmt.parse(dateStr);
	}
	/**
	 * return yyyy-MM-dd
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String dateStr){
		return parseDate(dateStr,"yyyy-MM-dd");
	}
	public static String UnicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch.toString() + "");
		}
		return str;
	}
	public static void createDir(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs()
		}
	}
	
	public static enum  JunjieFileType{
		EXCEL(""),
		WORD(""),
		GANTT(""),
		PHOTO(""),
		PDF(""),
		OTHERS("")
		String cssCode
		JunjieFileType(cssCode){
			this.cssCode = cssCode
		}
		public getCssCode(){
			return this.cssCode
		}

	}
	public static JunjieFileType genFileType(String fileName ){
		int dotPos = fileName.lastIndexOf(".")
		def extension = (dotPos) ? fileName[dotPos + 1..fileName.length() - 1] : ""
		JunjieFileType  result
		extension  = extension.toLowerCase()
		if(extension=="xlsx"||extension=="xls"){
			result =JunjieFileType.EXCEL
		}else if(extension=="doc" || extension=="docx"){
			result = JunjieFileType.WORD
		}else if(extension=="pdf"){
			result = JunjieFileType.PDF
		} else if("jpg,jpeg,png,gif".indexOf(extension)!=-1){
			result = JunjieFileType.PHOTO
		}else{
			result =JunjieFileType.OTHERS
		}
		return result
	}

	  
}
