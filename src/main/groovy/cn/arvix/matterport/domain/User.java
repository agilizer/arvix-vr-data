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
 * @author asdtiang
 *
 */
@Entity
@Table(name = "sys_user")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8407376072364433531L;
	@Id
	@Column
	@GeneratedValue
    private Long id;
	@Column(unique = true)
    private String username; //用户名 phone or email
	@Column
    private String password; //密码
	@Column
	private String nickname;
	@Column
    private String salt; //加密密码的盐
	
	@Temporal(TemporalType.TIMESTAMP)
	private    Calendar dateCreated;
	@Temporal(TemporalType.TIMESTAMP)
	private    Calendar lastUpdated;
	
	@Column
	private Boolean enabled = Boolean.TRUE;
    
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(Long id, String username, String password, String nickname,
			String salt, Calendar dateCreated, Calendar lastUpdated,
			Boolean enabled) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.salt = salt;
		this.dateCreated = dateCreated;
		this.lastUpdated = lastUpdated;
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", nickname=" + nickname + ", salt=" + salt
				+ ", dateCreated=" + dateCreated + ", lastUpdated="
				+ lastUpdated + ", enabled=" + enabled + "]";
	}
	
	

//    
//    public User() {
//    }
//
//    public User(String username, String password) {
//        this.username = username;
//        this.password = password;
//    }
//    public String getCredentialsSalt() {
//        return username + salt;
//    }
//    
//    
//
//	public Boolean getEnabled() {
//		return enabled;
//	}
//
//	public void setEnabled(Boolean enabled) {
//		this.enabled = enabled;
//	}
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getSalt() {
//		return salt;
//	}
//
//	public void setSalt(String salt) {
//		this.salt = salt;
//	}
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public Calendar getDateCreated() {
//		return dateCreated;
//	}
//
//	public void setDateCreated(Calendar dateCreated) {
//		this.dateCreated = dateCreated;
//	}
//
//	public Calendar getLastUpdated() {
//		return lastUpdated;
//	}
//
//	public void setLastUpdated(Calendar lastUpdated) {
//		this.lastUpdated = lastUpdated;
//	}
    
}
