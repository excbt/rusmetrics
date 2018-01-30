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
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ConsumptionService;
import ru.excbt.datafuse.nmk.service.ConsumptionTaskService;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTaskTemplate;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class ConsumptionTaskResourceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;


    @Autowired
    private ConsumptionTaskService consumptionTaskService;

    private ConsumptionTaskResource consumptionTaskResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        consumptionTaskResource = new ConsumptionTaskResource(consumptionTaskService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(consumptionTaskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    public void getList() throws Exception {

        LocalDate day = LocalDate.of(2017, 5, 26);

        consumptionTaskService.sendTask(
            ConsumptionTask.builder()
                .name("MyName")
                .dateFrom(day)
                .dateTo(day)
                .dataType(ConsumptionService.DATA_TYPE_ELECTRICITY)
                .template(ConsumptionTaskTemplate.Template24H_from_1H)
                .retryCnt(3).build().generateTaskUUID());


        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/rma/consumption-task/list"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));


    }


    @Test
    public void getNew() throws Exception {

        LocalDatePeriod period = LocalDatePeriod.currentMonth();

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/rma/consumption-task/new")
        .param("date", period.dateFromStr()))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());

        consumptionTaskService.viewTaskQueue().forEach(i -> {
            log.info("Task UUID: {}", i.getTaskUUID());
        });

    }


    @Test
    public void getMonth() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/rma/consumption-task/new/month")
            .param("year", String.valueOf(2017))
            .param("mon", String.valueOf(1)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());


    }
}
