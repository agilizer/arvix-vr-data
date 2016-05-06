package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.domain.SyncTaskContent;

import java.util.Map;

public interface FetchDataService {
	
	Map<String,Object> fetch(String sourceUrl, String dstUrl, boolean force, SyncTaskContent.TaskLevel taskLevel, SyncTaskContent.TaskType taskType);
	Map<String, Object> fetch(String sourceUrl, String dstUrl, boolean force, SyncTaskContent syncTaskContent);
	Status getCaseStatus(String caseId);
}
