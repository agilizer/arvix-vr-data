package cn.arvix.matterport.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import cn.arvix.matterport.domain.ModelData;
import cn.arvix.matterport.util.JSONResult;

public interface ModelDataService {
	
	String CASE_ID_NULL = "CaseIdNull";
	String BASE_URL = "base.url";
	Object getJsonFileDesc(String caseId);
	
	ModelData findByCaseId(String caseId);
	
	JSONResult uploadModelData(ModelData modelData,MultipartFile zipFileData);
	
	 Map<String, Object> update(String fieldName,Long id,String value);
	
	 
	 Map<String, Object> updatePhoto(Long id,MultipartFile photoData);
		

	/**
	 * title , caseId,description
	 * @param max
	 * @param offset
	 * @return
	 */
	JdbcPage list(int max,int offset);
	
	
	JdbcPage listAdmin(int max,int offset);
}
