package cn.arvix.vrdata.service;

import java.util.Map;

import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;

public interface UserRoleService {
	
	void insert(Role role,User user);
	void insert(Long roleId,Long userId);
	void delete(Role role,User user);
	void delete(Long roleId,Long userId);
	void enabled(Long userId,Boolean enabled);
	Map<String,Object> addAdmin(String username);
	Map<String,Object> removeAdmin(String username);
}
