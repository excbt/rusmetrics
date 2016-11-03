package ru.excbt.datafuse.nmk.config.jpa;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "ru.excbt.datafuse.nmk.data.repository" })
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data" })
@EnableJpaAuditing(auditorAwareRef = "mockAuditorAware")
public class JpaConfigLocal {

	@Autowired
	private Environment env;

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "dataSource", destroyMethod = "")
	public DataSource dataSource() {

		PGPoolingDataSource source = new PGPoolingDataSource();
		source.setUrl(env.getProperty("dataSource.url"));
		source.setUser(env.getProperty("dataSource.username"));
		source.setPassword(env.getProperty("dataSource.password"));
		source.setMaxConnections(10);
		return source;
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	//	@Bean
	//	public DefaultPersistenceUnitManager persistentUnitManager() {
	//		DefaultPersistenceUnitManager pu = new DefaultPersistenceUnitManager();
	//		pu.setPersistenceXmlLocation("classpath*:META-INF/persistence-local.xml");
	//		pu.setDefaultDataSource(dataSource());
	//		return pu;
	//	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "entityManagerFactory")
	public EntityManagerFactory entityManagerFactory() {

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setPersistenceUnitName("nmk-p");
		Properties hibernateProperties = HibernateProps.readEnvProps(env, "dataSource");
		emf.setJpaProperties(hibernateProperties);
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("ru.excbt.datafuse.nmk.data.model");
		emf.afterPropertiesSet();
		return emf.getObject();
	}

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory());
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
				return env.getProperty("dataSource.url");
			}

			@Override
			public String getDatasourceUsername() {
				return env.getProperty("dataSource.username");
			}

			@Override
			public String getDatasourcePassword() {
				return env.getProperty("dataSource.password");
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
