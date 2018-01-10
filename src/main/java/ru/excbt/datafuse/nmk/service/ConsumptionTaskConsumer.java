package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Session;

import static ru.excbt.datafuse.nmk.service.ConsumptionTask.*;

//@Component
public class ConsumptionTaskConsumer {
    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskConsumer.class);

    private final ConsumptionService consumptionService;

    @Autowired
    public ConsumptionTaskConsumer(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

//    @JmsListener(destination = CONS_TASK_QUEUE + "_1")
    public void receiveMessage(@Payload ConsumptionTask consumptionTask,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {
        log.info("received <" + consumptionTask + ">");

        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("######          Message Details           #####");
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("headers: " + headers);
        log.info("message: " + message);
        log.info("session: " + session);
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
    }

}
