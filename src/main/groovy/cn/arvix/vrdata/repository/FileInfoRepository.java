package cn.arvix.vrdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.vrdata.domain.FileInfo;

public interface FileInfoRepository extends  JpaRepository<FileInfo, Long>{

}

