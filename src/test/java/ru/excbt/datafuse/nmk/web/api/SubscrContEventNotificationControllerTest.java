package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrContEventNotificationControllerTest extends AnyControllerTest {

	@Test
	public void testNotifiicationsAll() throws Exception {
		testJsonGet("/api/subscr/contEvent/notifications/all");
	}
}
