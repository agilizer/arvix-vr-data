package cn.arvix.vrdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.domain.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	@Query("select role from UserRole ur join ur.user user join ur.role role where user.id = ?")
	List<Role> findRolesByUser(Long userId);
	UserRole findByUserAndRole(User user,Role role);
}
