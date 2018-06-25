package ru.excbt.datafuse.nmk.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.excbt.datafuse.nmk.config.jpa.JasperDatabaseConnectionSettings;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ru.excbt.datafuse.nmk.data.repository", "ru.excbt.datafuse.nmk.repository",
    "ru.excbt.datafuse.raw.data.repository"})
@EnableConfigurationProperties(value = {PortalProperties.class, SLogProperties.class})
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EntityScan(basePackages = {"ru.excbt.datafuse.nmk.data.model", "ru.excbt.datafuse.nmk.domain", "ru.excbt.datafuse.raw.data.model"})
public class DatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

	/**
	 *
	 * @return
	 */
	@Bean
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


}
