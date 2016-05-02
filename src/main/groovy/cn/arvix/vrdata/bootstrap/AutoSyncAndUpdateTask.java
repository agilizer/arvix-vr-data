package cn.arvix.vrdata.bootstrap;

import cn.arvix.vrdata.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private static final int MAX_THREAD_NUM = 5;
    private List<Thread> fetchThreadList = new ArrayList<Thread>();
    private List<Thread> updateThreadList = new ArrayList<Thread>();
    private boolean fetchToggle = true;
    private boolean updateToggle = true;

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
        //服务启动时可能会开启过多线程,开启线程数量限制
        new Thread() {
            @Override
            public void run() {
                for (String url : urls) {
                    //直到允许开启新任务
                    while (!fetchToggle) {
                        for (int i = 0 ; i < fetchThreadList.size() ; i++) {
                            Thread worker = fetchThreadList.get(i);
                            if (!worker.isAlive()) {
                                fetchThreadList.remove(worker);
                            }
                        }
                        if (fetchThreadList.size() < MAX_THREAD_NUM) {
                            fetchToggle = true;
                            break;
                        }
                        try {
                            sleep(4000);
                        } catch (InterruptedException e) {
                            //ignore
                        }
                    }

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
                        try {
                            Map<String, Object> result = fetchDataService.fetch(sourceUrl, dstUrl, false);

                            Thread worker = (Thread) result.get("WORKER");
                            if (worker != null) {
                                fetchThreadList.add(worker);
                                if (fetchThreadList.size() >= MAX_THREAD_NUM) {
                                    //暂停开启新任务
                                    fetchToggle = false;
                                }
                            }
                            System.err.println("开启新任务(FETCH)" + sourceUrl + ", " + dstUrl + ", " + fetchToggle);
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
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    //update操作需要通过http调用自身服务器的接口,需要等待服务器完全启动后再开启.
                }
                for (String url : urls) {
                    //直到允许开启新任务
                    while (!updateToggle) {
                        for (int i = 0 ; i < updateThreadList.size() ; i++) {
                            Thread worker = updateThreadList.get(i);
                            if (!worker.isAlive()) {
                                updateThreadList.remove(worker);
                            }
                        }
                        if (updateThreadList.size() < MAX_THREAD_NUM) {
                            updateToggle = true;
                            break;
                        }
                        try {
                            TimeUnit.SECONDS.sleep(4);
                        } catch (InterruptedException e) {
                            //ignore
                        }
                    }

                    String sourceUrl = null;
                    String dstUrl = null;
                    if (url.contains("|")) {
                        String[] arr = url.split("\\|");
                        if (arr != null && arr.length > 1) {
                            sourceUrl = arr[0];
                            dstUrl = arr[1];
                            try {
                                Map<String, Object> result = uploadDataService.uploadData(sourceUrl, dstUrl, null);

                                Thread worker = (Thread) result.get("WORKER");
                                if (worker != null) {
                                    updateThreadList.add(worker);
                                    if (updateThreadList.size() >= MAX_THREAD_NUM) {
                                        //暂停开启新任务
                                        updateToggle = false;
                                    }
                                }
                                System.err.println("开启新任务(UPDATE)" + sourceUrl + ", " + dstUrl + ", " + updateToggle);
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
