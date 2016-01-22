package cn.arvix.matterport.service;

import org.springframework.web.multipart.MultipartFile;

import cn.arvix.matterport.been.ImageTargetSize;
import cn.arvix.matterport.domain.FileInfo;

public interface FileService {
	/**
	 * 返回文件存储的路径
	 * @return
	 */
	String getRootPath();
	/**
	 * save file to path
	 * @param file
	 * @return if  file is null return null,else return FileInfo
	 */
	FileInfo saveFile(MultipartFile file,String path);
	
	FileInfo saveFileData(String data,String path);
	/**
	 * 按照指定的宽度和高度压缩
	 * @param file
	 * @param path
	 * @param imageTargetSizes
	 * @return
	 */
	FileInfo saveImageFile(MultipartFile file,String path,ImageTargetSize... imageTargetSizes );
	/**
	 * 当文件大于maxSize时执行压缩操作，压缩后图片宽度为sacleWidth
	 * @param file
	 * @param path
	 * @param maxSize
	 * @param sacleWidth
	 * @return
	 */
	FileInfo saveImageFile(MultipartFile file,String path,long maxSize,int sacleWidth );
	
	void delFile(FileInfo fileInfo);
	void delFile(Long fileInfoId);
}

