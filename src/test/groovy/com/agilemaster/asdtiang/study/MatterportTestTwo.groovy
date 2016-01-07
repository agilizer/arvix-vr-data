package com.agilemaster.asdtiang.study

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.Connection.Response

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

class MatterportTestTwo {
	def static Long EXPIRE_TIME = 240000
	def static FILE_DIR = "/home/abel/matterport/file/"
	
	public static void genFiles(String caseId){
		def jsonObject =  genFileJson(caseId);
		def fileList = []
		def baseUrl = ""
		jsonObject.each{key,value->
			if(key!='base.url'){
				fileList.add(key)
			}else{
				baseUrl = value
			}
		}
		println("file should fetch size:"+fileList.size());
		Response  resultFile
		File file
		long start  
		def fetchFileCount = 0
		def fetchTempList = fileList
		while(fileList.size()>0){
			start  = System.currentTimeMillis()
			
			for(String fileUrl:fetchTempList){
				try{
					/**
					 * time out处理，amazon cdn超时后会报403错误
					 */
					if((System.currentTimeMillis()-start)>EXPIRE_TIME){
						baseUrl = genBaseUrl(caseId);
						break;
					}
					resultFile = Jsoup.connect(baseUrl.replace("{{filename}}", fileUrl)).ignoreContentType(true).timeout(300000).execute();
				   // output here
					file = new File(FILE_DIR+fileUrl)
					if(!file.exists()){
						FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
					}
					fetchFileCount ++
					println("fetch file "+fetchFileCount+"  finish")
				   }catch(e){
					   e.printStackTrace();
					   println "useTime:"+(System.currentTimeMillis()-start)/1000
				   }
			}
			if(fetchFileCount==fileList.size()){
				break;
			}
			fetchTempList = fileList.subList(fetchFileCount, fileList.size());
		}
		println("file  fetch size:"+fetchFileCount);
	}
	public static JSONObject genFileJson(String caseId){
		def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
		String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
		return  JSON.parseObject(body);
	}
	
	public static String genBaseUrl(String caseId){
		def url="https://my.matterport.com/api/player/models/${caseId}/files?filter=*dam%2C*dam.lzma%2C*_obj.zip%2Cpan%2F*.jpg%2C*texture_jpg_high%2F*.jpg%2C*texture_jpg_low%2F*.jpg%2C*.csv%2C*.modeldata&type=2&format=json"
		String body = Jsoup.connect(url).ignoreContentType(true).timeout(200000).execute().body();
		return  JSON.parseObject(body).get("base.url");
	}
	
}
