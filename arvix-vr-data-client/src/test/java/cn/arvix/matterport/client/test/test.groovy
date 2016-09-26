package cn.arvix.matterport.client.test

import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import org.apache.commons.io.IOUtils

//
// -H "Accept-Encoding: gzip, deflate, sdch, br"
// -H "Accept-Language: zh-CN,zh;q=0.8"
// -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36"
// -H "Accept: application/json"
// -H "Referer: https://my.matterport.com/show/?m=N6HuPecF32y"
// -H "Cookie: __utma=1.645123838.1456495809.1467134783.1467134783.1; __utmz=1.1467134783.1.1.utmcsr=my.matterport.com|utmccn=(referral)|utmcmd=referral|utmcct=/models/eLqD5UUcQVH; anonymousflags=""{\\""panospheres\\"": 49}:1bcSce:IiLLYx682TSi4L8tpQWG--Xbn74""; _ga=GA1.2.645123838.1456495809; mp_mixpanel__c=0"
// -H "Connection: keep-alive"


//println Jsoup.connect("https://my.matterport.com/api/v1/jsonstore/model/mattertags/N6HuPecF32y")
//.header("Referer","https://my.matterport.com/show/?m=N6HuPecF32y")
//.header("Accept-Encoding","gzip, deflate, sdch, br")
//.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36")
////.header("Accept","application/json")
//.header("Connection","keep-alive")
//.header("Referer","https://my.matterport.com/show/?m=N6HuPecF32y")
//.timeout(300000).execute().body();

CloseableHttpClient httpclient = HttpClients.createDefault();
try {
	HttpGet get = new HttpGet("https://my.matterport.com/api/v1/jsonstore/model/highlights/N6HuPecF32y/active_reel");
	get.addHeader("Referer","https://my.matterport.com/show/?m=N6HuPecF32y");
	get.addHeader("Accept-Encoding","gzip, deflate, sdch, br");
	get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36");
	get.addHeader("Accept","application/json");
	get.addHeader("Connection","keep-alive");
	
	CloseableHttpResponse response = null;
	try {
		response = httpclient.execute(get);
		int code = response.getStatusLine().getStatusCode()
		HttpEntity resEntity = response.getEntity();
		if(code == 200){
			if (resEntity != null) {
				
				String text = IOUtils.toString(resEntity.getContent());
				println text;				
			}
		}		
		EntityUtils.consume(resEntity);
		
	}catch(e){
		
		e.printStackTrace()
	} finally {
		response.close();
	}
} finally {
	httpclient.close();
}

