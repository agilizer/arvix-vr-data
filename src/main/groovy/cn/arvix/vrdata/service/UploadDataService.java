package cn.arvix.vrdata.service;

import java.util.Map;

import cn.arvix.vrdata.domain.SyncTaskContent;

/**
 * Created by wanghaiyang on 16/4/26.
 */
public interface UploadDataService {

    public Map<String, Object> uploadData(SyncTaskContent syncTaskContent);
}
