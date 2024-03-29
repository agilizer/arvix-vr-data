package cn.arvix.vrdata.service;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskLevel;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskStatus;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskType;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;

@Service
public class SyncTaskContentServiceImpl implements SyncTaskContentService {
	private static final Logger log = LoggerFactory
	.getLogger(SyncTaskContentServiceImpl.class);
	@Autowired
	private JpaShareService jpaShareService;
	@Autowired
	private SyncTaskContentRepository syncTaskContentRepository;
	@Autowired
	private UserService userService;
	@Autowired
	FetchDataService fetchDataService;
	@Autowired
	UploadDataService uploadDataService;
	@Autowired
	MessageBroadcasterService messageBroadcasterService;
	@Override
	public void create(String sourceUrls, String dstUrl,
			TaskLevel taskLevel, TaskType taskType) {
	        if (sourceUrls.contains(curlSep)) {
	            //多个地址
	            String[] sourceUrlArray = sourceUrls.split(urlSep);
	            for(String url:sourceUrlArray){
	            	this.createOne(url, dstUrl, taskLevel, taskType);
	            }
	        } 
	        else{
	        	this.createOne(sourceUrls, dstUrl, taskLevel, taskType);
	        }
	}

	private void createOne(String sourceUrl, String dstUrl,
			TaskLevel taskLevel, TaskType taskType) {
		int subIndex = sourceUrl.indexOf("?m=");
		String caseId = sourceUrl.substring(subIndex + 3).trim();
		SyncTaskContent syncTaskContent = syncTaskContentRepository
				.findOneByCaseId(caseId);
		if (syncTaskContent == null&& subIndex >0) {
			syncTaskContent = new SyncTaskContent();
			User user = userService.currentUser();
			syncTaskContent.setCaseId(caseId);
			syncTaskContent.setAuthor(user);
			syncTaskContent.setSourceUrl(sourceUrl);
			syncTaskContent.setDstUrl(dstUrl);
			syncTaskContent.setTaskLevel(taskLevel);
			syncTaskContent.setDateCreated(Calendar.getInstance());
			syncTaskContent.setTaskType(taskType);
			syncTaskContent.setWorking(false);
			syncTaskContent.setTaskStatus(SyncTaskContent.TaskStatus.WAIT);
			syncTaskContent = syncTaskContentRepository.saveAndFlush(syncTaskContent);
			excute(syncTaskContent);
		}else{
			if(syncTaskContent!=null&&syncTaskContent.getTaskStatus()==TaskStatus.FAILED){
				jpaShareService.executeForHql("update SyncTaskContent set taskStatus=?,lastUpdated=?   where id=?",TaskStatus.SUCCESS,Calendar.getInstance()
						,syncTaskContent.getId());
				log.info("syncTaskContent  task exist and failed, restart task,sourceUrl  :{},dst url :{}",sourceUrl,dstUrl);
				messageBroadcasterService.send("任务已经存在");
			}else{
				messageBroadcasterService.send("任务存在或者url不正确");
				log.info("syncTaskContent  task exist or url is wrong,sourceUrl  :{},dst url :{}",sourceUrl,dstUrl);
			}
		}
	}

	@Override
	public void excute(SyncTaskContent syncTaskContent) {
		if(syncTaskContent!=null){
			if(syncTaskContent.getTaskType()==TaskType.UPDATE){
				uploadDataService.uploadData(syncTaskContent);
			}else{
				fetchDataService.fetchData(syncTaskContent);
			}
		}
	}

	@Override
	public void failed(SyncTaskContent syncTaskContent, String failedMsg) {
		jpaShareService.executeForHql("update SyncTaskContent set taskStatus=?,failedMsg=?,lastUpdated=?  where id=?", 
				TaskStatus.FAILED,failedMsg,Calendar.getInstance(),syncTaskContent.getId());
		fetchDataService.removeUrlAndFlagCheck(syncTaskContent.getSourceUrl());
		uploadDataService.clearCaseMap(syncTaskContent.getCaseId());
		messageBroadcasterService.send(failedMsg);
	}

	@Override
	public void finish(SyncTaskContent syncTaskContent) {
		jpaShareService.executeForHql("update SyncTaskContent set taskStatus=?,lastUpdated=?   where id=?",
				 TaskStatus.SUCCESS,Calendar.getInstance(),syncTaskContent.getId());
		fetchDataService.removeUrlAndFlagCheck(syncTaskContent.getSourceUrl());
		uploadDataService.clearCaseMap(syncTaskContent.getCaseId());
	}

	@Override
	public void cleanSuccess() {
		jpaShareService.executeForHql("delete from  SyncTaskContent where taskStatus=?", TaskStatus.SUCCESS);
	}

	@Override
	public void cleanFailed() {
		jpaShareService.executeForHql("delete from  SyncTaskContent where taskStatus=?", TaskStatus.FAILED);
	}

}
