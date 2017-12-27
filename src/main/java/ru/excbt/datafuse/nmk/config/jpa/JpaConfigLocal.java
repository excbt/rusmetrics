package ru.excbt.datafuse.nmk.config.jpa;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.modelmapper.ModelMapper;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.Data;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal.PortalDBProps;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal.SLogDBProps;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ru.excbt.datafuse.nmk.data.repository", "ru.excbt.datafuse.nmk.repository"})
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.data", "ru.excbt.datafuse.nmk.slog","ru.excbt.datafuse.nmk.service",
    "ru.excbt.datafuse.nmk.domain"})
@EnableConfigurationProperties(value = { PortalDBProps.class, SLogDBProps.class })
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfigLocal {

    private static final Logger log = LoggerFactory.getLogger(JpaConfigLocal.class);

	@Autowired
	private Environment env;

	@Data
	@ConfigurationProperties(prefix = "portal.datasource")
	public static class PortalDBProps {
		private String type;
        private String driverClassName;
		private String url;
		private String username;
		private String password;
	}

	@Data
    @ConfigurationProperties(prefix = "slog.datasource")
	public static class SLogDBProps {
        private String type;
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private String schema;
    }

	/**
	 *
	 * @return
	 */
	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties("portal.datasource")
	public DataSource dataSource(PortalDBProps portalDBProps) {
        log.info("nmk-p url: {}", portalDBProps.url);
        return //DataSourceBuilder.create().build();
            DataSourceBuilder.create().driverClassName(portalDBProps.driverClassName)
                .url(portalDBProps.url).username(portalDBProps.username).password(portalDBProps.password).build();
            //.url(portalDBProps.url).username(portalDBProps.username).password(portalDBProps.password).build();
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource") DataSource dataSource) {
        //package with spring data jpa converters "org.springframework.data.jpa.convert.threeten"
		return builder.dataSource(dataSource).packages("ru.excbt.datafuse.nmk.data.model", "ru.excbt.datafuse.nmk.domain").persistenceUnit("nmk-p")
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
	public JasperDatabaseConnectionSettings jasperDatabaseConnectionSettings(PortalDBProps portalDBProps) {
		return new JasperDatabaseConnectionSettings() {

			private final String url = portalDBProps.url;
			private final String username = portalDBProps.username;
			private final String password = portalDBProps.password;

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
