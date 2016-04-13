package cn.arvix.vrdata.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.service.UserRoleService;
import cn.arvix.vrdata.service.UserService;

@Controller
@RequestMapping("/adminUser")
public class AdminUserController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRoleService userRoleService;
	@Autowired
	UserService userService;
	
	@RequestMapping("/list/{type}/{max}/{offset}/{searchStr}")
	public String list(@PathVariable("max")Integer max,@PathVariable("type") String type,
			@PathVariable("searchStr") String searchStr,
			@PathVariable("offset")Integer offset,Model model) {
		model.addAttribute("type", type);
		model.addAttribute("max", max);
		model.addAttribute("offset", offset);
		if(null==type||"a".equals(type)){
			type=null;
		}
		if(null==searchStr||"a".equals(searchStr)){
			searchStr=null;
		}
		model.addAttribute("jdbcPage", userService.list(searchStr,type,max, offset));
		model.addAttribute("roleList", roleRepository.findAll());
		String viewName = "adminUser/list";
		
		if("user".equals(type)){
			viewName = "adminUser/listPerson";
		}
		if("admin".equals(type)){
			model.addAttribute("roleAdminId", roleRepository.findByAuthority(Role.ROLE_ADMIN));
			viewName = "adminUser/listAdmin";
		}
		
		if(null==searchStr||"a".equals(searchStr)){
			model.addAttribute("searchStr", "");
		}
		return viewName;
	}
	
	@RequestMapping("/edit/{id}")
	public String edit(@PathVariable Long id,Model model) {
		String viewName = "adminUser/edit";
		if(null!=id){
			User user  = userRepository.findOne(id);
			List<Role> roleList = userRoleRepository.findRolesByUser(id);
			model.addAttribute("roleList", roleList);
			model.addAttribute("user", user);
		}
		return viewName ;
	}
	
	
	@ResponseBody
	@RequestMapping("/addRole")
	public String addRole(Long userId,Long roleId) {
		userRoleService.insert(roleId, userId);
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("/removeRole")
	public String removeRole(Long userId,Long roleId) {
		userRoleService.delete(roleId, userId);
		return "success";
	}
	
	
	
	@ResponseBody
	@RequestMapping("/addAdmin")
	public Map<String,Object> addToAdmin(String username) {
		return userRoleService.addAdmin(username);
	}
	
	@ResponseBody
	@RequestMapping("/removeAdmin")
	public Map<String,Object> removeAdmin(String username) {
		return userRoleService.removeAdmin(username);
	}
	
	
	@ResponseBody
	@RequestMapping("/enabled")
	public String enabled(Long userId,Boolean enabled) {
		userRoleService.enabled(userId, enabled);
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("/update")
	public Map<String,Object> update(Long id,String nickname,String username,boolean enabled,String password) {
		return userService.update(id,  nickname, username, enabled, password);
	}
	

}
