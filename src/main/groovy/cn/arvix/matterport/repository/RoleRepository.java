package cn.arvix.matterport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.matterport.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByAuthority(String role);
}
