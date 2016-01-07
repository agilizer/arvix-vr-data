package com.agilemaster.asdtiang.study

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import com.alibaba.fastjson.JSON

def str = "https://my.matterport.com/show/?m=MXfJvWQecHT"
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
		
		
		script = script.replace("window.MP_PREFETCHED_MODELDATA", "var targetObject")
		// 使用ScriptEngine來parse
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		engine.eval(script);

		// 取得你要的變數
		Object obj = engine.get("targetObject");
		System.out.println("targetObject-->" + obj);
		System.out.println(JSON.toJSONString(obj));
		// 將obj轉成Json物件
		//JSONObject json = JSONObject.fromObject(obj);
		script = script.replace("window.MP_PREFETCHED_MODELDATA =","")
		println script;
	}
}

def title  = doc.title()
println title