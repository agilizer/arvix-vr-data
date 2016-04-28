package cn.arvix.vrdata.bootstrap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.domain.UserRole;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.service.UserService;


@Service
public class BootstrapServiceImpl implements BootstrapService, ApplicationContextAware{
	private static final Logger log = LoggerFactory
			.getLogger(BootstrapServiceImpl.class);
	private ApplicationContext applicationContext;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserService userService;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	ConfigDomainInitService configDomainInitService;
	@Autowired
	AutoSyncAndUpdateTask autoSyncAndUpdateTask;

	String ENV ="";
	boolean isDev = false;
	@PostConstruct
	public void init(){
		String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
		ENV = org.apache.commons.lang3.StringUtils.join(profiles);
		if(ENV.indexOf("dev")>-1){
			isDev = true;
		}
		log.info("env---------------->"+ENV);
		initUsers();
		configDomainInitService.init(isDev);
		autoSyncAndUpdateTask.init();
	}
	public void initUsers() {
		if(userRepository.count()==0){
			createDefaultUser("admin","123456",Role.ROLE_ADMIN);
			createDefaultUser("user","123456",Role.ROLE_USER);
		}
	}
	private void createDefaultUser(String username,String password,String authority){
		Role role = roleRepository.findByAuthority(authority);
		if(role==null){
			role = new Role();
			role.setAvailable(true);
			role.setAuthority(authority);
			role.setDescription("");
			role = roleRepository.save(role);
		}
		User user = new User();
		 user.setPassword(passwordEncoder.encode(password));
		 user.setUsername(username);
		userRepository.save(user);
		UserRole userAndRole = new UserRole();
		userAndRole.setRole(role);
		userAndRole.setUser(user);
		userRoleRepository.save(userAndRole);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
