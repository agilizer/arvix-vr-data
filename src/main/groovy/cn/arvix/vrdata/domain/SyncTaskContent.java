package cn.arvix.vrdata.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table()
public class SyncTaskContent  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5893206576416810566L;
	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column
	private String caseId;
	@ManyToOne()
	private User author;
	@Column(columnDefinition = "longtext")
	private String sourceUrl = "";
	@Column(columnDefinition = "longtext")
	private String failedMsg = "";
	@Column
	private String dstUrl = "";
	@Column
	private Boolean working = false;
	@Column
	private TaskLevel taskLevel = TaskLevel.NORMAL;
	@Column
	private TaskStatus taskStatus = TaskStatus.WAIT;
	@Column
	private TaskType taskType;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateCreated;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastUpdated;
	public static enum TaskLevel{
		HIGH,
		NORMAL		
	}
	public static enum TaskStatus{
		WORKING,
		WAIT,
		FAILED,
		SUCCESS,
		/**
		 * 程序出错
		 */
		ERROR
	}
	public static enum TaskType{
		FETCH,
		UPDATE,
		FETCH_UPDATE
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
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrls) {
		this.sourceUrl = sourceUrls;
	}
	public String getDstUrl() {
		return dstUrl;
	}
	public void setDstUrl(String dstUrl) {
		this.dstUrl = dstUrl;
	}
	public Boolean getWorking() {
		return working;
	}
	public void setWorking(Boolean working) {
		this.working = working;
	}
	public TaskLevel getTaskLevel() {
		return taskLevel;
	}
	public void setTaskLevel(TaskLevel taskLevel) {
		this.taskLevel = taskLevel;
	}
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Calendar getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Calendar dateCreated) {
		this.dateCreated = dateCreated;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getFailedMsg() {
		return failedMsg;
	}
	public void setFailedMsg(String failedMsg) {
		this.failedMsg = failedMsg;
	}
	
	public Calendar getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Calendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SyncTask[" + caseId + ", " + taskType + ", " + taskLevel + ", userId: " + author.getId() + "]");
		return stringBuilder.toString();
	}
}
