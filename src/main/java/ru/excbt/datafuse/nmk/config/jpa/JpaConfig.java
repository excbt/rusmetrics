package ru.excbt.datafuse.nmk.config.jpa;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
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
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfig {

	@Autowired
	private Environment env;

	/**
	 * 
	 * @return
	 */
	@Bean(name = "datasource")
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource(env.getProperty("dataSource.jndi"));
		return dataSource;
	}

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
	 * @param emf
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
