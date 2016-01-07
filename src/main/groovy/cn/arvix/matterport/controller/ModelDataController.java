package cn.arvix.matterport.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.matterport.service.FetchDataService;


@Controller
@RequestMapping("")
public class ModelDataController {
	@Autowired
	FetchDataService fetchDataService;
	
	
	@ResponseBody
	@RequestMapping("/api/v1/fetchData")
	public Map<String,Object> fetchData(String sourceUrl,boolean force) {
		return fetchDataService.fetch(sourceUrl, force);
	}
	
	@RequestMapping("/modelData/index")
	public String index(String sourceUrl) {
		return "modelData/index";
	}

}
