package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ConsumptionTaskSchedule {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskSchedule.class);

    private final ConsumptionService consumptionService;

    private final ConsumptionTaskService consumptionTaskService;

    private final AtomicBoolean taskActive = new AtomicBoolean();
    private final AtomicBoolean isProcessingEnabled = new AtomicBoolean(true);

    public ConsumptionTaskSchedule(ConsumptionService consumptionService, ConsumptionTaskService consumptionTaskService) {
        this.consumptionService = consumptionService;
        this.consumptionTaskService = consumptionTaskService;
    }


    @Scheduled(cron = "0 */2 * * * ?")
    @Transactional
    public void processScheduleTasks() {

        if (!isProcessingEnabled.get()) {
            log.warn("Processing is disabled");
            return;
        }

        log.debug("Process Schedule Tasks");

        if (!taskActive.compareAndSet(false, true)) {
            log.warn("Task processing already running");
            return;
        }

        try {
            consumptionTaskService.processTaskQueue(i -> {
                log.debug("Process task UUID {}, DataType: {}, dateFrom: {}, dateTo:{}", i.getTaskUUID(), i.getDataType(), i.getDateFrom(), i.getDateTo());
                consumptionService.processUniversal(i);
            } );
        } finally {
            if (taskActive.compareAndSet(true, false)) {
                log.debug("Running : false");
            } else {
                log.error("Running flag is not set");
            }
        }
        log.debug("Processing Schedule complete. OK");

    }

    @Transactional
    public void saveAndSendTask(final ConsumptionTask task) {
        ConsumptionTask savedTask = consumptionService.saveConsumptionTask(task, ConsumptionService.TaskState.SCHEDULED);
        consumptionTaskService.sendTask(savedTask);
    }


    public void processingEnable(){
        isProcessingEnabled.set(true);
    }

    public void processingDisable(){
        isProcessingEnabled.set(false);
    }

}
