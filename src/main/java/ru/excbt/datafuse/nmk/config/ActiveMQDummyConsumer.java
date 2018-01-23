package ru.excbt.datafuse.nmk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Session;

/**
 * Needed for ActiveMQ to be running always
 */
@Component
public class ActiveMQDummyConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActiveMQDummyConsumer.class);

    @JmsListener(destination = "DUMMY")
    public void receiveMessage(@Payload Object object,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {
        log.debug("received <" + object + ">");
        log.debug("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.debug("######          Message Details           #####");
        log.debug("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.debug("headers: " + headers);
        log.debug("message: " + message);
        log.debug("session: " + session);
        log.debug("- - - - - - - - - - - - - - - - - - - - - - - -");
    }

}
