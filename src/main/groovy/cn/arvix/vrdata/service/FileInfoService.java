package cn.arvix.vrdata.service;

import cn.arvix.vrdata.domain.FileInfo;

public interface FileInfoService {
	
	public String getDownloadPathById(Long id);

	public FileInfo getFileInfo(Long id);
	
	
	
}
