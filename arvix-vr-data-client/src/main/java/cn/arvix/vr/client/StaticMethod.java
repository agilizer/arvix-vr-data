package cn.arvix.vr.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class StaticMethod {
	//public static String SERVE_URL="http://vr.arvix.cn/";
	
	public static boolean checkCaseIdServiceExist(String serviceDomain,String caseId){
		boolean result =  false;
		try {
			Document doc = Jsoup.parse(new URL(serviceDomain+"api/v1/isExist/"+caseId), 30000);
			result  = Boolean.parseBoolean(doc.body().text());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
