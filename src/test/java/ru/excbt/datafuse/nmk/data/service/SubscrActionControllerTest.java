package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrActionControllerTest extends AnyControllerTest {

	@Test
	public void testGetGroup() throws Exception {
		testJsonGet("/api/subscr/subscrAction/groups");
	}
}
