package ru.excbt.datafuse.nmk.web.rest;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionService;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ContZPointConsumptionResourceTest extends PortalApiTest {

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
    public void testGetConsumptionDataPaged() throws Exception {

        LocalDatePeriod localDatePeriod = LocalDatePeriod.month(2017,2);

        Long contZPointId = 128551684L;

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-consumption/{contZPointId}/paged", contZPointId)
                .param("fromDateStr", localDatePeriod.dateFromStr())
                .param("toDateStr", localDatePeriod.dateToStr())
                .param("size", String.valueOf(50)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    @Transactional
    public void testGetConsumptionData() throws Exception {

        LocalDatePeriod localDatePeriod = LocalDatePeriod.month(2017,2);

        Long contZPointId = 128551684L;

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-consumption/{contZPointId}", contZPointId)
                .param("fromDateStr", localDatePeriod.dateFromStr())
                .param("toDateStr", localDatePeriod.dateToStr())
                .param("size", String.valueOf(50)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    @Transactional
    public void testAvailableYearData() throws Exception {
        //129832662L

        LocalDatePeriod localDatePeriod = LocalDatePeriod.month(2017,2);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-consumption/year/{yyyy}", 2017)
                .param("fromDateStr", localDatePeriod.dateFromStr())
                .param("toDateStr", localDatePeriod.dateToStr()))
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
