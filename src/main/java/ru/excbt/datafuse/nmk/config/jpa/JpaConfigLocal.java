package ru.excbt.datafuse.nmk.config.jpa;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.config.SLogProperties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ru.excbt.datafuse.nmk.data.repository", "ru.excbt.datafuse.nmk.repository",
    "ru.excbt.datafuse.raw.data.repository"})
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data", "ru.excbt.datafuse.nmk.slog","ru.excbt.datafuse.nmk.service",
    "ru.excbt.datafuse.nmk.domain", "ru.excbt.datafuse.raw.data"})
@EnableConfigurationProperties(value = {PortalProperties.class, SLogProperties.class})
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfigLocal {

    private static final Logger log = LoggerFactory.getLogger(JpaConfigLocal.class);

	@Autowired
	private Environment env;

	/**
	 *
	 * @return
	 */
	@Primary
	@Bean(name = "dataSource")
	//@ConfigurationProperties("portal.datasource")
    @Autowired
	public DataSource dataSource(PortalProperties portalProperties) {
        log.info("nmk-p url: {}", portalProperties.getDatasource().getUrl());
        return //DataSourceBuilder.create().build();
            DataSourceBuilder.create().driverClassName(portalProperties.getDatasource().getDriverClassName())
                .url(portalProperties.getDatasource().getUrl())
                .username(portalProperties.getDatasource().getUsername())
                .password(portalProperties.getDatasource().getPassword()).build();
            //.url(portalDBProps.url).username(portalDBProps.username).password(portalDBProps.password).build();
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {
        //package with spring data jpa converters "org.springframework.data.jpa.convert.threeten"
		return builder.dataSource(dataSource).packages("ru.excbt.datafuse.nmk.data.model",
            "ru.excbt.datafuse.nmk.domain", "ru.excbt.datafuse.raw.data.model").persistenceUnit("nmk-p")
				.build();
	}



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
	@Bean
    @Autowired
	public JasperDatabaseConnectionSettings jasperDatabaseConnectionSettings(PortalProperties portalProperties) {
		return new JasperDatabaseConnectionSettings() {

			private final String url = portalProperties.getDatasource().getUrl();
			private final String username = portalProperties.getDatasource().getUsername();
			private final String password = portalProperties.getDatasource().getPassword();

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

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }


}
