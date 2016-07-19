package ru.excbt.datafuse.nmk.config.jpa;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.raw.data.repository",
		entityManagerFactoryRef = "entityManagerFactoryRaw", transactionManagerRef = "transactionManagerRaw")
@ComponentScan(basePackages = { "ru.excbt.datafuse.raw.data" })
public class JpaRawConfigLocal {

	@Autowired
	private Environment env;

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "dataSourceRaw", destroyMethod = "")
	public DataSource dataSourceRaw() {

		PGPoolingDataSource source = new PGPoolingDataSource();
		source.setUrl(env.getProperty("dataSourceRaw.url"));
		source.setUser(env.getProperty("dataSourceRaw.username"));
		source.setPassword(env.getProperty("dataSourceRaw.password"));
		source.setMaxConnections(10);
		return source;
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
		Properties hibernateProperties = HibernateProps.readEnvProps(env,
				"dataSourceRaw");
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
	@Bean(name = "transactionManagerRaw")
	@Autowired
	public PlatformTransactionManager transactionManagerRaw() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryRaw());
		return transactionManager;
	}

}
