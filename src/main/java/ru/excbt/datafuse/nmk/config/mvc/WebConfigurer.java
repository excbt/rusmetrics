/**
 *
 */
package ru.excbt.datafuse.nmk.config.mvc;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.config.PortalProperties;

import javax.servlet.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 *
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

	private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private MetricRegistry metricRegistry;

    private final PortalProperties portalProperties;

    public WebConfigurer(Environment env, PortalProperties portalProperties) {

        this.env = env;
        this.portalProperties = portalProperties;
    }

	private void setLocationForStaticAssets(WebServerFactory server) {

        if (server instanceof ConfigurableServletWebServerFactory) {
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            File root;
            String prefixPath = resolvePathPrefix();
            if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
                root = new File(prefixPath + "target/www/");
            } else {
                root = new File(prefixPath + "src/main/webapp/");
            }
            if (root.exists() && root.isDirectory()) {
                servletWebServer.setDocumentRoot(root);
            }
        }

	}

	/**
	 * Resolve path prefix to static resources.
	 */
	private String resolvePathPrefix() {
		String fullExecutablePath = this.getClass().getResource("").getPath();
		String rootPath = Paths.get(".").toUri().normalize().getPath();
		String extractedPath = fullExecutablePath.replace(rootPath, "");
		int extractionEndIndex = extractedPath.indexOf("target/");
		if (extractionEndIndex <= 0) {
			return "";
		}
		return extractedPath.substring(0, extractionEndIndex);
	}

	/**
	 *
	 * @param servletContext
	 * @throws ServletException
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if (env.getActiveProfiles().length != 0) {
			log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
		}
		EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD,
				DispatcherType.ASYNC);
		initMetrics(servletContext, disps);
		//		if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
		//			initCachingHttpHeadersFilter(servletContext, disps);
		//		}
		log.info("Web application fully configured");

	}


    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
            metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
            metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
            new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        log.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet =
            servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/management/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }

    @Autowired(required = false)
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = portalProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/v2/api-docs", config);
            //source.registerCorsConfiguration("/app/**", config);
        }
        return new CorsFilter(source);
    }

    @Override
    public void customize(WebServerFactory server) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
        mappings.add("html", "text/html;charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", "text/html;charset=utf-8");
        ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
        servletWebServer.setMimeMappings(mappings);

        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(server);

    }


    // TODO CachingHttpHeadersFilter

}
