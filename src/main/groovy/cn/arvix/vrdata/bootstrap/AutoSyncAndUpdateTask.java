package cn.arvix.vrdata.bootstrap;

import cn.arvix.vrdata.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

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

    public void init() {
        String hql = "select c.mapValue from ConfigDomain c where c.mapName like '" + FetchDataServiceImpl.FETCH_PREFIX + "%'";
        List<?> fetchList = jpaShareService.queryForHql(hql, new HashMap<String, Object>());

        String hql2 = "select c.mapValue from ConfigDomain c where c.mapName like '" + UploadDataServiceImpl.UPDATE_PREFIX + "%'";
        List<?> updateList = jpaShareService.queryForHql(hql2, new HashMap<String, Object>());

        processFetch((List<String>)fetchList);
        processUpdate((List<String>)updateList);
    }

    private void processFetch(final List<String> urls) {
        System.err.println("Fetch task: " + urls);
        //服务启动时可能会开启过多线程
        new Thread() {
            @Override
            public void run() {
                for (String url : urls) {
                    String sourceUrl = null;
                    String dstUrl = null;
                    if (url.contains("|")) {
                        String[] arr = url.split("\\|");
                        if (arr != null && arr.length > 1) {
                            sourceUrl = arr[0];
                            dstUrl = arr[1];
                        }
                    } else {
                        sourceUrl = url;
                    }
                    if (sourceUrl != null && sourceUrl.length() > 15) {
                        System.out.println(sourceUrl + ", " + dstUrl);
                        try {
                            fetchDataService.fetch(sourceUrl, dstUrl, false);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }
            }
        }.start();
    }

    private void processUpdate(final List<String> urls) {
        System.err.println("Update Task: " + urls);
        new Thread() {
            @Override
            public void run() {
                for (String url : urls) {
                    String sourceUrl = null;
                    String dstUrl = null;
                    if (url.contains("|")) {
                        String[] arr = url.split("\\|");
                        if (arr != null && arr.length > 1) {
                            sourceUrl = arr[0];
                            dstUrl = arr[1];
                            try {
                                uploadDataService.uploadData(sourceUrl, dstUrl, null);
                            } catch (Exception e) {
                                //ignore
                            }
                        }
                    }
                }
            }
        }.start();
    }
}
