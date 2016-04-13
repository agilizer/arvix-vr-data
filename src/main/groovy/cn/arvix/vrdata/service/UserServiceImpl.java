package cn.arvix.vrdata.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.been.UserVO;
import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.domain.UserRole;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.util.StaticMethod;

/**
 * 
 * @author abel.lee
 *2014年11月14日 上午10:11:14
 */
@Service
public class UserServiceImpl implements UserService {
	private static final Logger log = LoggerFactory
			.getLogger(UserServiceImpl.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	JpaShareService jpaShareService;
	@Autowired
	UserRoleService userRoleService;
    /**
     * 创建用户
     * @param user
     */
    public User createUser(UserVO userVO) {
    	User user = new User();
    	user.setUsername(userVO.getUsername());
    	user.setNickname(userVO.getNickname());
    	user.setPassword(passwordEncoder.encode(userVO.getPassword()));////加密密码 
    	user.setDateCreated(Calendar.getInstance());
        user.setLastUpdated(Calendar.getInstance());
    	user=userRepository.save(user);
    	Role role = roleRepository.findByAuthority(Role.ROLE_USER);
    	UserRole userAndRole = new UserRole();
    	userAndRole.setRole(role);
		userAndRole.setUser(user);
		userRoleRepository.save(userAndRole);
        return user;
    }

    @Override
    public User updateUser(User user) {
    	//TODO
        return userRepository.save(user);
    }

	@Override
	public void deleteUser(User user) {
		userRepository.delete(user);		
	}

	@Override
	public void changePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	public User findByUsername(String username) {
		if(username ==null){
			return null;
		}
		return userRepository.findByUsername(username);
	}

	@Override
	public void loginAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User currentUser() {
		User user = null;
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		log.info("principal-->" + principal.getClass().getName()
				+ "   --->value:" + principal.toString());
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			user = findByUsername(username);
		}
		return user;
	}

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}
	
	@Override
	public JdbcPage list(String searchStr, String roleString, Integer max,
			Integer offset) {
		Role role = null;
		if (null != roleString) {
			role = roleRepository.findByAuthority(roleString.toUpperCase());
		}
		String hql = "select user from User  user i  where 1=1";
		String countHql = "select count(user.id) from User user  where 1=1";
		Map<String, Object> queryMap = null;
		System.out.println("searchStr----------------------->" + searchStr);
		queryMap = new HashMap<String, Object>();
		if (role != null) {
			hql = "select user from UserRole ur where ur.role=:role ";
			countHql = "select count(user) from UserRole ur  where ur.role=:role";
			queryMap.put("role", role);
		}
		if (null != searchStr) {
			queryMap.put("searchStr", "%" + searchStr + "%");
			hql = hql + " and  (user.username like:searchStr o)";
			countHql = countHql
					+ " and (user.username like:searchStr)";
		}
		log.info("hql:-->"+hql);
		return jpaShareService
				.queryForHql(hql, countHql, max, offset, queryMap);
	}
	@Override
	public   Map<String,Object> update(Long id, String nickname, String username,
			 boolean enabled,String password) {
		User user = userRepository.findOne(id);
		Map<String,Object> result = StaticMethod.getResult();
		if(user!=null){
			if(!user.getUsername().equals(username)&&this.findByUsername(username)!=null){
				result.put(ArvixDataConstants.ERROR_MSG, "用户名已经被其它用户使用，请更改！");
				return result;
			}
			if(password!=null&&password.length()>6&&password.length()<30){
				this.changePassword(user, password);
			}
			if(!user.getUsername().equals(username)&&this.findByUsername(username)==null&&username.length()==11){
				jpaShareService.executeForHql("update User set username=?  where id=?",username,user.getId());
			}
			jpaShareService.executeForHql("update User set nickname=? where id=?",nickname,id);
			userRoleService.enabled(user.getId(), enabled);
			result.put(ArvixDataConstants.SUCCESS, true);
		}else{
			result.put(ArvixDataConstants.ERROR_MSG, "没有找到相关用户！");
		}
		return result;
	}


}
