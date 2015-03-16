package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class UDirectoryNodeTest extends AnyControllerTest {

	@Test
	public void testGetNodeDir() throws Exception {
		testJsonGet(String.format(UDirectoryTestConst.DIRECTORY_URL_API
				+ "/%d/node", UDirectoryTestConst.TEST_DIRECTORY_ID));
	}

}
