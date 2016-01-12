package cn.arvix.matterport.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.arvix.matterport.consants.ArvixMatterportConstants;
import cn.arvix.matterport.domain.ModelData;
import cn.arvix.matterport.domain.ModelData.FetchStatus;
import cn.arvix.matterport.repository.ModelDataRepository;

@Service
public class ModelDataServiceImpl implements ModelDataService{

	
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

}
