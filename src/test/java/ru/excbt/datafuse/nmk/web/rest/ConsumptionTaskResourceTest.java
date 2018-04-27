package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Ignore;
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
import ru.excbt.datafuse.nmk.service.ConsumptionService;
import ru.excbt.datafuse.nmk.service.ConsumptionTaskSchedule;
import ru.excbt.datafuse.nmk.service.ConsumptionTaskService;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTaskTemplate;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.jms.JMSException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ConsumptionTaskResourceTest extends PortalApiTest {

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

    @Autowired
    private ConsumptionTaskSchedule consumptionTaskSchedule;

    private ConsumptionTaskResource consumptionTaskResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        consumptionTaskResource = new ConsumptionTaskResource(consumptionTaskService, consumptionTaskSchedule);

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

        log.info("CHECK \n");



        List<String> messageIds = consumptionTaskService.viewTextMessageQueue().stream().map(i -> {
            try {
                log.info("MessageText: ID:{}", i.getJMSMessageID());
                log.debug("Text:{}", i.getText());
            } catch (JMSException e) {
                e.printStackTrace();
                return null;
            }
            return i.getJMSMessageID();
        }).collect(Collectors.toList());


        messageIds.forEach(i -> {
            consumptionTaskService.viewTaskQueue(i).forEach(t -> {
                log.info("HHH:{}", t.getTaskUUID());
            });
        });

    }


    @Test
    @Transactional
    public void putNewTask() throws Exception {

        LocalDatePeriod period = LocalDatePeriod.currentMonth();

        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/rma/consumption-task/new")
        .param("date", period.dateFromStr()))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());

        consumptionTaskService.viewTaskQueue().forEach(i -> {
            log.info("Task UUID: {}", i.getTaskUUID());
        });

    }

    @Ignore
    @Test
    @Transactional
    public void putMonth() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/rma/consumption-task/new/month")
            .param("year", String.valueOf(2017))
            .param("mon", String.valueOf(1)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());

    }

    @Ignore
    @Test
    @Transactional
    public void putYear() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/rma/consumption-task/new/year")
            .param("year", String.valueOf(2017)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());
    }

    @Ignore
    @Test
    @Transactional
    public void getToday() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/rma/consumption-task/new/today")
            .param("year", String.valueOf(2017)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());
    }

    @Ignore
    @Test
    @Transactional
    public void putToday() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/rma/consumption-task/new/today")
            .param("year", String.valueOf(2017)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk());
    }

}
