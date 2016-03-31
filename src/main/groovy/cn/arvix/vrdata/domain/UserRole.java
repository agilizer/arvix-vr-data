package cn.arvix.vrdata.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sys_user_role")
public class UserRole  implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1442601998050057989L;
	
	@Id
	@Column()
	@GeneratedValue
    private Long id;
	@OneToOne  
	private Role role;
	@OneToOne  
	private User user;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	

}
