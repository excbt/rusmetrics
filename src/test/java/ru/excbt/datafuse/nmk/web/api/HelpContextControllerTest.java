package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.service.HelpContextService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;

import javax.transaction.Transactional;

public class HelpContextControllerTest extends AnyControllerTest {

    @Autowired
    private HelpContextService helpContextService;

	@Test
    @Transactional
	public void testRedirect() throws Exception {
		RequestExtraInitializer param = builder -> {
		};
		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().is3xxRedirection());
		};
		_testGet("/api/help/jmp/home01", param, tester);
	}

	@Test
    @Transactional
	public void testSetup() throws Exception {
        helpContextService.createByAnchorId("home01");
		_testGetSuccessful("/api/help/setup/home01");
	}

	@Test
    @Transactional
	public void testHelpInfo() throws Exception {
        helpContextService.createByAnchorId("home01");
		_testGetJson("/api/help/info/home01");
	}
}
