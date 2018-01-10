package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import java.util.Collections;

import static ru.excbt.datafuse.nmk.service.ConsumptionTask.CONS_TASK_QUEUE;

@Service
public class ConsumptionTaskService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskService.class);

    private final ConsumptionService consumptionService;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public ConsumptionTaskService(ConsumptionService consumptionService,
                                  ConnectionFactory connectionFactory,
                                  MessageConverter messageConverter
                                  //JmsTemplate jmsTemplate
                                    ) {
        this.consumptionService = consumptionService;

        this.jmsTemplate = new JmsTemplate();
        this.jmsTemplate.setConnectionFactory(connectionFactory);
        this.jmsTemplate.setMessageConverter(messageConverter);
        this.jmsTemplate.setExplicitQosEnabled(true);
        this.jmsTemplate.setTimeToLive(3600);
        this.jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        this.jmsTemplate.setPriority(100);
        this.jmsTemplate.setReceiveTimeout(1);
    }

    public void send(ConsumptionTask task) {
        log.info("sending with convertAndSend() to queue <" + task + ">");
        jmsTemplate.convertAndSend(CONS_TASK_QUEUE, task);
    }

    public int queueSize () {
        BrowserCallback<Integer> browserCallback = (s, qb) -> Collections.list(qb.getEnumeration()).size();
        return jmsTemplate.browse(CONS_TASK_QUEUE, browserCallback);
    }


    public ConsumptionTask receive() {
        Object o = jmsTemplate.receiveAndConvert(ConsumptionTask.CONS_TASK_QUEUE);
        if (o == null) {
            return null;
        }
        ConsumptionTask task;
        if (o instanceof ConsumptionTask) {
            task = (ConsumptionTask) o;
        } else {
            log.info("o: {}", o);
            throw new IllegalStateException("ConsumptionTask is invalid");
        }

        return task;
    }


}
