package ru.excbt.datafuse.nmk.web.rest;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationTypeService;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class OrganizationTypeResourceIntTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(OrganizationTypeResourceIntTest.class);

    private static ResultHandler logJsonHandler = (i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i));

    public static void log(MvcResult i) {
        try {
            log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
    }

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private OrganizationTypeService organizationTypeService;

    private OrganizationTypeResource organizationTypeResource;



    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        organizationTypeResource = new OrganizationTypeResource(organizationTypeService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(organizationTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    public void getOrganizationTypes() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/organization-types"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(OrganizationTypeResourceIntTest::log)
            .andExpect(status().isOk());

    }
}
