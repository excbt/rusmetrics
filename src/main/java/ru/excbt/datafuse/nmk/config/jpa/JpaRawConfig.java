package ru.excbt.datafuse.nmk.config.jpa;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.raw.data.repository", entityManagerFactoryRef = "entityManagerFactoryRaw")
@ComponentScan(basePackages = { "ru.excbt.datafuse.raw.data" })
public class JpaRawConfig {

	@Autowired
	private Environment env;
	
	
	/**
	 * 
	 * @return
	 */
	@Bean(name = "dataSourceRaw")
	public DataSource dataSourceRaw() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource(env
				.getProperty("dataSourceRaw.jndi"));
		return dataSource;
	}	
	
	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "entityManagerFactoryRaw")
	public EntityManagerFactory entityManagerFactoryRaw() {

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setPersistenceUnitName("dataraw");
		Properties hibernateProperties = HibernateProps.readEnvProps(env, "dataSourceRaw");
		emf.setJpaProperties(hibernateProperties);
		emf.setDataSource(dataSourceRaw());
		emf.setPackagesToScan("ru.excbt.datafuse.raw.data.model");
		emf.afterPropertiesSet();
		return emf.getObject();
	}	

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
//	@Bean(name = "transactionManagerRaw")
//	public PlatformTransactionManager transactionManagerRaw() {
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(entityManagerFactoryRaw());
//		return transactionManager;
//	}

}
