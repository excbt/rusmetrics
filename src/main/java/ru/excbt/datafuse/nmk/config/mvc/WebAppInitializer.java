package ru.excbt.datafuse.nmk.config.mvc;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfig;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.config.security.WebSecurityConfig;

public class WebAppInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { SpringMvcConfig.class, JpaConfig.class, LdapConfig.class,
				WebSecurityConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		Filter[] filters;

		CharacterEncodingFilter encFilter;
		encFilter = new CharacterEncodingFilter();
		encFilter.setEncoding("UTF-8");
		encFilter.setForceEncoding(true);

		filters = new Filter[] { encFilter };
		return filters;
	}

}
