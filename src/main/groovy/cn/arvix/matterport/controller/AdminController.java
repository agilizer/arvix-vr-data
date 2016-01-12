package cn.arvix.matterport.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.matterport.domain.Role;
import cn.arvix.matterport.service.JdbcPage;
import cn.arvix.matterport.service.ModelDataService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	ModelDataService modelDataService;
	@RequestMapping("/listModelData/{max}/{offset}")
	public String listAdmin(@PathVariable("max")Integer max,@PathVariable("offset")Integer offset,Model model) {
		JdbcPage jdbcPage = modelDataService.listAdmin(max, offset);
		model.addAttribute("jdbcPage", jdbcPage);
		return "admin/listModelData";
	}
	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/updateModelData")
	public Map<String, Object> update(String name,Long pk,String value) {
		return modelDataService.update(name, pk, value);
	}
	
}
