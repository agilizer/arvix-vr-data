package cn.arvix.matterport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cn.arvix.matterport.domain.Role;
import cn.arvix.matterport.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public User findByUsername(String username);
	@Query("select u_r.role  from UserRole u_r   where u_r.user.id = ?1")
	public List<Role> listRoleByUserId(Long userId);
}
