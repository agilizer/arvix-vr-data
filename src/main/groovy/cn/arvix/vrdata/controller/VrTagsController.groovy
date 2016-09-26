package cn.arvix.vrdata.controller

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

import cn.arvix.vrdata.service.ModelDataService;

@Controller
@RequestMapping("/api/v1/jsonstore/model")
public class VrTags {
	//https://my.matterport.com/api/v1/jsonstore/model/mattertags/N6HuPecF32y
	@Autowired
	ModelDataService modelDataService;
	@ResponseBody
	@RequestMapping(value = "/mattertags/{caseId}")
	public String mattertags(@PathVariable("caseId")String caseId) {
		return  modelDataService.getModelTags(caseId) ;
	}
}
