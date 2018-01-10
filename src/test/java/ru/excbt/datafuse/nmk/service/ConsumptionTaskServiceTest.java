package ru.excbt.datafuse.nmk.service;

import org.assertj.core.api.Assertions;
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
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.jms.ConnectionFactory;
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

        consumptionTaskService = new ConsumptionTaskService(consumptionService, connectionFactory, messageConverter);

        jmsTemplateLocal = jmsTemplate;
    }


    @Test
    public void testSend() throws InterruptedException {

        int size = consumptionTaskService.queueSize();
        log.info("Size before: {}", size);
        consumptionTaskService.send(new ConsumptionTask().name("MyName"));
        size = consumptionTaskService.queueSize();
        TimeUnit.SECONDS.sleep(2);
        log.info("Size after:{}", size);
        assertThat(size).isGreaterThan(0);



        //ConsumptionTask task = (ConsumptionTask) jmsTemplateLocal.receiveAndConvert(ConsumptionTask.CONS_TASK_QUEUE);
        ConsumptionTask task = consumptionTaskService.receive();
        assertThat(task).isNotNull();
        log.info(task.toString());
        //consumptionTaskConsumer.

        consumptionTaskService.send(new ConsumptionTask().name("MyName-2"));

        try {
            task = (ConsumptionTask) jmsTemplateLocal.receiveAndConvert(ConsumptionTask.CONS_TASK_QUEUE);
        } catch (JmsException e) {
            log.error("e: {}", e);
        }
        if (task == null) {
            log.info("No Messages");
        } else {
            log.info("Message:{} ", task.toString());
        }

    }
}
