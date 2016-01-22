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
		
		
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.NGINX_URL)){
			saveCon(ArvixMatterportConstants.NGINX_URL,"http://127.0.0.1/"
					,true,"文件存储路径",ValueType.String)
		}
		
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.NGINX_FILE_PATH)){
			saveCon(ArvixMatterportConstants.NGINX_FILE_PATH,"D:/openSource/nginx-1.9.9/html/"
					,true,"文件存储路径",ValueType.String)
		}
		
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.SITE_URL)){
			saveCon(ArvixMatterportConstants.SITE_URL,"http://127.0.0.1:8888/"
					,true,"网站访问路径",ValueType.String)
		}
		
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.API_UPLOAD_MODELDATA_KEY)){
			saveCon(ArvixMatterportConstants.API_UPLOAD_MODELDATA_KEY,"c2654aa9-f432-49a7-9dd6-524518beeea1"
					,true,"上传数据时访问key",ValueType.String)
		}
		
		
		
		
		if(!configDomainRepository.findByMapName(ArvixMatterportConstants.TEAM_DESCRIPTION)){
			saveCon(ArvixMatterportConstants.TEAM_DESCRIPTION,"""
<h1>团队介绍</h1><p><h3 style="color:red">北京唯幻科技有限公司</h3>是一家采用3维VR（虚拟现实技术）为房地产行业提供虚拟现实、增强性业务综合解决方案的一家创新型互联网公司。

                            		身由苏黎世大学计算机海归博士郭奕先生及知名投资人成立于2012年，为国内多家央企、国企搭建传统行业的互联网 的平台

                            		和升级方案。 目前与合伙人共同成立"北京唯幻科技有限公司"，并且完成A轮的融资。
"""
					,true,"团队介绍",ValueType.String)
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
