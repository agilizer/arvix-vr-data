package cn.arvix.vrdata;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for multi-part uploads.
 * Adds a {@link StandardServletMultipartResolver} if none is present, and adds
 * a {@link javax.servlet.MultipartConfigElement multipartConfigElement} if none
 * is otherwise defined. The {@link EmbeddedWebApplicationContext} will
 * associate the {@link MultipartConfigElement} bean to any {@link Servlet}
 * beans.
 * <p>
 * The {@link javax.servlet.MultipartConfigElement} is a Servlet API that's used
 * to configure how the container handles file uploads. By default
 *
 * @author Greg Turnquist
 * @author Josh Long
 */
@Configuration
@ConditionalOnProperty(prefix = "multipart", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class MultipartConfiguration {
	private static final Logger log = LoggerFactory
			.getLogger(MultipartConfiguration.class);
	@Autowired
	private MultipartProperties multipartProperties = new MultipartProperties();

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		log.info(
				"MultipartConfiguration ----------------------> maxFileSize {} ,maxRequestSize {}",
				multipartProperties.getMaxFileSize(),
				multipartProperties.getMaxRequestSize());
		return this.multipartProperties.createMultipartConfig();
	}

	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

}
