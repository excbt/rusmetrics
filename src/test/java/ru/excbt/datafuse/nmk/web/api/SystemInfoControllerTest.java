package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SystemInfoControllerTest extends AnyControllerTest {

	@Test
	public void test() throws Exception {
		testJsonGet("/api/systemInfo/fullUserInfo");
	}

}
