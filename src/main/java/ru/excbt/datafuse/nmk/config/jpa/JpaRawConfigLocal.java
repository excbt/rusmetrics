package ru.excbt.datafuse.nmk.config.jpa;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = "classpath:META-INF/data-access.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.raw.data.repository", entityManagerFactoryRef = "entityManagerFactoryRaw")
@ComponentScan(basePackages = { "ru.excbt.datafuse.raw.data" })
public class JpaRawConfigLocal {

	@Value("${dataSourceRaw.driverClassName}")
	private String driverClassname;

	@Value("${dataSourceRaw.url}")
	private String datasourceUrl;

	@Value("${dataSourceRaw.username}")
	private String datasourceUsername;

	@Value("${dataSourceRaw.password}")
	private String datasourcePassword;

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(destroyMethod = "close")
	@Primary
	public DataSource dataSourceRaw() {
		checkNotNull(driverClassname);
		checkNotNull(datasourceUrl);
		checkNotNull(datasourceUsername);
		checkNotNull(datasourcePassword);

		PGPoolingDataSource source = new PGPoolingDataSource();
		source.setUrl(datasourceUrl);
		source.setUser(datasourceUsername);
		source.setPassword(datasourcePassword);
		source.setMaxConnections(10);

		return source;
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "entityManagerFactoryRaw")
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryRaw(
			DefaultPersistenceUnitManager persistentUnitManager) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitManager(persistentUnitManager);
		emf.setPersistenceUnitName("dataraw");
		return emf;
	}

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManagerRaw(
			@Value("#{entityManagerFactoryRaw}") EntityManagerFactory entityManagerFactoryRaw) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryRaw);
		return transactionManager;
	}

}
