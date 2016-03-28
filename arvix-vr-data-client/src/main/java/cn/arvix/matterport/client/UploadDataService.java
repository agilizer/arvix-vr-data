package cn.arvix.matterport.client;

public interface UploadDataService {
	
	void uploadData(String serverUrl,String apiKey,String filePath,ModelDataClient modelData);

}
