package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class AppStatusControllerTest extends AnyControllerTest {

	@Test
	public void testAppVersion() throws Exception {
		_testGetJson("/api/appStatus/version");
	}

	@Test
	public void testAppStatus() throws Exception {
		_testGetJson("/api/appStatus/status");
	}

}
