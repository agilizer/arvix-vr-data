package cn.arvix.vrdata.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.domain.UserRole;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.util.StaticMethod;

@Service
public class UserRoleServiceImpl implements UserRoleService{

	@Autowired
	UserRepository userRepository;
	@Autowired
	UserService userService;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	JpaShareService jpaShareService;
	
	private static final Logger log = LoggerFactory
			.getLogger(UserRoleServiceImpl.class);
	@Override
	public void insert(Role role, User user) {
		UserRole userRole = new UserRole();
		userRole.setRole(role);
		userRole.setUser(user);
		userRoleRepository.save(userRole);
	}

	@Override
	public void insert(Long roleId, Long userId) {
		Role role = roleRepository.getOne(roleId);
		User user = userRepository.getOne(userId);
		if(null!=role&&null!=user){
			insert(role,user);
		}else{
			log.warn("insert role or user not found,roleId {} userId{}",roleId,userId);
		}
	}

	@Override
	public void delete(Role role, User user) {
		UserRole userRole = userRoleRepository.findByUserAndRole(user, role);
		if(null!=userRole){
			userRoleRepository.delete(userRole);
		}
	}

	@Override
	public void delete(Long roleId, Long userId) {
		Role role = roleRepository.getOne(roleId);
		User user = userRepository.getOne(userId);
		if(null!=role&&null!=user){
			delete(role,user);
		}else{
			log.warn("delete role or user not found,roleId {} userId{}",roleId,userId);
		}
	}

	@Override
	public void enabled(Long userId, Boolean enabled) {
		jpaShareService.executeForHql("update User set enabled=? where id=?", enabled,userId);
	}

	@Override
	public Map<String, Object> addAdmin(String username) {
		Map<String,Object> result = StaticMethod.getResult();
		User user  = userRepository.findByUsername(username);
		if(user!=null){
			Role role = roleRepository.findByAuthority(Role.ROLE_ADMIN);
			UserRole userRole = userRoleRepository.findByUserAndRole(user, role);
			if(userRole == null){
				insert(role,user);
				result.put(ArvixDataConstants.SUCCESS, true);
			}else{
				result.put(ArvixDataConstants.ERROR_MSG, "该用户已经是管理员");
			}
		}else{
			result.put(ArvixDataConstants.ERROR_MSG, "没找到相关用户！");
		}
		return result;
	}

	@Override
	public Map<String, Object> removeAdmin(String username) {
		Map<String,Object> result = StaticMethod.getResult();
		User user  = userRepository.findByUsername(username);
		User currentUser = userService.currentUser();
		if(user!=null){
			if(user == currentUser){
				result.put(ArvixDataConstants.ERROR_MSG, "不能取消自己的管理权限！");
			}else{
				Role role = roleRepository.findByAuthority(Role.ROLE_ADMIN);
				UserRole userRole = userRoleRepository.findByUserAndRole(user, role);
				if(userRole != null){
					delete(role,user);
				}
				result.put(ArvixDataConstants.SUCCESS, true);
			}
		}else{
			result.put(ArvixDataConstants.ERROR_MSG, "没找到相关用户！");
		}
		return result;
	}


}
