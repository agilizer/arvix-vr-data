package cn.arvix.matterport.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.matterport.domain.Role;
import cn.arvix.matterport.service.ConfigDomainService;

@Controller
@RequestMapping("/configDomain")
public class ConfigDomainController {
	@Autowired
	ConfigDomainService configDomainService;
	@Secured({Role.ROLE_ADMIN}) 
	@RequestMapping(value = "/list")
	public String list(Model model) {
		model.addAttribute("configDomainList", configDomainService.list());
		return "configDomain/list";
	}
	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/update")
	public Map<String, Object> update(Long pk,String value) {
		return configDomainService.update(pk, value);
	}
	
}
