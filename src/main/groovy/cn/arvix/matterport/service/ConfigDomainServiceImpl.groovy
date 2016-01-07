package cn.arvix.matterport.service;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import cn.arvix.matterport.domain.ConfigDomain
import cn.arvix.matterport.repository.ConfigDomainRepository
import cn.arvix.matterport.util.StaticMethod


@Service
public class ConfigDomainServiceImpl implements ConfigDomainService{
	private static final Logger log = LoggerFactory
			.getLogger(ConfigDomainServiceImpl.class);
	Map configMap ;
	@Autowired
	ConfigDomainRepository configDomainRepository;
	List<ConfigDomainUpdateListener> updateListenerList = new ArrayList<ConfigDomainUpdateListener>()
	
	public void init(Map configMap){
		this.configMap = configMap;
	}
	
	@Override
	public List<ConfigDomain> list() {
		return configDomainRepository.findAll();
	}

	@Override
	public int getConfigInt(String configName) {
		Object obj = configMap.get(configName);
		if(null==obj){
			throw new IllegalArgumentException("not found configName "+configName);
		}else{
			return (int)obj;
		}
	}

	@Override
	public boolean getConfigBoolean(String configName) {
		return (boolean)configMap.get(configName);
	}

	@Override
	public String getConfigString(String configName) {
		return (String)configMap.get(configName);
	}

	@Override
	public Date getConfigDate(String configName) {
		return (Date)configMap.get(configName);
	}

	@Override
	public Map<String, Object> update(Long id, String value) {
		ConfigDomain configDomain = configDomainRepository.findOne(id);
		Map<String, Object> result = StaticMethod.genResult()
		if(configDomain!=null){
			configDomain.setMapValue(value);
			StaticMethod.addToConfigMap(configMap, configDomain)
			configDomainRepository.save(configDomain)
			result.put(ZeroBarConstants.SUCCESS, true)
			updateListenerList.each {
				if(it.getConfigMapName()&&it.getConfigMapName()==configDomain.mapName){
					it.runNotify(configDomain)
					log.info("change config : "+ it.getConfigMapName() +"  to value:" + configDomain.mapValue) ;
				}
			}
		}else{
			result.put(ZeroBarConstants.ERROR_MSG, "没有找到相关配置项！")
		}
		return result
	}
	public void addUpdateListener(ConfigDomainUpdateListener updateListener){
		updateListenerList.add(updateListener);
	}

	@Override
	public Object getConfig(String configName) {
		return configMap.get(configName);
	}
}
