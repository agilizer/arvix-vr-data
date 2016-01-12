package cn.arvix.matterport.service;

import java.util.Map;

import cn.arvix.matterport.domain.ModelData;

public interface ModelDataService {
	
	
	String BASE_URL = "base.url";
	Object getJsonFileDesc(String caseId);
	
	ModelData findByCaseId(String caseId);
	
	 Map<String, Object> update(String fieldName,Long id,String value);
	
	/**
	 * title , caseId,description
	 * @param max
	 * @param offset
	 * @return
	 */
	JdbcPage list(int max,int offset);
	
	
	JdbcPage listAdmin(int max,int offset);
}
