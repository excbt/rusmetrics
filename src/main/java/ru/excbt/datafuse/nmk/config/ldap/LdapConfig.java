package ru.excbt.datafuse.nmk.config.ldap;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig.LdapProps;

@Configuration
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.ldap" })
@EnableConfigurationProperties(value = {LdapProps.class})
    public class LdapConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(LdapConfig.class);

    @Data
    @ConfigurationProperties(prefix = "ldap")
    public static class LdapProps {
        private String url;
        private String base;
        private String user;
        private String password;
    }

	@Bean
    public LdapContextSource contextSource (LdapProps ldapProps) {
		logger.info("Connecting to LDAP");
		logger.info("LDAP url: {}", ldapProps.url);
		logger.info("LDAP ldapBase: {}", ldapProps.base);
		logger.info("LDAP ldapUser: {}", ldapProps.user);
        LdapContextSource contextSource= new LdapContextSource();
        contextSource.setUrl(ldapProps.url);
        contextSource.setBase(ldapProps.base);
        contextSource.setUserDn(ldapProps.user);
        contextSource.setPassword(ldapProps.password);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

}
