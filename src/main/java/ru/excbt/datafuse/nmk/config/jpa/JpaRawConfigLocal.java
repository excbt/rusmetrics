package ru.excbt.datafuse.nmk.config.jpa;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import lombok.Data;
import ru.excbt.datafuse.nmk.config.jpa.JpaRawConfigLocal.RawDBProps;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.excbt.datafuse.raw.data.repository",
		entityManagerFactoryRef = "entityManagerFactoryRaw", transactionManagerRef = "transactionManagerRaw")
@ComponentScan(basePackages = { "ru.excbt.datafuse.raw" })
@EnableConfigurationProperties(value = { RawDBProps.class })
public class JpaRawConfigLocal {

	private static final Logger log = LoggerFactory.getLogger(JpaRawConfigLocal.class);

	@Autowired
	private Environment env;

	@Data
	@ConfigurationProperties(prefix = "raw.datasource")
	public static class RawDBProps {
		private String type;
        private String driverClassName;
		private String url;
		private String username;
		private String password;
        private String connectionTestQuery;
        private Integer minimumIdle;
        private Integer maximumPoolSize;
        private String poolName;
	}

	/**
	 *
	 * @return
	 */
	@Bean(name = "dataSourceRaw", destroyMethod = "close")
	public DataSource dataSourceRaw(RawDBProps rawDBProps) {
        log.info("dataraw jdbcURL: {}", rawDBProps.url);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(rawDBProps.driverClassName);
        hikariConfig.setJdbcUrl(rawDBProps.url);
        hikariConfig.setUsername(rawDBProps.username);
        hikariConfig.setPassword(rawDBProps.password);
        hikariConfig.setMaximumPoolSize(rawDBProps.maximumPoolSize);
        hikariConfig.setMinimumIdle(rawDBProps.minimumIdle);
        hikariConfig.setConnectionTestQuery(rawDBProps.connectionTestQuery);
        hikariConfig.setPoolName(rawDBProps.poolName);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
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

}
