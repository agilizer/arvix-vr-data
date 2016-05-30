package cn.arvix.vrdata.service;

import java.util.Map;

import cn.arvix.vrdata.domain.SyncTaskContent;

public interface FetchDataService {
	Map<String,Object> fetchData(SyncTaskContent syncTaskContent);
}
