package ru.excbt.datafuse.nmk.config.jpa;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.raw.data.repository",
		entityManagerFactoryRef = "entityManagerFactoryRaw", transactionManagerRef = "transactionManagerRaw")
@ComponentScan(basePackages = { "ru.excbt.datafuse.raw" })
public class JpaRawConfigLocal {

	@Autowired
	private Environment env;

	/**
	 * 
	 * @return
	 */
	@Bean(name = "dataSourceRaw")
	@ConfigurationProperties(prefix = "raw.datasource")
	public DataSource dataSourceRaw() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * 
	 * @return
	 * @throws NamingException
	 */
	@Bean(name = "entityManagerFactoryRaw")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryRaw(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSourceRaw") DataSource dataSource) {

		return builder.dataSource(dataSource).packages("ru.excbt.datafuse.raw.data.model").persistenceUnit("dataraw")
				.build();
	}

	/**
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean(name = "transactionManagerRaw")
	@Autowired
	public PlatformTransactionManager transactionManagerRaw(
			@Qualifier("entityManagerFactoryRaw") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/**
	 * 
	 * @return
	 */
	//	@Bean
	//	public JasperDatabaseConnectionSettings jasperDatabaseConnectionSettings() {
	//		return new JasperDatabaseConnectionSettings() {
	//
	//			@Override
	//			public String getDatasourceUrl() {
	//				return env.getProperty("jasper.dataSource.url");
	//			}
	//
	//			@Override
	//			public String getDatasourceUsername() {
	//				return env.getProperty("jasper.dataSource.username");
	//			}
	//
	//			@Override
	//			public String getDatasourcePassword() {
	//				return env.getProperty("jasper.dataSource.password");
	//			}
	//		};
	//	}

}
