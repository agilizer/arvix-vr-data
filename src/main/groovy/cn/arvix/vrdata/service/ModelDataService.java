package cn.arvix.vrdata.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.util.JSONResult;

public interface ModelDataService {

	String CASE_ID_NULL = "CaseIdNull";
	String BASE_URL = "base.url";

	Object getJsonFileDesc(String caseId);

	ModelData findByCaseId(String caseId);

	String getActiveReel(String caseId);

	JSONResult uploadModelData(ModelData modelData, MultipartFile zipFileData);

	Map<String, Object> update(String fieldName, Long id, String value);
	
	Map<String, Object> update(ModelData modelData);

	Map<String, Object> updatePhoto(Long id, MultipartFile photoData);

	Map<String, Object> delete(Long id);

	/**
	 * title , caseId,description
	 * 
	 * @param max
	 * @param offset
	 * @return
	 */
	JdbcPage list(int max, int offset);

	JdbcPage listAdmin(int max, int offset);
	
}
