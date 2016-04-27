package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;

import java.util.Map;

public interface FetchDataService {
	
	Map<String,Object> fetch(String sourceUrl,boolean force);
	Map<String,Object> fetch(String[] sourceUrls,boolean force);
	Status getCaseStatus(String caseId);
}
