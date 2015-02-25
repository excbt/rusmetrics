package ru.excbt.datafuse.nmk.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class UserApiTest extends AnyControllerTest {

	@Test
	public void testUserContObjects() throws Exception {
		testJsonGet ("/api/user/contObjects");
	}

}
