/**
 * 
 */
package ru.excbt.datafuse.nmk.config.jpa;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 18.01.2017
 * 
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class })
@Import(value = { JpaConfigLocal.class, LdapConfig.class, JpaRawConfigLocal.class })
//@Import(value = { JpaConfigLocal.class, LdapConfig.class, JpaRawConfigLocal.class })
public class JpaLayerTestApp {

}
