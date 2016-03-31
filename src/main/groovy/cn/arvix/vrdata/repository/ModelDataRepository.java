package cn.arvix.vrdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.arvix.vrdata.domain.ModelData;

public interface ModelDataRepository extends JpaRepository<ModelData, Long> {
	ModelData findByCaseId(String caseId);
	
	@Query("select fileJson from ModelData where caseId=?1")
	String findFileJsonByCaseId(String caseId);
	@Query("select activeReel from ModelData where caseId=?1")
	String findActiveReelByCaseId(String caseId);
}