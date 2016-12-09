package cn.arvix.vrdata.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.service.ConfigDomainService;
import cn.arvix.vrdata.service.ModelDataListService;
import cn.arvix.vrdata.service.ModelDataService;
import cn.arvix.vrdata.util.StaticMethod;

@Controller
public class HomeController {

	private static final Logger log = LoggerFactory
			.getLogger(HomeController.class);
	@Autowired
	ModelDataService modelDataService;
	
	@Autowired
	ModelDataListService modelDataListService;
	@Autowired
	ConfigDomainService configDomainService;
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	@ResponseBody
	@RequestMapping("/session/**")
	public Map<String,Object> session(Model model) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("create", true);
		return result;
	}
	
	
	@RequestMapping("/")
	public String index(Model model) {
		// Collection<?> temp =
		// SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		model.addAttribute("modelDataJdbcPage", modelDataListService.list(12, 0,""));
		return "home";
	}

	@RequestMapping("/show/**")
	public String show(Model model, HttpServletRequest request,HttpServletResponse response) {
		String caseId = request.getParameter("m");
		ModelData modelData = modelDataService.findByCaseId(caseId);
		String viewName = "404";
		StaticMethod.cros(response);
		
		if(modelData!=null){
			log.info("modelData.getOnline()-->"+modelData.getOnline()+" title:"+modelData.getTitle());
			if(null==modelData.getUseMatterportLink()||true==modelData.getUseMatterportLink()){
				viewName = "redirect:"+modelData.getSourceUrl();
				if(!viewName.endsWith("&play=1")){
					viewName = viewName + "&play=1";
				}
			}else{
				if(null!=modelData.getOnline()&&modelData.getOnline()){
					model.addAttribute("modelData",modelData );
					model.addAttribute("caseId",caseId );
					model.addAttribute("siteUrl", configDomainService.getConfig(ArvixDataConstants.SITE_URL));
					model.addAttribute("teamDesc", configDomainService.getConfig(ArvixDataConstants.TEAM_DESCRIPTION));
					viewName = "show-"+modelData.getJsVersion();
				}else{
					viewName = "404";
				}
			}
		}
		return viewName;
	}
	
	@RequestMapping("/show2/**")
	public String show2(Model model, HttpServletRequest request) {
		return "redirect:/show/?m="+request.getParameter("m");
	}
	
	@RequestMapping("/models/group_1258/job_400c3158-26b8-461b-8c37-ddd2b322a7c4/wf_5465b36db14041d191d36bd4f0719843/mesh/1.1.407.13667/2016-09-09_2039.56/~/{{filename}}")
	public String aaa(Model model, HttpServletRequest request) {
		return "redirect:/show/?m="+request.getParameter("m");
	}
	
}
