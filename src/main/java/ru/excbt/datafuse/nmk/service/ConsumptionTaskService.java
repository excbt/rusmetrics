package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask.CONS_TASK_QUEUE;

@Service
public class ConsumptionTaskService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskService.class);

    private final JmsTemplate jmsTemplate;

    private final MessageConverter messageConverter;

    @Autowired
    public ConsumptionTaskService(ConnectionFactory connectionFactory,
                                  MessageConverter messageConverter
                                  //JmsTemplate jmsTemplate
    ) {
        this.messageConverter = messageConverter;

        this.jmsTemplate = new JmsTemplate();
        this.jmsTemplate.setConnectionFactory(connectionFactory);
        this.jmsTemplate.setMessageConverter(messageConverter);
        this.jmsTemplate.setExplicitQosEnabled(true);
        this.jmsTemplate.setTimeToLive(3600);
        this.jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        this.jmsTemplate.setPriority(100);
        this.jmsTemplate.setReceiveTimeout(1);
    }

    /**
     *
     * @param task
     */
    public void sendTask(ConsumptionTask task) {
        Objects.requireNonNull(task);
        log.info("sending with convertAndSend() to queue <{}>", task );
        ConsumptionTask taskToSend = task;
        if (taskToSend.getTaskUUID() == null) {
            log.warn("Task UUID is not set. Generating NEW");
            taskToSend = taskToSend.generateTaskUUID();
        }

        jmsTemplate.convertAndSend(CONS_TASK_QUEUE, taskToSend);
    }

    /**
     *
     * @return
     */
    public int getTaskQueueSize() {
        BrowserCallback<Integer> browserCallback = (s, qb) -> Collections.list(qb.getEnumeration()).size();
        return jmsTemplate.browse(CONS_TASK_QUEUE, browserCallback);
    }

    /**
     * @return
     */
    public List<ConsumptionTask> viewTaskQueue() {

        BrowserCallback<List<Object>> browserCallback = (s, qb) -> Collections.list(qb.getEnumeration());

        return jmsTemplate.browse(CONS_TASK_QUEUE, browserCallback).stream().map(i -> {
            ConsumptionTask task = null;
            try {
                Object obj = messageConverter.fromMessage((Message) i);
                task = objToConsumptionTask(obj);
            } catch (JMSException e) {
                log.error("MQ message conversion to ConsumptionTask error", e);
            }
            return task;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    /**
     *
     * @param obj
     * @return
     */
    private ConsumptionTask objToConsumptionTask(Object obj) {
        ConsumptionTask task;
        if (obj instanceof ConsumptionTask) {
            task = (ConsumptionTask) obj;
        } else {
            log.warn("MQ message is not ConsumptionTask compatible");
            task = null;
        }
        return task;
    }


    /**
     * @return
     */
    public ConsumptionTask receiveTask() {
        Object obj = jmsTemplate.receiveAndConvert(ConsumptionTask.CONS_TASK_QUEUE);
        if (obj == null) {
            return null;
        }
        ConsumptionTask task = objToConsumptionTask(obj);
        return task;
    }


    public void processTaskQueue(Consumer<ConsumptionTask> consumer) {
        log.debug("Processing Queue");
        ConsumptionTask task = receiveTask();
        int cnt = 0;
        while (task != null) {
            log.debug("Processing task:{}", task);
            consumer.accept(task);
            log.debug("Processing task:{} complete. Read next", task);
            task = receiveTask();
            cnt++;
        }
        log.debug("Processing Queue complete. Finish: {} tasks", cnt);
    }

}
