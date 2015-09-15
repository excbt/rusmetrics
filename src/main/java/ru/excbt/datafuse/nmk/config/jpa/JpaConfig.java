package ru.excbt.datafuse.nmk.config.jpa;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = {
		"ru.excbt.datafuse.nmk.data.repository",
		"ru.excbt.datafuse.raw.data.repository" })
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data",
		"ru.excbt.datafuse.raw.data" })
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfig {

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
	@Bean
	@Primary
	public DefaultPersistenceUnitManager persistentUnitManager()
			throws NamingException {
		DefaultPersistenceUnitManager pu = new DefaultPersistenceUnitManager();
		pu.setPersistenceXmlLocation("classpath*:META-INF/persistence.xml");
		return pu;
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "entityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()
			throws NamingException {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitManager(persistentUnitManager());
		emf.setPersistenceUnitName("nmk-p");
		return emf;
	}

	/**
	 * 
	 * @param emf
	 * @return
	 */
	@Bean
	@Autowired
	@Primary
	public PlatformTransactionManager transactionManager(
			@Value("#{entityManagerFactory}") EntityManagerFactory entityManagerFactory) {
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
