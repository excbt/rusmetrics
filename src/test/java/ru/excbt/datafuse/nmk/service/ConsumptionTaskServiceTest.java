package ru.excbt.datafuse.nmk.service;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.jms.ConnectionFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ConsumptionTaskServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskServiceTest.class);

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Mock
    private ConsumptionService consumptionService;

    private ConsumptionTaskService consumptionTaskService;


    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private JmsTemplate jmsTemplate;

    private JmsTemplate jmsTemplateLocal;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        consumptionTaskService = new ConsumptionTaskService(connectionFactory, messageConverter);

        jmsTemplateLocal = jmsTemplate;
        jmsTemplateLocal.setReceiveTimeout(1);
    }


    @Test
    @Transactional
    public void testSend() throws InterruptedException {

        int size;
        //size = consumptionTaskService.queueSize();
        //log.info("Size before: {}", size);

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        consumptionTaskService.sendTask(
            ConsumptionTask.builder()
                .name("MyName")
                .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
                .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant())
                .contServiceType(ContServiceTypeKey.HW.getKeyname())
                .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
                .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
                .retryCnt(3).build());

        size = consumptionTaskService.getTaskQueueSize();
        //TimeUnit.SECONDS.sleep(2);
        log.info("Size after:{}", size);
        assertThat(size).isGreaterThan(0);


        consumptionTaskService.viewTaskQueue().forEach(i -> {
            log.info("B Task: {}", i.toString());
        });


        ConsumptionTask task = consumptionTaskService.receiveTask();
        assertThat(task).isNotNull();
        log.info(task.toString());
        consumptionTaskService.sendTask(task.makeRetry());

        TimeUnit.SECONDS.sleep(1);
        consumptionTaskService.processTaskQueue(i -> consumptionService.processHWater(i));

//        try {
//            task = (ConsumptionTask) jmsTemplateLocal.receiveAndConvert(ConsumptionTask.CONS_TASK_QUEUE);
//        } catch (JmsException e) {
//            log.error("e: {}", e);
//        }
//        if (task == null) {
//            log.info("No Messages");
//        } else {
//            log.info("Message:{} ", task.toString());
//        }
        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
        TimeUnit.SECONDS.sleep(1);
    }
}
