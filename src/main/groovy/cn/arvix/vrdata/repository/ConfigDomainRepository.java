package cn.arvix.vrdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.vrdata.domain.ConfigDomain;

public interface ConfigDomainRepository extends JpaRepository<ConfigDomain, Long>{
	ConfigDomain findByMapName(String mapName);
}

