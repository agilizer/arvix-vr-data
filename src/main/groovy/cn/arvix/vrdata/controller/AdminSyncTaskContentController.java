package cn.arvix.vrdata.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.domain.Role;
import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;
import cn.arvix.vrdata.service.FetchDataService;
import cn.arvix.vrdata.service.SyncTaskContentService;
import cn.arvix.vrdata.service.UploadDataService;
import cn.arvix.vrdata.service.UserService;
import cn.arvix.vrdata.util.StaticMethod;

@Controller
@RequestMapping("/admin/syncTasskContent")
public class AdminSyncTaskContentController {
	
	@Autowired
	SyncTaskContentRepository syncTaskContentRepository;
	@Autowired
	SyncTaskContentService syncTaskContentService;
	@Autowired
	UserService userService;
	@Autowired
	FetchDataService fetchDataService;
	@Autowired
	UploadDataService uploadDataService;
	
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
	
	
	@RequestMapping(value = "/cleanSuccess", method = RequestMethod.POST)
	@ResponseBody
	public String cleanSuccess() {
		syncTaskContentService.cleanSuccess();
		return "true";
	}
	
	@RequestMapping(value = "/cleanFailed", method = RequestMethod.POST)
	@ResponseBody
	public String cleanFailed() {
		syncTaskContentService.cleanFailed();
		return "true";
	}

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
		List<SyncTaskContent> syncTaskContentList = syncTaskContentRepository.findAll();
		model.addAttribute("tasks", syncTaskContentList);
		return "admin/syncPage";
	}

	
}
