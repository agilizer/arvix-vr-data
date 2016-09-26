package cn.arvix.vr.client;

public interface FetchService {
	
	void fetchData(String serverUrl,String apiKey,String workDir,String[] fetchUrlArray);
	void setUploadDataService(UploadDataService uploadDataService);
}
