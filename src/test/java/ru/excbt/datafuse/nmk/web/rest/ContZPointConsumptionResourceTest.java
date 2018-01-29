package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ContZPointConsumptionResourceTest {

    private static final Logger log = LoggerFactory.getLogger(ContZPointConsumptionResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private ContZPointConsumptionService contZPointConsumptionService;

    private ContZPointConsumptionResource contZPointConsumptionResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        contZPointConsumptionResource = new ContZPointConsumptionResource(contZPointConsumptionService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(contZPointConsumptionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    @Transactional
    public void testGetConsumption() throws Exception {
        //129832662L

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-consumption/{contZPointId}", 128551684))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    @Transactional
    public void testAvailableYearData() throws Exception {
        //129832662L

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-consumption/year/{yyyy}", 2017))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    @Transactional
    public void testAvailableMonthData() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            get("/api/subscr/cont-zpoint-consumption/year/{yyyy}/mon/{mon}", 2017, 2))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }
}
