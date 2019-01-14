package ru.excbt.datafuse.nmk.web.rest.errors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ApiResultIntTest {

    private static final Logger log = LoggerFactory.getLogger(ApiResultIntTest.class);

    private MockMvc mockMvc;

    @Autowired
    private ApiResultTestController apiResultTestController;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiResultTestController)
            .setControllerAdvice(exceptionTranslator)
            .build();
    }

    @Test
    public void testApiParam() throws Exception {
        mockMvc.perform(get("/test/api-param-exception"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)));
    }

    @Test
    public void testApiParamToVm() throws Exception {
        mockMvc.perform(get("/test/api-param-to-vm"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)));
    }



}
