package cn.arvix.vrdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.arvix.vrdata.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByAuthority(String role);
}
