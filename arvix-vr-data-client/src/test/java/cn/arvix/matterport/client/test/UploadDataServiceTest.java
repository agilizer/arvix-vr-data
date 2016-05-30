package cn.arvix.matterport.client.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cn.arvix.matterport.client.ModelDataClient;
import cn.arvix.matterport.client.UploadDataService;
import cn.arvix.matterport.client.UploadDataServiceImpl;

public class UploadDataServiceTest {

	@Test
	public void test() {
		ModelDataClient modelData = new ModelDataClient();
		String newCaseId = System.currentTimeMillis()+"";
		modelData.setCaseId(newCaseId);
		modelData.setFileJson(newCaseId);
		modelData.setTitle("title"+ newCaseId);
		
		File file = new File("D:/home/test/YXkDVj4ungu.zip");
		File destFile = new File("D:/home/test/"+newCaseId+".zip");
		try {
			FileUtils.copyFile(file, destFile);
			UploadDataService uploadDataService = new UploadDataServiceImpl();
			uploadDataService.uploadData("http://localhost:8888/api/v1/updateModelData", "c2654aa9-f432-49a7-9dd6-524518beeea1", "D:/home/test/"+newCaseId+".zip", modelData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
