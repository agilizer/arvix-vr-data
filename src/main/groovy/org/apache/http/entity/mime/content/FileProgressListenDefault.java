package org.apache.http.entity.mime.content;

import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.service.MessageBroadcasterService;

public class FileProgressListenDefault  implements FileProgressListenInter{

	private MessageBroadcasterService messageBroadcasterService;
	private SyncTaskContent syncTaskContent;
	public FileProgressListenDefault(MessageBroadcasterService messageBroadcasterService, SyncTaskContent syncTaskContent){
		this.messageBroadcasterService = messageBroadcasterService;
		this.syncTaskContent =syncTaskContent;
	}
	
	@Override
	public void progress(long uploaded, long fileSize) {
		messageBroadcasterService.send(syncTaskContent.getCaseId()+" 上传进度: " + (uploaded/1000/1000) + "/" + (fileSize/1000/1000)+" M");
	}
	
	public SyncTaskContent getSyncTaskContent() {
		return syncTaskContent;
	}
	public void setSyncTaskContent(SyncTaskContent syncTaskContent) {
		this.syncTaskContent = syncTaskContent;
	}
}
