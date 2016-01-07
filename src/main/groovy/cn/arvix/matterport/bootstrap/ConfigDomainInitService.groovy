package cn.arvix.matterport.bootstrap

import javax.servlet.ServletContext

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import cn.arvix.matterport.consants.ArvixMatterportConstants
import cn.arvix.matterport.domain.ConfigDomain
import cn.arvix.matterport.domain.ConfigDomain.ValueType
import cn.arvix.matterport.repository.ConfigDomainRepository
import cn.arvix.matterport.service.ConfigDomainService
import cn.arvix.matterport.util.StaticMethod

@Service
class ConfigDomainInitService {
	@Autowired
	ServletContext servletContext;
	@Autowired
	ConfigDomainRepository configDomainRepository;
	@Autowired
	ConfigDomainService configDomainService;
	void init(boolean isDev){
		initDefaultConfig(isDev)
		def map = [:]
		configDomainRepository.findAll().each {
			StaticMethod.addToConfigMap(map, it)
		}
		servletContext.setAttribute(ConfigDomainService.APPLICATION_KEY_NAME, map)
		configDomainService.init(map)
	}
	private void initDefaultConfig(boolean isDev){
		def configDomain
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.FILE_STORE_PATH)){
			saveCon(ArvixMatterportConstants.FILE_STORE_PATH,"/home/abel/arvix-test-files/"
					,true,"文件存储路径",ValueType.String)
		}
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.SITE_URL)){
			saveCon(ArvixMatterportConstants.SITE_URL,"http://127.0.0.1:8888/"
					,true,"网站访问路径",ValueType.String)
		}
		}
	private void saveCon(String mapName,String mapValue,boolean editAble,String desc,ValueType valueType){
		def configDomain = new ConfigDomain()
		configDomain.setEditable(editAble)
		configDomain.setDescription(desc)
		configDomain.setMapName(mapName)
		configDomain.setMapValue(mapValue);
		configDomain.setValueType(valueType)
		configDomainRepository.saveAndFlush(configDomain)
	}
}
