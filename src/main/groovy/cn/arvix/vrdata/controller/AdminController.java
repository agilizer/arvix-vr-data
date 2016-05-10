package cn.arvix.vrdata.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.RoleRepository;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;
import cn.arvix.vrdata.repository.UserRepository;
import cn.arvix.vrdata.repository.UserRoleRepository;
import cn.arvix.vrdata.service.FetchDataService;
import cn.arvix.vrdata.service.FetchDataServiceImpl;
import cn.arvix.vrdata.service.JdbcPage;
import cn.arvix.vrdata.service.ModelDataService;
import cn.arvix.vrdata.service.SimpleStaService;
import cn.arvix.vrdata.service.SyncTaskContentService;
import cn.arvix.vrdata.service.UploadDataService;
import cn.arvix.vrdata.service.UserRoleService;
import cn.arvix.vrdata.service.UserService;
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
	@Autowired
	SimpleStaService simpleStaService;
	@Autowired
	SyncTaskContentRepository syncTaskContentRepository;
	@Autowired
	SyncTaskContentService syncTaskContentService;
	
	private Calendar startStaTime = Calendar.getInstance();

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
	@RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteTask(@RequestParam("caseId") String caseId, @RequestParam("taskType") SyncTaskContent.TaskType taskType) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		SyncTaskContent syncTaskContent = syncTaskContentRepository.findOneByCaseIdAndTaskType(caseId, taskType);
		if (syncTaskContent == null) {
			resultBody.put("deleteResult", "没有此任务");
		} else {
			if (syncTaskContent.getTaskStatus() == SyncTaskContent.TaskStatus.WORKING) {
				resultBody.put("deleteResult", "无法删除正在执行的任务");
			} else {
				syncTaskContentRepository.delete(syncTaskContent);
				resultBody.put("deleteResult", "删除成功");
			}
		}
		return resultBody;
	}

	@Secured({Role.ROLE_ADMIN}) 
	@RequestMapping(value = "/syncCreate", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> syncCreate(@RequestParam("sourceUrls") String sourceUrls, @RequestParam("taskLevel") SyncTaskContent.TaskLevel taskLevel,
									@RequestParam("taskType") SyncTaskContent.TaskType taskType,
									@RequestParam(value = "dstUrl", defaultValue = "") String dstUrl , @RequestParam(value = "force", defaultValue = "FALSE") boolean force) {
		if (sourceUrls != "") {
			sourceUrls = sourceUrls.trim();
		}
		if (dstUrl != "") {
			dstUrl = dstUrl.trim();
		}
		syncTaskContentService.create(sourceUrls, dstUrl, taskLevel, taskType);
		Map<String, Object> result = StaticMethod.getResult();
		result.put(ArvixDataConstants.SUCCESS, true);
		return result;
	}

	@Secured({Role.ROLE_ADMIN})
	@RequestMapping(value = "/syncPage")
	public String syncPage(Model model) {
		User user = userService.currentUser();
		List<SyncTaskContent> syncTaskContentList = new ArrayList<SyncTaskContent>();
		List<String> messages = new ArrayList<String>();
		if (user != null) {
			syncTaskContentList.addAll(syncTaskContentRepository.getTaskByUserId(user.getId()));
			for (SyncTaskContent syncTaskContent : syncTaskContentList) {
				SyncTaskContent.TaskType taskType = syncTaskContent.getTaskType();
				Status status = null;
				if (taskType == SyncTaskContent.TaskType.UPDATE) {
					status = uploadDataService.getCaseStatus(syncTaskContent.getCaseId());
				} else {
					status = fetchDataService.getCaseStatus(syncTaskContent.getCaseId());
				}
				if (status != null) {
					//最后五条消息
					int len = status.getMessage().size();
					int num = 5;
					int fromIndex = len - num;
					if (fromIndex < 0) {
						fromIndex = 0;
					}
					messages.addAll(status.getMessage().subList(fromIndex, len));
				}
			}
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String msg : messages) {
			stringBuilder.append(msg + "\n");
		}
		model.addAttribute("messages", stringBuilder.toString());
		model.addAttribute("tasks", syncTaskContentList);
		return "admin/sync";
	}

	@Secured({Role.ROLE_ADMIN})
	@RequestMapping(value = "/checkSyncStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> syncStatus(@RequestParam("sourceUrl") String sourceUrl) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (sourceUrl.contains(syncTaskContentService.urlSep)) {
			String[] sourceUrls = sourceUrl.split(syncTaskContentService.urlSep);
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
	@RequestMapping(value = "/checkUploadStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadStatus(@RequestParam("serverUrl") String serverUrl) {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		if (serverUrl.contains(syncTaskContentService.urlSep)) {
			String sep = syncTaskContentService.urlSep;
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
	
	@Secured({Role.ROLE_ADMIN}) 
	@RequestMapping(value = "/downsizeStaShow")
	public String downsizeStaShow(Model model) {
		model.addAttribute("size", simpleStaService.getDownloadSize());
		model.addAttribute("startTime", startStaTime);
		return "admin/downsizeStaShow";
	}
	
}
