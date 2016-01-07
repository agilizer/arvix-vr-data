package cn.arvix.matterport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.matterport.domain.ConfigDomain;

public interface ConfigDomainRepository extends JpaRepository<ConfigDomain, Long>{
	ConfigDomain findByMapName(String mapName);
}

