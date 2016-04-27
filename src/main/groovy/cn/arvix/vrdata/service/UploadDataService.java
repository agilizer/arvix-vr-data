package cn.arvix.vrdata.service;

import cn.arvix.vrdata.been.Status;

import java.util.Map;

/**
 * Created by wanghaiyang on 16/4/26.
 */
public interface UploadDataService {

    public Map<String, Object> uploadData(String serverUrl, String dstUrl);
    public Status getCaseStatus(String caseId);
}
