package cn.arvix.matterport.util;

import java.util.regex.Matcher
import java.util.regex.Pattern

import cn.arvix.matterport.consants.ArvixMatterportConstants
import cn.arvix.matterport.domain.ConfigDomain

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

	  
}
