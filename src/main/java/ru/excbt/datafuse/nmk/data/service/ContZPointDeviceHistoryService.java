package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointDeviceHistory;
import ru.excbt.datafuse.nmk.data.repository.ContZPointDeviceHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ContZPointDeviceHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ContZPointDeviceHistoryService.class);

    private static final PageRequest LIMIT_1 = new PageRequest(0, 1);

    private final ContZPointDeviceHistoryRepository repository;

    @Autowired
    public ContZPointDeviceHistoryService(ContZPointDeviceHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean saveHistory(ContZPoint contZPoint) {
        Objects.requireNonNull(contZPoint);

        if(contZPoint.getDeviceObject() == null || contZPoint.getDeviceObject().getId() == null) {
            return false;
        }

        List<ContZPointDeviceHistory> lastHistories = repository.findLastByContZPoint(contZPoint,LIMIT_1);

        boolean doInsert = false;
        int revision = 1;
        if (lastHistories.size() == 0) {
            doInsert = true;
        } else {
            ContZPointDeviceHistory currentRecord = lastHistories.get(0);
            if (!currentRecord.getDeviceObject().getId().equals(contZPoint.getDeviceObject().getId())) {
                currentRecord.setEndDate(LocalDateTime.now());
                repository.saveAndFlush(currentRecord);
                revision = currentRecord.getRevision() + 1;
                doInsert = true;
            }
        }

        if (doInsert) {
            ContZPointDeviceHistory newRecord = new ContZPointDeviceHistory();
            newRecord.setContZPoint(contZPoint);
            newRecord.setDeviceObject(contZPoint.getDeviceObject());
            newRecord.setStartDate(LocalDateTime.now());
            newRecord.setRevision(revision);
            log.info("Insert contZPointId:{}, deviceObjectId:{}", contZPoint.getId(), contZPoint.getDeviceObject().getId());
            repository.saveAndFlush(newRecord);

        }

        return doInsert;
//
//        ContZPointDeviceHistory closedRecord = repository.findLastByContZPoint(contZPoint,LIMIT_1).stream()
//            .filter( i -> !i.getDeviceObject().equals(contZPoint.getDeviceObject()))
//            .findFirst()
//            .map(history ->
//                {
//                    history.setEndDate(LocalDateTime.now());
//                    return repository.save(history);
//                }).orElse(null);
//
//        ContZPointDeviceHistory contZPointDeviceCurrent = new ContZPointDeviceHistory();
//        contZPointDeviceCurrent.setContZPoint(contZPoint);
//        contZPointDeviceCurrent.setDeviceObject(contZPoint.getDeviceObject());
//        contZPointDeviceCurrent.setStartDate(LocalDateTime.now());
//        contZPointDeviceCurrent.setRevision(closedRecord == null ? 1 : closedRecord.getRevision() + 1);
//        repository.save(contZPointDeviceCurrent);
    }


    /**
     *
     * @param contZPoint
     */
    public void finishHistory(ContZPoint contZPoint) {

        List<ContZPointDeviceHistory> actualHistory = repository.findLastByContZPoint(contZPoint,LIMIT_1);
        actualHistory.forEach(i -> {
                i.setEndDate(LocalDateTime.now());
                repository.saveAndFlush(i);
            }
        );
    }

}
