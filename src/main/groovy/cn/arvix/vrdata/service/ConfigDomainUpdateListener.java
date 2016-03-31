package cn.arvix.vrdata.service;

import cn.arvix.vrdata.domain.ConfigDomain;

public interface ConfigDomainUpdateListener {
    String getConfigMapName();
    void runNotify(ConfigDomain configDomain);

}