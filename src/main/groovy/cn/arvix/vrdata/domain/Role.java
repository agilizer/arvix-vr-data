package cn.arvix.vrdata.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author abel.lee 2014年9月23日
 * role只做后台管理用，和plugin role无关。
 */
@Entity
@Table(name = "sys_role")
public class Role implements Serializable {
	/**
	 * 
	 */
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_USER = "USER";
	private static final long serialVersionUID = -5496991108604730216L;
	@Id
	@Column
	@GeneratedValue
	private Long id; // 编号
	@Column(unique = true)
	private String authority; // 角色标识
	@Column
	private String description; // 角色描述,UI界面显示使用
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private List<Resource> resources; // 拥有的资源
	@Column
	private Boolean available = Boolean.FALSE; // 是否可用,如果不可用将不会添加给用户

	
	public Role() {
	}

	public Role(String authority, String description, Boolean available) {
		this.authority = authority;
		this.description = description;
		this.available = available;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Role role = (Role) o;

		if (id != null ? !id.equals(role.id) : role.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Role{" + "id=" + id + ", authority='" + authority + '\''
				+ ", description='" + description + '\'' + ", resourceIds="
				+ resources + ", available=" + available + '}';
	}
}
