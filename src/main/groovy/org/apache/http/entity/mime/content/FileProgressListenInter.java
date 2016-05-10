package org.apache.http.entity.mime.content;

public interface FileProgressListenInter {
	
	public void progress(long uploaded,long fileSize);

}
