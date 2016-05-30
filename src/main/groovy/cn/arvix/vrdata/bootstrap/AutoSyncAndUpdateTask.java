package cn.arvix.vrdata.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.domain.SyncTaskContent;
import cn.arvix.vrdata.repository.SyncTaskContentRepository;
import cn.arvix.vrdata.service.JpaShareService;
import cn.arvix.vrdata.service.SyncTaskContentService;

/**
 * Created by wanghaiyang on 16/4/28.
 */
@Service
public class AutoSyncAndUpdateTask {
    @Autowired
    JpaShareService jpaShareService;
    @Autowired
    SyncTaskContentService syncTaskContentService;
    @Autowired
    private SyncTaskContentRepository syncTaskContentRepository;

    public void init() {
        List<SyncTaskContent> syncTaskContentList = syncTaskContentRepository.getRestartTask();
        for (SyncTaskContent syncTaskContent : syncTaskContentList) {
        	syncTaskContentService.excute(syncTaskContent);
        }
    }

    public void addToWait(Runnable worker) {

    }
}
