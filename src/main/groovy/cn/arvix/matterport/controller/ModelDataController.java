package cn.arvix.matterport.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.arvix.matterport.consants.ArvixMatterportConstants;
import cn.arvix.matterport.domain.ModelData;
import cn.arvix.matterport.service.ConfigDomainService;
import cn.arvix.matterport.service.FetchDataService;
import cn.arvix.matterport.service.ModelDataService;
import cn.arvix.matterport.util.JSONResult;


@Controller
@RequestMapping("")
public class ModelDataController {
	private static final Logger log = LoggerFactory
			.getLogger(ModelDataController.class);
	@Autowired
	FetchDataService fetchDataService;
	@Autowired
	ModelDataService modelDataService;
	@Autowired
	ConfigDomainService configDomainService;
	
	@ResponseBody
	@RequestMapping("/api/v1/fetchData")
	public Map<String,Object> fetchData(String sourceUrl,boolean force) {
		return fetchDataService.fetch(sourceUrl, force);
	}
	
	@ResponseBody
	@RequestMapping("/api/v1/updateModelData")
	public JSONResult updateModelData(ModelData modelData,MultipartFile zipFileData,String apiKey,HttpServletResponse response) {
		if(!configDomainService.getConfigString(ArvixMatterportConstants.API_UPLOAD_MODELDATA_KEY).equals(apiKey)){
			try {
				response.sendError(403, "apiKey wrong");
			} catch (IOException e) {
				log.error("api key wrong!",e);
			}
			return null;
		}
		return modelDataService.uploadModelData(modelData, zipFileData);
	}
	
	@RequestMapping("/modelData/index")
	public String index(String sourceUrl) {
		return "modelData/index";
	}
	
	
	

}
