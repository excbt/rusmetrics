package ru.excbt.datafuse.nmk.cli;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import ru.excbt.datafuse.nmk.config.Constants;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by kovtonyk on 11.04.2017.
 */
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class, DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class})
@ComponentScan(basePackages = {"ru.excbt.datafuse.nmk.config.jpa", "ru.excbt.datafuse.nmk.config.ldap"})
public abstract class PortalToolCli {

    private static final Logger log = LoggerFactory.getLogger(PortalToolCli.class);

    @Inject
    private Environment env;


    protected abstract void doWork();

    protected abstract String beginMessage();

    protected abstract String completeMessage();

    @PostConstruct
    public void initApplication() throws IOException {

        log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
            && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run "
                + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
            && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not"
                + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }

        log.info(StringUtils.repeat("=", 60));
        log.info(StringUtils.repeat("=", 6) + " " + beginMessage() + " " +
            StringUtils.repeat("=", 60 - 8 - beginMessage().length()));

        doWork();

        log.info(StringUtils.repeat("=", 6) + " " + completeMessage() + " " +
            StringUtils.repeat("=", 60 - 8 - completeMessage().length()));
        log.info(StringUtils.repeat("=", 60));

    }


}
