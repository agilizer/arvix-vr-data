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
	@ManyToOne()
	private User author;
	@Column(columnDefinition = "longtext")
	private String sourceUrls;
	@Column
	private String dstUrl;
	@Column
	private Boolean working = false;
	@Column
	private TaskLevel taskLevel = TaskLevel.NORMAL;
	@Column
	private TaskStatus taskStatus = TaskStatus.WAIT;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateCreated;
	
	public static enum TaskLevel{
		HIGHT,
		NORMAL		
	}
	public static enum TaskStatus{
		WORKING,
		WAIT		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getSourceUrls() {
		return sourceUrls;
	}
	public void setSourceUrls(String sourceUrls) {
		this.sourceUrls = sourceUrls;
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
	
	

}
