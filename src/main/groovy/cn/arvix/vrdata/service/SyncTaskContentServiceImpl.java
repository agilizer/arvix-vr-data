package cn.arvix.vrdata.service;

import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskLevel;
import cn.arvix.vrdata.domain.SyncTaskContent.TaskType;
import cn.arvix.vrdata.domain.User;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;

@Service
public class SyncTaskContentServiceImpl implements SyncTaskContentService {
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
		if (syncTaskContent == null) {
			syncTaskContent = new SyncTaskContent();
			User user = userService.currentUser();
			syncTaskContent.setCaseId(caseId);
			syncTaskContent.setAuthor(user);
			syncTaskContent.setSourceUrl(sourceUrl);
			syncTaskContent.setDstUrl(dstUrl);
			syncTaskContent.setTaskLevel(taskLevel);
			syncTaskContent.setDateCreated(Calendar.getInstance());
			if (dstUrl == null || dstUrl.trim().equals("")
					|| taskType == TaskType.FETCH) {
				syncTaskContent.setTaskType(TaskType.FETCH);
			} else {
				syncTaskContent.setTaskType(TaskType.FETCH_UPDATE);
			}
			syncTaskContent.setWorking(false);
			syncTaskContent.setTaskStatus(SyncTaskContent.TaskStatus.WAIT);
			syncTaskContent = syncTaskContentRepository.saveAndFlush(syncTaskContent);
			excute(syncTaskContent);
		}
	}

	@Override
	public void excute(SyncTaskContent syncTaskContent) {
		if(syncTaskContent!=null){
			if(syncTaskContent.getTaskType()==TaskType.UPDATE){
				uploadDataService.uploadData(syncTaskContent);
			}else{
				fetchDataService.fetch(syncTaskContent);
			}
		}
	}

}
