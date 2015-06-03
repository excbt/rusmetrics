package ru.excbt.datafuse.nmk.web;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public interface RequestExtraInitializer {
	public void doInit(MockHttpServletRequestBuilder builder);
}
