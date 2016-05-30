package cn.arvix.vrdata.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table()
public class ModelData  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4865070217329556899L;
	
	@Id
	@GeneratedValue
	@Column
	private Long id; // 编号
	/**
	 * caseId
	 */
	@Column(unique = true)
	private String caseId;
	@Column
	private String title;
	@Column
	private String sourceUrl;
	
	@OneToOne(cascade={CascadeType.REMOVE})
	private FileInfo fileInfo;
	/**
	 * json modelData
	 */
	@Column(columnDefinition = "longtext")
	private String modelData;
	
	@Column(columnDefinition = "longtext")
	private String description;
	
	/**
	 * files json description
	 */
	@Column(columnDefinition = "longtext")
	private String fileJson;
	@Column
	private Long fileTotalSize = 0l;
	@Column
	private String currentFetchFileKey;
	@Column
	private Integer fileSumCount = 0;
	@Column
	private Integer fileFetchedCount = 0;
	@Column
	private  FetchStatus fetchStatus;
	@Column(columnDefinition = "longtext")
	private  String  fetchErrorMsg;
	@Column(columnDefinition = "longtext")
	private  String  activeReel;
	@Column
	private Long userTimeSec;
	@Column
	private String dataVersion;
	@Column
	private String jsVersion;
	@Column
	private String tagStr;
	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateCreated;
	@Column
	private Boolean online = true;
	
	@Column
	private Boolean useMatterportLink = true;
	@Column(columnDefinition = "longtext")
	private String rightHtml ; 
	
	@Column(columnDefinition = "longtext")
	private String logoDownHtml ; 
	/**
	 * 最后更新时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastUpdated;
	/**
	 * 是否显示arvix logo相关信息
	 */
	private Boolean logoShow = true;

	public String getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(String dataVersion) {
		this.dataVersion = dataVersion;
	}

	public String getJsVersion() {
		return jsVersion;
	}

	public void setJsVersion(String jsVersion) {
		this.jsVersion = jsVersion;
	}

	public String getTagStr() {
		return tagStr;
	}

	public void setTagStr(String tagStr) {
		this.tagStr = tagStr;
	}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getModelData() {
		return modelData;
	}

	public void setModelData(String modelData) {
		this.modelData = modelData;
	}

	public Long getFileTotalSize() {
		return fileTotalSize;
	}

	public void setFileTotalSize(Long fileTotalSize) {
		this.fileTotalSize = fileTotalSize;
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

	public FetchStatus getFetchStatus() {
		return fetchStatus;
	}

	public void setFetchStatus(FetchStatus fetchStatus) {
		this.fetchStatus = fetchStatus;
	}


	public Calendar getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Calendar dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Calendar getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Calendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getFileJson() {
		return fileJson;
	}

	public void setFileJson(String fileJson) {
		this.fileJson = fileJson;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public String getCurrentFetchFileKey() {
		return currentFetchFileKey;
	}

	public void setCurrentFetchFileKey(String currentFetchFileKey) {
		this.currentFetchFileKey = currentFetchFileKey;
	}

	public Long getUserTimeSec() {
		return userTimeSec;
	}

	public void setUserTimeSec(Long userTimeSec) {
		this.userTimeSec = userTimeSec;
	}

	public String getFetchErrorMsg() {
		return fetchErrorMsg;
	}

	public void setFetchErrorMsg(String fetchErrorMsg) {
		this.fetchErrorMsg = fetchErrorMsg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActiveReel() {
		return activeReel;
	}

	public void setActiveReel(String activeReel) {
		this.activeReel = activeReel;
	}

	public Boolean getLogoShow() {
		return logoShow;
	}

	public void setLogoShow(Boolean logoShow) {
		this.logoShow = logoShow;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public String getRightHtml() {
		return rightHtml;
	}

	public void setRightHtml(String rightHtml) {
		this.rightHtml = rightHtml;
	}

	public String getLogoDownHtml() {
		return logoDownHtml;
	}

	public void setLogoDownHtml(String logoDownHtml) {
		this.logoDownHtml = logoDownHtml;
	}

	public Boolean getUseMatterportLink() {
		return useMatterportLink;
	}

	public void setUseMatterportLink(Boolean useMatterportLink) {
		this.useMatterportLink = useMatterportLink;
	}
	
	
}
