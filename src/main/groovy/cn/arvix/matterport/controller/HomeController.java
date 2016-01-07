package cn.arvix.matterport.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.arvix.matterport.consants.ArvixMatterportConstants;
import cn.arvix.matterport.domain.ModelData;
import cn.arvix.matterport.service.ConfigDomainService;
import cn.arvix.matterport.service.ModelDataService;

@Controller
public class HomeController {

	@Autowired
	ModelDataService modelDataService;
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
		model.addAttribute("modelDataJdbcPage", modelDataService.list(10, 0));
		return "home";
	}

	@RequestMapping("/show/**")
	public String show(Model model, HttpServletRequest request) {
		String caseId = request.getParameter("m");
		ModelData modelData = modelDataService.findByCaseId(caseId);
		String viewName = "404";
		if(modelData!=null){
			model.addAttribute("modelData",modelData );
			System.out.println(modelData.getModelData());
			model.addAttribute("caseId",caseId );
			model.addAttribute("siteUrl", configDomainService.getConfig(ArvixMatterportConstants.SITE_URL));
			viewName = "show";
			
		}
		return viewName;
	}
}
