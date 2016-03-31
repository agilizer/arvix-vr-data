package cn.arvix.vrdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.vrdata.domain.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}


