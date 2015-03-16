package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class UDirectoryNodeApiTest extends AnyControllerTest {

	@Test
	public void testGetNodeDir() throws Exception {
		testJsonGet(String.format(UDirectoryApiTestConst.DIRECTORY_URL_API
				+ "/%d/node", UDirectoryApiTestConst.TEST_DIRECTORY_ID));
	}

}
