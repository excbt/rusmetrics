package ru.excbt.datafuse.nmk.config.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.ldap" })
@PropertySource(value = "classpath:META-INF/ldap.properties")
public class LdapConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(LdapConfig.class);
	
	@Value("${ldap.url}")
	private String ldapUrl;

	@Value("${ldap.base}")
	private String ldapBase;

	@Value("${ldap.user}")
	private String ldapUser;

	@Value("${ldap.password}")
	private String ldapPassword;

	/**
	 * 
	 * @return
	 */
	public String getLdapBase() {
		return ldapBase;
	}
	
	@Bean
    public LdapContextSource contextSource () {
		logger.info("Connecting to LDAP");
		logger.info("LDAP url: {}", ldapUrl);
		logger.info("LDAP ldapBase: {}", ldapBase);
		logger.info("LDAP ldapUser: {}", ldapUser);
        LdapContextSource contextSource= new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUser);
        contextSource.setPassword(ldapPassword);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());        
    }	
	
}
