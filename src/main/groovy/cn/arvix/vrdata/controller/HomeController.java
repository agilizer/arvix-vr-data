package cn.arvix.vrdata.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.arvix.vrdata.consants.ArvixMatterportConstants;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.service.ConfigDomainService;
import cn.arvix.vrdata.service.ModelDataListService;
import cn.arvix.vrdata.service.ModelDataService;
import cn.arvix.vrdata.util.StaticMethod;

@Controller
public class HomeController {

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
			model.addAttribute("modelData",modelData );
			System.out.println(modelData.getModelData());
			model.addAttribute("caseId",caseId );
			model.addAttribute("siteUrl", configDomainService.getConfig(ArvixMatterportConstants.SITE_URL));
			model.addAttribute("teamDesc", configDomainService.getConfig(ArvixMatterportConstants.TEAM_DESCRIPTION));
			viewName = "show";
			
		}
		return viewName;
	}
	
	@RequestMapping("/show2/**")
	public String show2(Model model, HttpServletRequest request) {
		return "redirect:/show/?m="+request.getParameter("m");
	}
}
