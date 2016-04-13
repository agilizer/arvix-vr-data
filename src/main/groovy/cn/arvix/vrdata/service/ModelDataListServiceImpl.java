package cn.arvix.vrdata.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.ModelData.FetchStatus;

@Service
public class ModelDataListServiceImpl implements ModelDataListService{
	@Autowired
	JpaShareService jpaShareService;
	@Override
	public JdbcPage list(int max, int offset, String search) {
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("fetchStatus",FetchStatus.FINISH);
		String hql = "select m.title,m.caseId,f.storePath,m.description From ModelData m left join m.fileInfo f  where m.fetchStatus=:fetchStatus order by m.id asc" ;
		String countHql = "select count(*) from ModelData where fetchStatus=:fetchStatus " ;
		JdbcPage jdbcPage = jpaShareService.queryForHql(hql, countHql, max,
				offset, map);
		return jdbcPage;
	}

}
