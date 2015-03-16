package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class UDirectoryNodeApiTest extends AnyControllerTest {

	@Test
	@Ignore
	public void testGetNodeDir() throws Exception {
		testJsonGet ("/api/u_directory/19748646");
	}

}
