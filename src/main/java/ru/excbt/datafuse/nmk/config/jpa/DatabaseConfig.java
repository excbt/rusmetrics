/**
 * 
 */
package ru.excbt.datafuse.nmk.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.01.2017
 * 
 */
@Configuration
@Import(value = { JpaConfigLocal.class, JpaRawConfigLocal.class, LdapConfig.class })
public class DatabaseConfig {

}