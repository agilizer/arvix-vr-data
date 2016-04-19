package cn.arvix.vrdata.controller;

import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import cn.arvix.vrdata.service.ModelDataService


@Controller
@RequestMapping("/api")
public class ApiController {
	@Autowired
	ModelDataService modelDataService;
	@ResponseBody
	@RequestMapping("/player/models/{caseId}/files")
	public Object checkUsername(@PathVariable("caseId") String caseId,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		Object object  = modelDataService.getJsonFileDesc(caseId)
		return object;
	}
}
