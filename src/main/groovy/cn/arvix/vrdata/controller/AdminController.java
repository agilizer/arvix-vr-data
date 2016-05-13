package cn.arvix.vrdata.controller;

import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.service.JdbcPage;
import cn.arvix.vrdata.service.ModelDataService;
import cn.arvix.vrdata.service.SimpleStaService;
import cn.arvix.vrdata.service.UserRoleService;
import cn.arvix.vrdata.service.UserService;
import cn.arvix.vrdata.util.StaticMethod;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	ModelDataService modelDataService;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRoleService userRoleService;
	@Autowired
	SimpleStaService simpleStaService;

	
	private Calendar startStaTime = Calendar.getInstance();

	@RequestMapping("/listModelData/{max}/{offset}")
	//TODO: 分页
	public String listAdmin(@PathVariable("max")Integer max,@PathVariable("offset")Integer offset,Model model) {
		JdbcPage jdbcPage = modelDataService.listAdmin(max, offset);
		model.addAttribute("jdbcPage", jdbcPage);
		return "admin/listModelData";
	}

	@RequestMapping("/searchModelData/{max}/{offset}")
	@Secured({Role.ROLE_ADMIN})
	//TODO: 搜索
	public String searchModelData(@PathVariable("max")Integer max, @PathVariable("offset")Integer offset,
									@RequestParam(value = "title", required = false)String title,
									@RequestParam(value = "caseId", required = false)String caseId, @RequestParam(value = "desc", required = false)String desc,
									@RequestParam(value = "tags", required = false)String tags, Model model) {
		JdbcPage jdbcPage = modelDataService.searchModelData(max, offset, title, caseId, desc, tags);
		model.addAttribute("jdbcPage", jdbcPage);
		model.addAttribute("search", 1);
		String searchStr = "?";
		if (title != null) {
			searchStr += "title=" + title;
		}
		if (caseId != null) {
			if (searchStr.equals("?")) {
				searchStr += "caseId=" + caseId;
			} else {
				searchStr += "&caseId=" + caseId;
			}
		}
		if (desc != null) {
			if (searchStr.equals("?")) {
				searchStr += "desc=" + desc;
			} else {
				searchStr += "&desc=" + desc;
			}
		}
		if (tags != null) {
			if (searchStr.equals("?")) {
				searchStr += "tags=" + tags;
			} else {
				searchStr += "&tags=" + tags;
			}
		}
		model.addAttribute("searchStr", searchStr);
		return "admin/listModelData";
	}

	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/updateModelDataByField")
	public Map<String, Object> updateModelDataByField(String name,Long pk,String value) {
		return modelDataService.update(name, pk, value);
	}

	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/updateModelData")
	public Map<String, Object> updateModelData(ModelData modelData) {
		return modelDataService.update(modelData);
	}
	
	
	@Secured({Role.ROLE_ADMIN}) 
	@RequestMapping(value = "/modelEdit/{caseId}")
	public String modelEdit(@PathVariable("caseId")String caseId,Model model) {
		model.addAttribute("model", modelDataService.findByCaseId(caseId));
		return "admin/modelEdit";
	}
	
	
	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/deleteModelData/{id}")
	public Map<String, Object> delete(@PathVariable("id")Long id,String value) {
		return modelDataService.delete(id);
	}
	
	@Secured({Role.ROLE_ADMIN}) 
	@ResponseBody
	@RequestMapping(value = "/updateUserPw/{userId}/newPassword")
	public Map<String, Object> updateUserPw(@PathVariable("userId")Long userId,@PathVariable("newPassword")String newPassword) {
		User user = userService.findById(userId);
		Map<String,Object> result = StaticMethod.getResult();
		if(null!=user){
			userService.changePassword(user, newPassword);
			 result.put(ArvixDataConstants.SUCCESS,true);
		}else{
			result.put(ArvixDataConstants.ERROR_MSG, "没有找到相关数据！");
			result.put(ArvixDataConstants.ERROR_CODE,404);
		}
		return result;
	}
	
	@Secured({Role.ROLE_ADMIN}) 
	@RequestMapping(value = "/downsizeStaShow")
	public String downsizeStaShow(Model model) {
		model.addAttribute("size", simpleStaService.getDownloadSize());
		model.addAttribute("startTime", startStaTime);
		return "admin/downsizeStaShow";
	}
	
}
