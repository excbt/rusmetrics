package ru.excbt.datafuse.nmk.config.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import ru.excbt.datafuse.nmk.config.PropertyConfig;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfig;
import ru.excbt.datafuse.nmk.config.security.WebSecurityConfig;
import ru.excbt.datafuse.nmk.web.interceptor.LoginInterceptor;

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk" })
@Import({ PropertyConfig.class, JpaConfig.class, WebSecurityConfig.class })
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private LoginInterceptor loginInterceptor;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations(
				"/resources/");
		registry.addResourceHandler("/api/**").addResourceLocations(
				"/resources/");
		registry.addResourceHandler("/bower_components/**")
				.addResourceLocations("/app/bower_components/");
		registry.addResourceHandler("/vendor_components/**")
				.addResourceLocations("/app/vendor_components/");
		registry.addResourceHandler("/app/bower_components/**")
				.addResourceLocations("/app/bower_components/");
		registry.addResourceHandler("/app/vendor_components/**")
				.addResourceLocations("/app/vendor_components/");
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getMultipartResolver() {
		CommonsMultipartResolver result = new CommonsMultipartResolver();
		result.setMaxUploadSize(20971520);
		result.setMaxUploadSize(1048576);
		return result;
	}

	@Bean(name = "internalResourceViewResolver")
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setPrefix("/WEB-INF/views/");
		bean.setCache(false);
		// bean.setOrder(0);
		return bean;
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).favorParameter(true)
				.parameterName("mediaType").ignoreAcceptHeader(true)
				.useJaf(false).mediaType("xml", MediaType.APPLICATION_XML)
				.mediaType("json", MediaType.APPLICATION_JSON)
				.mediaType("html", MediaType.TEXT_HTML)
				.mediaType("less", MediaType.TEXT_HTML);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor);

		WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
		webContentInterceptor.setCacheSeconds(0);
		webContentInterceptor.setUseExpiresHeader(true);
		webContentInterceptor.setUseCacheControlHeader(true);
		webContentInterceptor.setUseCacheControlNoStore(true);

		registry.addInterceptor(webContentInterceptor);
	}

}
