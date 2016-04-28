package cn.arvix.vrdata.repository;

import cn.arvix.vrdata.domain.ConfigDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigDomainRepository extends JpaRepository<ConfigDomain, Long>{
	ConfigDomain findByMapName(String mapName);

}

