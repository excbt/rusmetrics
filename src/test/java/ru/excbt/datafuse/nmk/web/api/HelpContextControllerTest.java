package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;

public class HelpContextControllerTest extends AnyControllerTest {

	@Test
	public void testRedirect() throws Exception {
		RequestExtraInitializer param = builder -> {
		};
		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().is3xxRedirection());
		};
		_testGet("/api/help/jmp/home01", param, tester);
	}

	@Test
	public void testSetup() throws Exception {
		_testGetSuccessful("/api/help/setup/home01");
	}

	@Test
	public void testHelpInfo() throws Exception {
		_testGetJson("/api/help/info/home01");
	}
}
