package cn.arvix.matterport.client;

import java.io.Serializable;

public class ModelDataClient  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4865070217329556899L;
	
	/**
	 * caseId
	 */
	private String caseId;
	private String title;
	private String sourceUrl;
	/**
	 * json modelData
	 */
	private String modelDataClient;
	private String description;
	
	/**
	 * files json description
	 */
	private String fileJson;
	private Long fileTotalSize = 0l;
	private String currentFetchFileKey;
	private Integer fileSumCount = 0;
	private Integer fileFetchedCount = 0;
	private  String  fetchErrorMsg;
	private Long userTimeSec;
	
	public static enum FetchStatus {
		FETCHING("正在抓取"),FINISH("完成抓取"),
		ERROR("抓取出错");
		private String text;
		FetchStatus(String text){
			this.text = text;
		}
		public String getText(){
			return this.text;
		}
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	public String getModelDataClient() {
		return modelDataClient;
	}
	public void setModelDataClient(String modelDataClient) {
		this.modelDataClient = modelDataClient;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFileJson() {
		return fileJson;
	}
	public void setFileJson(String fileJson) {
		this.fileJson = fileJson;
	}
	public Long getFileTotalSize() {
		return fileTotalSize;
	}
	public void setFileTotalSize(Long fileTotalSize) {
		this.fileTotalSize = fileTotalSize;
	}
	public String getCurrentFetchFileKey() {
		return currentFetchFileKey;
	}
	public void setCurrentFetchFileKey(String currentFetchFileKey) {
		this.currentFetchFileKey = currentFetchFileKey;
	}
	public Integer getFileSumCount() {
		return fileSumCount;
	}
	public void setFileSumCount(Integer fileSumCount) {
		this.fileSumCount = fileSumCount;
	}
	public Integer getFileFetchedCount() {
		return fileFetchedCount;
	}
	public void setFileFetchedCount(Integer fileFetchedCount) {
		this.fileFetchedCount = fileFetchedCount;
	}
	public String getFetchErrorMsg() {
		return fetchErrorMsg;
	}
	public void setFetchErrorMsg(String fetchErrorMsg) {
		this.fetchErrorMsg = fetchErrorMsg;
	}
	public Long getUserTimeSec() {
		return userTimeSec;
	}
	public void setUserTimeSec(Long userTimeSec) {
		this.userTimeSec = userTimeSec;
	}
	
	
}
