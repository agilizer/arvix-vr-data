package cn.arvix.vrdata.controller;

import java.util.HashMap;
import java.util.Map;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
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
	FetchDataService fetchDataService;
	@Autowired
	UploadDataService uploadDataService;

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
	@RequestMapping(value = "/sync", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> sync(@RequestParam("sourceUrl") String sourceUrl,@RequestParam(value = "dstUrl", defaultValue = "") String dstUrl , @RequestParam(value = "force", defaultValue = "FALSE") boolean force) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (sourceUrl != "") {
			sourceUrl = sourceUrl.trim();
		}
		if (dstUrl != "") {
			dstUrl = dstUrl.trim();
		}
		Map<String, Object> fetchResult = fetchDataService.fetch(sourceUrl, dstUrl, force);
		resultBody.put("fetchResult", fetchResult);
		return resultBody;
	}

	@Secured({Role.ROLE_ADMIN})
	@RequestMapping(value = "/syncPage")
	public String syncPage() {
		return "admin/sync";
	}

	@Secured({Role.ROLE_ADMIN})
	@RequestMapping(value = "/checkSyncStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> syncStatus(@RequestParam("sourceUrl") String sourceUrl) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (sourceUrl.contains(FetchDataServiceImpl.urlSep)) {
			String[] sourceUrls = sourceUrl.split(FetchDataServiceImpl.urlSep);
			Status multiStatus = new Status();
			for (String url : sourceUrls) {
				if (url != "") {
					int subIndex = url.indexOf("?m=");
					String caseId = url.substring(subIndex + 3);
					Status status = fetchDataService.getCaseStatus(caseId);
					if (status != null) {
						multiStatus.getMessage().addAll(status.getMessage());
					}
				}
			}
			resultBody.put("status", multiStatus);
		} else {
			int subIndex = sourceUrl.indexOf("?m=");
			String caseId = sourceUrl.substring(subIndex + 3);
			Status status = fetchDataService.getCaseStatus(caseId);
			resultBody.put("status", status);
		}

		return resultBody;
	}

	@Secured({Role.ROLE_ADMIN})
	@ResponseBody
	@RequestMapping(value = "/uploadData")
	public Map<String, Object> uploadModelData(@RequestParam("serverUrl") String serverUrl, @RequestParam("dstUrl") String dstUrl) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (serverUrl != "") {
			serverUrl = serverUrl.trim();
		}
		if (dstUrl != "") {
			dstUrl = dstUrl.trim();
		}
		Map<String, Object> uploadResult =  uploadDataService.uploadData(serverUrl, dstUrl, null);
		resultBody.put("uploadResult", uploadResult);
		return resultBody;
	}

	@Secured({Role.ROLE_ADMIN})
	@RequestMapping(value = "/checkUploadStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadStatus(@RequestParam("serverUrl") String serverUrl) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (serverUrl.contains(FetchDataServiceImpl.urlSep)) {
			String sep = FetchDataServiceImpl.urlSep;
			String[] sourceUrlArr = serverUrl.split(sep);
			Status multiStatus = new Status();

			for (String url : sourceUrlArr) {
				if (url != "") {
					String urll = url;
					int subIndex = urll.indexOf("?m=");
					String caseId = urll.substring(subIndex + 3);
					Status status = uploadDataService.getCaseStatus(caseId);
					if (status != null) {
						multiStatus.getMessage().addAll(status.getMessage());
					}
				}
			}
			resultBody.put("status", multiStatus);
		} else {
			int subIndex = serverUrl.indexOf("?m=");
			String caseId = serverUrl.substring(subIndex + 3);
			Status status = uploadDataService.getCaseStatus(caseId);
			resultBody.put("status", status);
		}

		return resultBody;
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
	
}
