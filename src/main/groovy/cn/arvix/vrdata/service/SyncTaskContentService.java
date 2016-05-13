package cn.arvix.vrdata.service;

import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskLevel;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskType;


public interface SyncTaskContentService {
	public  String urlSep = "\\n";
	public String curlSep = "\n";
	public void  create(String sourceUrls,String dstUrl,TaskLevel taskLevel,TaskType taskType);
	public void failed( SyncTaskContent syncTaskContent,String failedMsg);
	public void finish(SyncTaskContent syncTaskContent);
	public void excute(SyncTaskContent syncTaskContent);
	public void cleanSuccess();
	public void cleanFailed();
}
