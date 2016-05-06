package cn.arvix.vrdata;

import cn.arvix.vrdata.service.ShareService;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"cn.arvix.vrdata"})
public class WebAppConfig extends WebMvcConfigurerAdapter {
	private static final Logger log = LoggerFactory
			.getLogger(WebAppConfig.class);
    @Autowired
	ShareService shareService;
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    // Maps resources path to webapp/resources
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	/**
    	 * 设置js css文件路径
    	 */
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/resources/");
		registry.addResourceHandler("/upload/**").addResourceLocations("classpath:/files/upload/");
    }

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    // Provides internationalization of messages
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        return source;
    }
    @Bean
    org.h2.tools.Server h2Server() {
    	log.info("env---------------->WebAppConfig--->"+shareService.getEnv());
    	if(shareService.devEnv()){
    		   Server server = new Server();
    	        try {
    	            server.runTool("-tcp");
    	            server.runTool("-tcpAllowOthers");
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	        return server;
    	}else{
    		return null;
    	}
    }
    @Value("${sync.taskExecutor.corePoolSize}")
    private int corePoolSize;
    @Value("${sync.taskExecutor.maxPoolSize}")
    private int maxPoolSize;
    @Value("${sync.taskExecutor.queueCapacity}")
    private int queueCapacity;
    @Value("${sync.taskExecutor.keepAliveSeconds}")
    private int keepAliveSeconds;
    @Bean(name = "syncTaskExecutor")
    ThreadPoolTaskExecutor syncTaskExecutor(){
    	ThreadPoolTaskExecutor taskExecutor  = new ThreadPoolTaskExecutor();
    	taskExecutor.setCorePoolSize(corePoolSize);
    	taskExecutor.setMaxPoolSize(maxPoolSize);
    	taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
    	taskExecutor.setQueueCapacity(queueCapacity);
    	return taskExecutor;
    }
   
}
