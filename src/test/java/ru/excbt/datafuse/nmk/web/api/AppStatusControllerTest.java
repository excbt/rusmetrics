package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;


public class AppStatusControllerTest extends AnyControllerTest {

	@Test
    @Transactional
	public void testAppVersion() throws Exception {
		_testGetJson("/api/appStatus/version");
	}

	@Test
    @Transactional
	public void testAppStatus() throws Exception {
		_testGetJson("/api/appStatus/status");
	}

}
