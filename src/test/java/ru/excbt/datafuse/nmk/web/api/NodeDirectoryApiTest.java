package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class NodeDirectoryApiTest extends AnyControllerTest {

	@Test
	public void testGetNodeDir() throws Exception {
		testJsonGet ("/api/nodeDirectory/19748646");
	}

}
