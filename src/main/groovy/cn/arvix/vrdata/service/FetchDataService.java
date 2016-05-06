package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.domain.SyncTaskContent;

import java.util.Map;

public interface FetchDataService {
	
	Map<String,Object> fetch(SyncTaskContent syncTaskContent);
	Status getCaseStatus(String caseId);
}
