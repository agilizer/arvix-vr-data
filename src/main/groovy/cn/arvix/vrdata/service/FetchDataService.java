package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.domain.SyncTaskContent;

import java.util.Map;

public interface FetchDataService {
	
	Map<String,Object> fetchData(SyncTaskContent syncTaskContent);
	Status getCaseStatus(String caseId);
}
