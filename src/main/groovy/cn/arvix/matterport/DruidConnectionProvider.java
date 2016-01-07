/**
 * 
 */
package cn.arvix.matterport;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * https://github.com/alibaba/druid/issues/592
 * 
 * <pre>
 * Druid通过实现Hibernate接口ConnectionProvider提供对hibernate的支持：
 * public class DruidConnectionProvider implements ConnectionProvider, Configurable,Stoppable {
 * 在最新版中Druid1.0.6中，继承的是org.hibernate.service.jdbc.connections.spi.ConnectionProvider,
 * Hibernate4.3的ConnectionProvider接口声明已经被修改为：
 * org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 * </pre>
 * 
 * 在hibernate.properties中不使用Druid的ConnectionProvider，而使用重新定义的ConnectionProvider：
 * 
 * <pre>
 * <i>
 * 	#Druid::注意这里配置的ConnectionProvider！！！ 第一次，配置错误了导致直接加不成功的异常！！
 * 	#hibernate.connection.provider_class=com.alibaba.druid.support.hibernate.DruidConnectionProvider
 * </i>
 * </pre>
 * 
 * @author 呙长伟
 * @date 2014年7月16日
 */
public class DruidConnectionProvider implements ConnectionProvider, Configurable, Stoppable {
	private static final Logger logger = LoggerFactory.getLogger(DruidConnectionProvider.class);
	private static final long serialVersionUID = 1026193803901107651L;

	private DruidDataSource dataSource;
	private static final String CONFIG_FILE_PREFIX = "/hibernate-";
	/**
	 * 在vm加入参数指定添加的hibernate配置文件.该配置文件会覆盖hibernate.properties中对应配置. 参数名为:
	 * db.conf.value 例如vm加入参数 -Ddb.conf.value=dev 则对应的增加配置文件名为:
	 * hibernate-dev.properties
	 */
	private static final String CONFIG_VAR_NAME = "db.conf.value";

	public DruidConnectionProvider() {
		dataSource = new DruidDataSource();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return dataSource.isWrapperFor(unwrapType);
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return dataSource.unwrap(unwrapType);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	private Map propertiesToMap(Properties properties) {
		Map<String, String> map = new HashMap<String, String>();
		for (final String name : properties.stringPropertyNames())
			map.put(name, properties.getProperty(name));
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void configure(Map configurationValues) {
		String configValue = System.getProperty(CONFIG_VAR_NAME);
		String fileName = "/hibernate.properties";
		if (null != configValue) {
			fileName = CONFIG_FILE_PREFIX + configValue + ".properties";
		}
		Properties tempProperties = new Properties();
		InputStream stream = ConfigHelper.getResourceAsStream(fileName);
		try {
			tempProperties.load(stream);
			configurationValues.putAll(propertiesToMap(tempProperties));
			logger.info("load hibernate config from file:" + fileName + " success");
		} catch (Exception e) {
			logger.warn("load properties form config wrong", e);
		} finally {
			try {
				stream.close();
			} catch (IOException ioe) {
				logger.error("load properties form config io error", ioe);
			}
		}

		logger.info("DruidDataSource init:: " + configurationValues);
		try {
			DruidDataSourceFactory.config(dataSource, configurationValues);
		} catch (SQLException e) {
			throw new IllegalArgumentException("config error", e);
		}
	}

	@Override
	public void stop() {
		dataSource.close();
	}

}
