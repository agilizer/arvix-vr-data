package cn.arvix.vrdata.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.repository.UserRepository;

@Service("securityUserDetailsService")
public class SecurityUserDetailsService implements UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(SecurityUserDetailsService.class);
	// get user from the database, via Hibernate
	@Autowired
	private UserRepository userRepository;

	@Transactional()
	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		cn.arvix.vrdata.domain.User user = userRepository.findByUsername(username);
		if (null == user) {
			throw new UsernameNotFoundException(username);
		}
		List<GrantedAuthority> authorities = buildUserAuthority(userRepository.listRoleByUserId(user.getId()));
		return buildUserForAuthentication(user, authorities);

	}

	// Converts com.mkyong.users.model.User user to
	// org.springframework.security.core.userdetails.User
	private User buildUserForAuthentication(cn.arvix.vrdata.domain.User user, List<GrantedAuthority> authorities) {
		return new User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority(List<Role> userRoles) {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
		// Build user's authorities
		for (Role role : userRoles) {
			setAuths.add(new SimpleGrantedAuthority(role.getAuthority()));
			log.info("role.getAuthority()---->" + role.getAuthority());
		}
		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
		return Result;
	}
}