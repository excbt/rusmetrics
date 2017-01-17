package ru.excbt.datafuse.nmk.config.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
import ru.excbt.datafuse.nmk.web.interceptor.LoginInterceptor;

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk" },
		excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX, pattern = "ru.excbt.datafuse.nmk.config.*") })
@Import({ PropertyConfig.class })
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Autowired
	private LoginInterceptor loginInterceptor;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//registry.addResourceHandler("/app").addResourceLocations("/app/index.html");
		registry.addResourceHandler("/app/**").addResourceLocations("/app/");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/api/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/bower_components/**").addResourceLocations("/app/bower_components/");
		registry.addResourceHandler("/vendor_components/**").addResourceLocations("/app/vendor_components/");
		registry.addResourceHandler("/app/bower_components/**").addResourceLocations("/app/bower_components/");
		registry.addResourceHandler("/app/vendor_components/**").addResourceLocations("/app/vendor_components/");

		registry.addResourceHandler("/jasper/preview/**").addResourceLocations("/jasper/preview/");

		registry.addResourceHandler("/app/jasper/preview/**").addResourceLocations("/jasper/preview/");

		registry.addResourceHandler("/app/static/**").addResourceLocations("/static.wro/");

	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getMultipartResolver() {
		CommonsMultipartResolver result = new CommonsMultipartResolver();
		result.setMaxUploadSize(20971520);
		//result.setMaxUploadSize(1048576);
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
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(true)
				.useJaf(false).mediaType("xml", MediaType.APPLICATION_XML).mediaType("json", MediaType.APPLICATION_JSON)
				.mediaType("html", MediaType.TEXT_HTML).mediaType("less", MediaType.TEXT_HTML);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor);

		WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
		webContentInterceptor.setCacheSeconds(0);
		//		webContentInterceptor.setUseExpiresHeader(true);
		//		webContentInterceptor.setUseCacheControlHeader(true);
		//		webContentInterceptor.setUseCacheControlNoStore(true);

		registry.addInterceptor(webContentInterceptor);
	}

	/**
	 * 
	 * @param registry
	 */
	//	@Override
	//	public void addViewControllers(ViewControllerRegistry registry) {
	//		registry.addViewController("/app").setViewName("forward:/app/index.html");
	//	}

	// @Override
	// public void configureMessageConverters(List<HttpMessageConverter<?>>
	// converters) {
	// //ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new
	// ByteArrayHttpMessageConverter();
	// // MediaType zip = new MediaType("application", "zip");
	// // MediaType pdf = new MediaType("application", "pdf");
	// // MediaType xlsx = new MediaType("application",
	// "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	// // MediaType xls = new MediaType("application", "vnd.ms-excel");
	// // byteArrayHttpMessageConverter.getSupportedMediaTypes().add(zip);
	// // byteArrayHttpMessageConverter.getSupportedMediaTypes().add(pdf);
	// // byteArrayHttpMessageConverter.getSupportedMediaTypes().add(xlsx);
	// // byteArrayHttpMessageConverter.getSupportedMediaTypes().add(xls);
	// //converters.add(byteArrayHttpMessageConverter);
	// }

}
