package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import javax.transaction.Transactional;

public class ContEventControllerTest extends AnyControllerTest {

	@Test
    @Transactional
	public void testEventTypes() throws Exception {
		_testGetJson("/api/contEvent/types");
	}
}
