package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class UDirectoryNodeApiTest extends AnyControllerTest {

	@Test
	public void testGetNodeDir() throws Exception {
		testJsonGet ("/api/uDirectory/19748646");
	}

}
