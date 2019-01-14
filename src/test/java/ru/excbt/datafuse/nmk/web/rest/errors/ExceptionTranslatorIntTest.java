package ru.excbt.datafuse.nmk.web.rest.errors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ExceptionTranslator controller advice.
 *
 * @see ExceptionTranslator
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ExceptionTranslatorIntTest {

    private static final Logger log = LoggerFactory.getLogger(ExceptionTranslatorIntTest.class);

    @Autowired
    private ExceptionTranslatorTestController controller;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(exceptionTranslator)
            .build();
    }

    @Test
    public void testConcurrencyFailure() throws Exception {
        mockMvc.perform(get("/test/concurrency-failure"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_CONCURRENCY_FAILURE));
    }

    @Test
    public void testMethodArgumentNotValid() throws Exception {
         mockMvc.perform(post("/test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON))
             .andDo(MockMvcResultHandlers.print())
             .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_VALIDATION))
             .andExpect(jsonPath("$.fieldErrors.[0].objectName").value("testDTO"))
             .andExpect(jsonPath("$.fieldErrors.[0].field").value("test"))
             .andExpect(jsonPath("$.fieldErrors.[0].message").value("NotNull"));
    }

    @Test
    public void testParameterizedError() throws Exception {
        mockMvc.perform(get("/test/parameterized-error"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("test parameterized error"))
            .andExpect(jsonPath("$.params.param0").value("param0_value"))
            .andExpect(jsonPath("$.params.param1").value("param1_value"));
    }

    @Test
    public void testParameterizedError2() throws Exception {
        mockMvc.perform(get("/test/parameterized-error2"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("test parameterized error"))
            .andExpect(jsonPath("$.params.foo").value("foo_value"))
            .andExpect(jsonPath("$.params.bar").value("bar_value"));
    }

    @Test
    public void testAccessDenied() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_ACCESS_DENIED))
            .andExpect(jsonPath("$.description").value("test access denied!"));
    }

    @Test
    public void testMethodNotSupported() throws Exception {
        mockMvc.perform(post("/test/access-denied"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_METHOD_NOT_SUPPORTED))
            .andExpect(jsonPath("$.description").value("Request method 'POST' not supported"));
    }

    @Test
    public void testExceptionWithResponseStatus() throws Exception {
        mockMvc.perform(get("/test/response-status"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.400"))
            .andExpect(jsonPath("$.description").value("test response status"));
    }

    @Test
    public void testInternalServerError() throws Exception {
        mockMvc.perform(get("/test/internal-server-error"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((r) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(r)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_INTERNAL_SERVER_ERROR))
            .andExpect(jsonPath("$.description").value("Internal server error"));
    }
}
