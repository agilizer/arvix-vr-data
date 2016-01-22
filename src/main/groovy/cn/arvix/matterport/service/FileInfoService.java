package cn.arvix.matterport.service;

import cn.arvix.matterport.domain.FileInfo;

public interface FileInfoService {
	
	public String getDownloadPathById(Long id);

	public FileInfo getFileInfo(Long id);
	
	
	
}
