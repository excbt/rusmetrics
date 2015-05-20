package ru.excbt.datafuse.nmk.config.jpa;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManagerFactory;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ru.excbt.datafuse.nmk.config.PropertyConfig;

@Configuration
@Import({ PropertyConfig.class})
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableTransactionManagement
@EnableJpaRepositories("ru.excbt.datafuse.nmk.data.repository")
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data" })
public class JpaConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(JpaConfig.class);

	@Value("${dataSource.driverClassName}")
	private String driverClassname;

	@Value("${dataSource.url}")
	private String datasourceUrl;

	@Value("${dataSource.username}")
	private String datasourceUsername;

	@Value("${dataSource.password}")
	private String datasourcePassword;

	/**
	 * org.apache.tomcat.jdbc.pool.DataSource
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		checkNotNull(driverClassname);
		checkNotNull(datasourceUrl);
		checkNotNull(datasourceUsername);
		checkNotNull(datasourcePassword);

		DataSource dataSource = new DataSource();
		dataSource.setDriverClassName(driverClassname);
		dataSource.setUrl(datasourceUrl);
		dataSource.setUsername(datasourceUsername);
		dataSource.setPassword(datasourcePassword);
		dataSource.setMaxWait(100);
		dataSource.setMaxActive(30);
		dataSource.setMinIdle(10);
		dataSource.setMaxIdle(20);
		return dataSource;
	}

	@Bean
	public DefaultPersistenceUnitManager persistentUnitManager() {
		DefaultPersistenceUnitManager pu = new DefaultPersistenceUnitManager();
		pu.setPersistenceXmlLocation("classpath*:META-INF/persistence.xml");
		pu.setDefaultDataSource(dataSource());
		return pu;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitManager(persistentUnitManager());
		emf.setPersistenceUnitName("nmk-p");
		return emf;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	public String getDatasourceUrl() {
		return datasourceUrl;
	}

	public String getDatasourceUsername() {
		return datasourceUsername;
	}

	public String getDatasourcePassword() {
		return datasourcePassword;
	}

}
