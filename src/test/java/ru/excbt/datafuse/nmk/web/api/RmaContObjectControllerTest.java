package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaContObjectControllerTest extends AnyControllerTest {

	@Test
	public void testContObjectGet() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects"));
	}

}
