package cn.arvix.matterport.service;

import cn.arvix.matterport.domain.ModelData;

public interface ModelDataService {
	String BASE_URL = "base.url";
	Object getJsonFileDesc(String caseId);
	
	ModelData findByCaseId(String caseId);
	
	JdbcPage list(int max,int offset);
	
	

}
