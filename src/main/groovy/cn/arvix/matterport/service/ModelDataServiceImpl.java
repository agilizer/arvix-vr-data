package cn.arvix.matterport.service;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.arvix.matterport.consants.ArvixMatterportConstants;
import cn.arvix.matterport.domain.ModelData;
import cn.arvix.matterport.domain.ModelData.FetchStatus;
import cn.arvix.matterport.repository.ModelDataRepository;
import cn.arvix.matterport.util.AntZipUtil;
import cn.arvix.matterport.util.JSONResult;
import cn.arvix.matterport.util.StaticMethod;

@Service
public class ModelDataServiceImpl implements ModelDataService{
	private static final Logger log = LoggerFactory
			.getLogger(ModelDataServiceImpl.class);
	
	@Autowired
	ModelDataRepository modelDataRepository;
	@Autowired
	ConfigDomainService configDomainService;
	@Autowired
	JpaShareService jpaShareService;
	@Override
	public Object getJsonFileDesc(String caseId) {
		String fileJson = modelDataRepository.findFileJsonByCaseId(caseId);
		Object result = null;
		if(fileJson!=null){
			JSONObject jsonObject = JSON.parseObject(fileJson);
			String baseUrl = configDomainService.getConfigString(ArvixMatterportConstants.SITE_URL)+"files/"+caseId+"/{{filename}}";
			jsonObject.put(BASE_URL,baseUrl );
			result = jsonObject;
		}
		return result;
	}
	@Override
	public ModelData findByCaseId(String caseId) {
		return modelDataRepository.findByCaseId(caseId);
	}
	@Override
	public JdbcPage list(int max, int offset) {
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("fetchStatus",FetchStatus.FINISH);
		String hql = "select title,caseId From ModelData where fetchStatus=:fetchStatus " ;
		String countHql = "select count(*) from ModelData where fetchStatus=:fetchStatus " ;
		JdbcPage jdbcPage = jpaShareService.queryForHql(hql, countHql, max,
				offset, map);
		return jdbcPage;
	}
	@Override
	public JdbcPage listAdmin(int max, int offset) {
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("fetchStatus",FetchStatus.FINISH);
		String hql = "select title,caseId,id,description,sourceUrl,fileTotalSize,fileSumCount,modelData From ModelData where fetchStatus=:fetchStatus " ;
		String countHql = "select count(*) from ModelData where fetchStatus=:fetchStatus " ;
		JdbcPage jdbcPage = jpaShareService.queryForHql(hql, countHql, max,
				offset, map);
		return jdbcPage;
	}
	@Override
	public Map<String, Object> update(String fieldName, Long id, String value) {
		Map<String, Object> result = StaticMethod.getResult();
		jpaShareService.executeForHql("update ModelData set "+fieldName+"=? where id=?", value,id);
		result.put(ArvixMatterportConstants.SUCCESS, true);
		return result;
	}
	@Override
	public JSONResult uploadModelData(ModelData modelData, MultipartFile zipFileData) {
		JSONResult jsonResult = new JSONResult();
		if(modelData.getCaseId()!=null){
			ModelData modelDataFromDb  = modelDataRepository.findByCaseId(modelData.getCaseId());
			if(modelDataFromDb!=null){
				jsonResult.setErrorCode(ArvixMatterportConstants.ERROR_CODE_EXIST);
			}else{
				Calendar now = Calendar.getInstance();
				modelData.setDateCreated(now);
				modelData.setLastUpdated(now);
				modelData.setFetchStatus(FetchStatus.FINISH);
				String saveDir =  configDomainService.getConfigString(ArvixMatterportConstants.FILE_STORE_PATH);
				log.info("unzip file ......."+"  saveDir : {}",saveDir);
				String uploadFilePath =saveDir+"upload/"
						+System.currentTimeMillis()+".zip";
				File fileDir = new File(saveDir+"upload/");
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				File file = new File(uploadFilePath);
				try {
					zipFileData.transferTo(file);
					String unzipResult = AntZipUtil.readByApacheZipFile(uploadFilePath, saveDir);
					log.info("unzipResult {}",unzipResult);
					modelDataRepository.save(modelData);
					jsonResult.setSuccess(true);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			jsonResult.setErrorCode(CASE_ID_NULL);
		}
		log.info("jsonResult {}",jsonResult.toString());
		return jsonResult;
	}

}
