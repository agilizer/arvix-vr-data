package cn.arvix.matterport.service;

import cn.arvix.matterport.domain.ConfigDomain;

public interface ConfigDomainUpdateListener {
    String getConfigMapName();
    void runNotify(ConfigDomain configDomain);

}