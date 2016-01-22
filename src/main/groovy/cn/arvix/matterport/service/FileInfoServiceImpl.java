package cn.arvix.matterport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.matterport.domain.FileInfo;
import cn.arvix.matterport.repository.FileInfoRepository;

@Service
public class FileInfoServiceImpl implements FileInfoService {
	
	@Autowired
	FileInfoRepository fileInfoRepository;

	@Override
	public String getDownloadPathById(Long id) {
		FileInfo fileInfo = fileInfoRepository.getOne(id);
		return fileInfo.getStorePath();
	}

	@Override
	public FileInfo getFileInfo(Long id) {
		// TODO Auto-generated method stub
		return fileInfoRepository.getOne(id);
	}

}
