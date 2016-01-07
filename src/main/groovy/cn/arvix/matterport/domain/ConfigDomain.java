package cn.arvix.matterport.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * site config values,init to application scope:servletContext.configMap in
 * gsp:${application?.configMap?.mapName}
 */
@Entity
@Table()
public class ConfigDomain implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2840825329019055598L;
	@Id
	@GeneratedValue
	@Column
	private Long id; // 编号
	@Column(unique = true)
	private String mapName;
	@Column(columnDefinition = "longtext")
	private String mapValue;
	@Column
	private String description;
	@Column
	private ValueType valueType;
	@Column
	Boolean editable = false;

	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateCreated;
	/**
	 * 最后更新时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastUpdated;

	public static enum ValueType {
		Integer, String,HTML, Boolean, DATE,
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMapName() {
		return mapName;
	}


	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapValue() {
		return mapValue;
	}

	public void setMapValue(String mapValue) {
		this.mapValue = mapValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
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

}