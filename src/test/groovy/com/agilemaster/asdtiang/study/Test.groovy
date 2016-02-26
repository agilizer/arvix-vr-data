package com.agilemaster.asdtiang.study

import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

def str = "https://my.matterport.com/show/?m=ZujWX1srahK"
def caseId = str.substring(str.indexOf("?m=")+3)
println(caseId)
Document doc = Jsoup.parse(new URL(str), 30000);

// 取得所有的script tag
Elements eles = doc.getElementsByTag("script");

for (Element ele : eles) {
	// 檢查是否有detailInfoObject字串
	String script = ele.toString();
	if (script.indexOf("window.MP_PREFETCHED_MODELDATA") > -1) {

		// 只取得script的內容
		script = ele.childNode(0).toString();
		println script
		
		
		script = script.replace("window.MP_PREFETCHED_MODELDATA = ", "")
		script =script.substring(0,script.length()-1)
		println script
		JSONObject object = JSON.parseObject(script);
		
		println("------------------\n\n")
		def objectModel = object.getJSONObject("model")
		println(objectModel)
		println("------------------\n\n")
		JSONArray objectAr = objectModel.getJSONArray("images")
		println(objectAr)
		Response  resultFile
		File file
		def fileName
		objectAr.each {
			it.get("signed_src")
			resultFile = Jsoup.connect(it.get("signed_src")).ignoreContentType(true).timeout(300000).execute();
			// output here
			fileName = System.currentTimeMillis()+".jpg"
			 file = new File("/home/temp/2016/" + fileName)
			 if(!file.exists()){
				 FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
			 }
			it.put("signed_src","http://127.0.0.1:8888/resources/test/"+fileName)
			println "signed_src"+fileName
			
			it.get("download_url")
			resultFile = Jsoup.connect(it.get("download_url")).ignoreContentType(true).timeout(300000).execute();
			// output here
			fileName = System.currentTimeMillis()+".jpg"
			 file = new File("/home/temp/2016/" + fileName)
			 if(!file.exists()){
				 FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
			 }
			it.put("download_url","http://127.0.0.1:8888/resources/test/"+fileName)
			println "download_url"+fileName
			
			it.get("src")
			resultFile = Jsoup.connect(it.get("src")).ignoreContentType(true).timeout(300000).execute();
			// output here
			fileName = System.currentTimeMillis()+".jpg"
			 file = new File("/home/temp/2016/" + fileName)
			 if(!file.exists()){
				 FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
			 }
			it.put("src","http://127.0.0.1:8888/resources/test/"+fileName)
			
			
			it.get("thumbnail_signed_src")
			resultFile = Jsoup.connect(it.get("thumbnail_signed_src")).ignoreContentType(true).timeout(300000).execute();
			// output here
			fileName = System.currentTimeMillis()+".jpg"
			 file = new File("/home/temp/2016/" + fileName)
			 if(!file.exists()){
				 FileUtils.writeByteArrayToFile(file, resultFile.bodyAsBytes())
			 }
			it.put("thumbnail_signed_src","http://127.0.0.1:8888/resources/test/"+fileName)
		}
		println("------------------\n\n")
		println(JSON.toJSONString(object))
		
		
	}
}




def title  = doc.title()
println title