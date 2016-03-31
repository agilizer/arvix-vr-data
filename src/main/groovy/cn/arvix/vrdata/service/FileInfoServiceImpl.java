package cn.arvix.vrdata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.FileInfo;
import cn.arvix.vrdata.repository.FileInfoRepository;

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
