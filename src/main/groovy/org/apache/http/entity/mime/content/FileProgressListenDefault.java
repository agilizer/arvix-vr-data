package org.apache.http.entity.mime.content;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.domain.SyncTaskContent;

public class FileProgressListenDefault  implements FileProgressListenInter{

	private Status status;
	private SyncTaskContent syncTaskContent;
	public FileProgressListenDefault(Status status, SyncTaskContent syncTaskContent){
		this.status = status;
		this.syncTaskContent =syncTaskContent;
	}
	
	@Override
	public void progress(long uploaded, long fileSize) {
		 status.addMessage(syncTaskContent.getCaseId()+" 上传进度: " + (uploaded/1000/1000) + "/" + (fileSize/1000/1000)+" M");
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public SyncTaskContent getSyncTaskContent() {
		return syncTaskContent;
	}
	public void setSyncTaskContent(SyncTaskContent syncTaskContent) {
		this.syncTaskContent = syncTaskContent;
	}
}
