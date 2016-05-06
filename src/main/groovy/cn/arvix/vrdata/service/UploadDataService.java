package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;
import cn.arvix.vrdata.domain.SyncTaskContent;

import java.util.Map;

/**
 * Created by wanghaiyang on 16/4/26.
 */
public interface UploadDataService {

    public Map<String, Object> uploadData(String sourceUrl, String dstUrl, SyncTaskContent.TaskLevel taskLevel, Status status);
    public Map<String, Object> uploadData(String sourceUrl, String dstUrl, SyncTaskContent syncTaskContent);
    public Status getCaseStatus(String caseId);
}
