package cn.arvix.vrdata.bootstrap;

import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;
import cn.arvix.vrdata.service.FetchDataService;
import cn.arvix.vrdata.service.JpaShareService;
import cn.arvix.vrdata.service.UploadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanghaiyang on 16/4/28.
 */
@Service
public class AutoSyncAndUpdateTask {
    @Autowired
    JpaShareService jpaShareService;
    @Autowired
    FetchDataService fetchDataService;
    @Autowired
    UploadDataService uploadDataService;
    @Autowired
    private SyncTaskContentRepository syncTaskContentRepository;

    public void init() {
        List<SyncTaskContent> syncTaskContentList = syncTaskContentRepository.getWaitingTask();

        List<SyncTaskContent> fetchList = new ArrayList<SyncTaskContent>();
        List<SyncTaskContent> updateList = new ArrayList<SyncTaskContent>();

        for (SyncTaskContent syncTaskContent : syncTaskContentList) {
            SyncTaskContent.TaskType taskType = syncTaskContent.getTaskType();
            if (syncTaskContent.getTaskStatus() == SyncTaskContent.TaskStatus.WAIT) {
                if (taskType == SyncTaskContent.TaskType.FETCH || taskType == SyncTaskContent.TaskType.FETCH_UPDATE) {
                    fetchList.add(syncTaskContent);
                } else if (taskType == SyncTaskContent.TaskType.UPDATE) {
                    updateList.add(syncTaskContent);
                }
            }
        }

        processFetch(fetchList);
        processUpdate(updateList);
    }

    public void addToWait(Runnable worker) {

    }

    private void processFetch(final List<SyncTaskContent> syncTaskContentList) {
        System.err.println("Fetch task: " + syncTaskContentList);
        for (SyncTaskContent syncTaskContent : syncTaskContentList) {
            fetchDataService.fetch(syncTaskContent.getSourceUrl(), syncTaskContent.getDstUrl(), false, syncTaskContent);
        }
    }

    private void processUpdate(final List<SyncTaskContent> syncTaskContentList) {
        System.err.println("Update Task: " + syncTaskContentList);
        for (SyncTaskContent syncTaskContent : syncTaskContentList) {
            uploadDataService.uploadData(syncTaskContent.getSourceUrl(), syncTaskContent.getDstUrl(), syncTaskContent);
        }
    }
}
