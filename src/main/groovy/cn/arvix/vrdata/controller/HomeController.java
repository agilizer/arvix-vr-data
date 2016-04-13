package cn.arvix.vrdata.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
			log.info("modelData.getOnline()-->"+modelData.getOnline());
			if(null!=modelData.getOnline()&&modelData.getOnline()){
				model.addAttribute("modelData",modelData );
				model.addAttribute("caseId",caseId );
				model.addAttribute("siteUrl", configDomainService.getConfig(ArvixDataConstants.SITE_URL));
				model.addAttribute("teamDesc", configDomainService.getConfig(ArvixDataConstants.TEAM_DESCRIPTION));
				model.addAttribute("contactHtml", configDomainService.getConfig(ArvixDataConstants.SERVICE_CONTACT_HTML));
				viewName = "show";
			}else{
				viewName = "404";
			}
		}
		return viewName;
	}
	
	@RequestMapping("/show2/**")
	public String show2(Model model, HttpServletRequest request) {
		return "redirect:/show/?m="+request.getParameter("m");
	}
}
