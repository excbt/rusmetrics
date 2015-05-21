package ru.excbt.datafuse.nmk.config.jpa;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableTransactionManagement
@EnableJpaRepositories("ru.excbt.datafuse.nmk.data.repository")
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data" })
@EnableJpaAuditing(auditorAwareRef = "mockAuditorAware")
public class JpaTestConfiguration {

	@Value("${dataSource.driverClassName}")
	private String driverClassname;

	@Value("${dataSource.url}")
	private String datasourceUrl;

	@Value("${dataSource.username}")
	private String datasourceUsername;

	@Value("${dataSource.password}")
	private String datasourcePassword;

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		checkNotNull(driverClassname);
		checkNotNull(datasourceUrl);
		checkNotNull(datasourceUsername);
		checkNotNull(datasourcePassword);

		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(driverClassname);
		dataSource.setUrl(datasourceUrl);
		dataSource.setUsername(datasourceUsername);
		dataSource.setPassword(datasourcePassword);
		dataSource.setMaxWait(100);
		dataSource.setMaxActive(10);
		return dataSource;
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean
	public DefaultPersistenceUnitManager persistentUnitManager() {
		DefaultPersistenceUnitManager pu = new DefaultPersistenceUnitManager();
		pu.setPersistenceXmlLocation("classpath*:META-INF/persistence-test.xml");
		pu.setDefaultDataSource(dataSource());
		return pu;
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitManager(persistentUnitManager());
		return emf;
	}

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public JasperDatabaseConnectionSettings jasperDatabaseConnectionSettings() {
		return new JasperDatabaseConnectionSettings() {

			@Override
			public String getDatasourceUrl() {
				return datasourceUrl;
			}

			@Override
			public String getDatasourceUsername() {
				return datasourceUsername;
			}

			@Override
			public String getDatasourcePassword() {
				return datasourcePassword;
			}
		};
	}

}
