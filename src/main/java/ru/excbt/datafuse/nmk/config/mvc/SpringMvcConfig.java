package ru.excbt.datafuse.nmk.config.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(basePackages = { "ru.excbt.datafuse.nmk.web" })
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/api/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/bower_components/**").addResourceLocations("/app/bower_components/");
        registry.addResourceHandler("/vendor_components/**").addResourceLocations("/app/vendor_components/");
        registry.addResourceHandler("/app/bower_components/**").addResourceLocations("/app/bower_components/");
        registry.addResourceHandler("/app/vendor_components/**").addResourceLocations("/app/vendor_components/");
    }	
	
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
	
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getMultipartResolver() {
    	CommonsMultipartResolver result = new CommonsMultipartResolver();
    	result.setMaxUploadSize(20971520);
    	result.setMaxUploadSize(1048576);
        return result;
    }    
    
}
