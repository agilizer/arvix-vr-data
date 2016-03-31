package cn.arvix.vrdata.controller;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

import cn.arvix.vrdata.consants.ArvixMatterportConstants
import cn.arvix.vrdata.domain.ModelData
import cn.arvix.vrdata.service.ConfigDomainService
import cn.arvix.vrdata.service.FetchDataService
import cn.arvix.vrdata.service.ModelDataService
import cn.arvix.vrdata.util.JSONResult


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
	@RequestMapping("/api/v1/isExist/{caseId}")
	public String isExist(@PathVariable("caseId") String caseId) {
		String result = "false";
		ModelData data = modelDataService.findByCaseId(caseId);
		if(data){
			 result = "true";
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/api/v1/updateModelData")
	public JSONResult updateModelData(ModelData modelData,String  modelDataClient,MultipartFile zipFileData,String apiKey,HttpServletRequest request,HttpServletResponse response) {
		if(!configDomainService.getConfigString(ArvixMatterportConstants.API_UPLOAD_MODELDATA_KEY).equals(apiKey)){
			try {
				response.sendError(403, "apiKey wrong");
			} catch (IOException e) {
				log.error("api key wrong!",e);
			}
			return null;
		}
		modelData.setModelData(modelDataClient);
		return modelDataService.uploadModelData(modelData, zipFileData);
	}
	
	
	@ResponseBody
	@RequestMapping("/api/v1/updateModelPhoto")
	public Map<String,Object> updateModelPhoto(Long id,MultipartFile qqfile,HttpServletRequest request,HttpServletResponse response) {
		return modelDataService.updatePhoto(id, qqfile);
	}
	
	
	
	@RequestMapping("/modelData/index")
	public String index(String sourceUrl) {
		return "modelData/index";
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/api/v1/jsonstore/model/highlights/{caseId}/active_reel",method=RequestMethod.GET)
	public Object highlights(@PathVariable("caseId") String caseId,HttpServletRequest request,HttpServletResponse response) {
		return modelDataService.getActiveReel(caseId);
	}

}
