package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaObjectTreeTemplateControllerTest extends RmaControllerTest {

	@Test
	public void testRmaSubscrObjectTreeTemplates() throws Exception {
		_testGetJson("/api/rma/subscrObjectTreeTemplates");
	}
}
