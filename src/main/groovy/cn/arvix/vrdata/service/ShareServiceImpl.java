package cn.arvix.vrdata.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ShareServiceImpl implements ShareService , ApplicationContextAware{

	
	
	private ApplicationContext applicationContext;
	private static String ENV="";
	private static boolean isDev = false; 
	
	@PostConstruct
	public void init(){
		String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
		ENV = org.apache.commons.lang3.StringUtils.join(profiles);
		if(ENV.indexOf("dev")>-1){
			isDev = true;
		}
	}
	
	
	@Override
	public String getEnv() {
		return ENV;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Override
	public boolean devEnv() {
		return isDev;
	}

}
