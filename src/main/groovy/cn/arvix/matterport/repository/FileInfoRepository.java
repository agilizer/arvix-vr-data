package cn.arvix.matterport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.matterport.domain.FileInfo;

public interface FileInfoRepository extends  JpaRepository<FileInfo, Long>{

}

