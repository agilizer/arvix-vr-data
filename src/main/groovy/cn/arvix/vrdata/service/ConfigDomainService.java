package cn.arvix.vrdata.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.arvix.vrdata.domain.ConfigDomain;

public interface ConfigDomainService {
	String APPLICATION_KEY_NAME="configMap";
	/**
	 * 
	 * @param id configDomain id
	 * @param value date:yyyy-MM-dd; boolean true or false.
	 */
	Map<String, Object> update(Long id,String value);
	List<ConfigDomain> list();
	int getConfigInt(String configName);
	boolean getConfigBoolean(String configName);
	String getConfigString(String configName);
	Date getConfigDate(String configName);
	Object getConfig(String configName);
	void addUpdateListener(ConfigDomainUpdateListener updateListener);
	void init(Map configMap);
}
