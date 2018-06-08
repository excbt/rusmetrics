package ru.excbt.datafuse.nmk.service.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import ru.excbt.datafuse.nmk.config.AsyncConfiguration;
import ru.excbt.datafuse.nmk.config.CacheConfiguration;
import ru.excbt.datafuse.nmk.config.MetricsConfiguration;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.config.mvc.SpringMvcConfig;

@EnableAutoConfiguration(exclude = {
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class,
    WebMvcAutoConfiguration.class, SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {"ru.excbt.datafuse.nmk.config"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            value = {
                AsyncConfiguration.class,
                MetricsConfiguration.class,
                CacheConfiguration.class
            })
    })
@EnableConfigurationProperties({PortalProperties.class})
public class PortalDataTestConfiguration {

    @MockBean
    private CacheManager cacheManager;

}
