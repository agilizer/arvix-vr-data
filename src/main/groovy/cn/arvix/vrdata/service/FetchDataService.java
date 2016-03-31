package cn.arvix.vrdata.service;

import java.util.Map;

public interface FetchDataService {
	
	Map<String,Object> fetch(String sourceUrl,boolean forse);
	
	
	
}
