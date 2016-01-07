package cn.arvix.matterport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.matterport.domain.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}


