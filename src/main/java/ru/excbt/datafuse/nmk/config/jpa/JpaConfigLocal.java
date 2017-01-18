package ru.excbt.datafuse.nmk.config.jpa;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.Data;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal.PortalDBProps;

//@Configuration
//@PropertySource(value = "classpath:META-INF/data-access.properties")
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = { "ru.excbt.datafuse.nmk.data.repository" })
//@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data", "ru.excbt.datafuse.nmk.slog" })
//@EnableJpaAuditing(auditorAwareRef = "mockAuditorAware")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.nmk.data.repository",
		entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data", "ru.excbt.datafuse.nmk.slog" })
@EnableConfigurationProperties(value = { PortalDBProps.class })
//@EnableJpaAuditing(auditorAwareRef = "mockAuditorAware")
public class JpaConfigLocal {

	@Autowired
	private Environment env;

	@Data
	@ConfigurationProperties(prefix = "portal.datasource")
	public static class PortalDBProps {
		private String type;
		private String url;
		private String username;
		private String password;
	}

	/**
	 * 
	 * @return
	 */
	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties("portal.datasource")
	public DataSource dataSource() {
		//		if (HikariDataSource.class.getName().equals(env.getProperty("portal.datasource.type"))) {
		//			final HikariDataSource ds = new HikariDataSource();
		//			ds.setMaximumPoolSize(25);
		//			ds.addDataSourceProperty("url", env.getProperty("portal.datasource.url"));
		//			ds.addDataSourceProperty("user", env.getProperty("portal.datasource.username"));
		//			ds.addDataSourceProperty("password", env.getProperty("portal.datasource.password"));
		//			//ds.setMetricRegistry(metricRegistry);
		//			return ds;
		//		} else {
		return DataSourceBuilder.create().build();
		//}
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
	//	@Bean(name = "entityManagerFactory")
	//	public EntityManagerFactory entityManagerFactory() {
	//
	//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	//
	//		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	//		emf.setJpaVendorAdapter(vendorAdapter);
	//		emf.setPersistenceUnitName("nmk-p");
	//		Properties hibernateProperties = HibernateProps.readEnvProps(env, "dataSource");
	//		emf.setJpaProperties(hibernateProperties);
	//		emf.setDataSource(dataSource());
	//		emf.setPackagesToScan("ru.excbt.datafuse.nmk.data.model");
	//		emf.afterPropertiesSet();
	//		return emf.getObject();
	//	}
	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {

		return builder.dataSource(dataSource).packages("ru.excbt.datafuse.nmk.data.model").persistenceUnit("nmk-p")
				.build();
	}

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	//	@Bean(name = "transactionManager")
	//	public PlatformTransactionManager transactionManager() {
	//		JpaTransactionManager transactionManager = new JpaTransactionManager();
	//		transactionManager.setEntityManagerFactory(entityManagerFactory());
	//		return transactionManager;
	//	}

	@Primary
	@Bean(name = "transactionManager")
	@Autowired
	public PlatformTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	/**
	 * 
	 * @return
	 */
	//	@Bean
	//	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	//		return new PersistenceExceptionTranslationPostProcessor();
	//	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public JasperDatabaseConnectionSettings jasperDatabaseConnectionSettings() {
		return new JasperDatabaseConnectionSettings() {

			private final String url = env.getProperty("spring.datasource.url");
			private final String username = env.getProperty("spring.datasource.username");
			private final String password = env.getProperty("spring.datasource.password");

			@Override
			public String getDatasourceUrl() {
				return url;
			}

			@Override
			public String getDatasourceUsername() {
				return username;
			}

			@Override
			public String getDatasourcePassword() {
				return password;
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
