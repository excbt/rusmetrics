package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointDeviceHistory;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDeviceHistoryDTO;
import ru.excbt.datafuse.nmk.data.repository.ContZPointDeviceHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointDeviceHistoryMapper;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContZPointDeviceHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ContZPointDeviceHistoryService.class);

    private static final PageRequest LIMIT_1 = new PageRequest(0, 1);

    private final ContZPointDeviceHistoryRepository historyRepository;

    private final ContZPointDeviceHistoryMapper historyMapper;

    private final DeviceObjectRepository deviceObjectRepository;

    @Autowired
    public ContZPointDeviceHistoryService(ContZPointDeviceHistoryRepository historyRepository, ContZPointDeviceHistoryMapper historyMapper, DeviceObjectRepository deviceObjectRepository) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.deviceObjectRepository = deviceObjectRepository;
    }

    @Transactional
    public boolean saveHistory(ContZPoint contZPoint) {
        Objects.requireNonNull(contZPoint);

        if(contZPoint.getDeviceObject() == null || contZPoint.getDeviceObject().getId() == null) {
            return false;
        }

        List<ContZPointDeviceHistory> lastHistories = historyRepository.findLastByContZPoint(contZPoint,LIMIT_1);

        boolean doInsert = false;
        int revision = 1;
        if (lastHistories.size() == 0) {
            doInsert = true;
        } else {
            ContZPointDeviceHistory currentRecord = lastHistories.get(0);
            if (!currentRecord.getDeviceObject().getId().equals(contZPoint.getDeviceObject().getId())) {
                currentRecord.setEndDate(LocalDateTime.now());
                historyRepository.saveAndFlush(currentRecord);
                revision = currentRecord.getRevision() + 1;
                doInsert = true;
            }
        }

        if (doInsert) {
            ContZPointDeviceHistory newRecord = new ContZPointDeviceHistory();
            newRecord.setContZPoint(contZPoint);
            DeviceObject deviceObject;
            if (contZPoint.getDeviceObject().getLastModifiedBy() == null) {
                log.warn("contZPoint.deviceObject is from DTO. Spring Data Jpa Bug");
                deviceObject = deviceObjectRepository.findById(contZPoint.getDeviceObject().getId())
                .orElseThrow(() -> new EntityNotFoundException(DeviceObject.class, contZPoint.getDeviceObject().getId()));
            } else {
                deviceObject = contZPoint.getDeviceObject();
            }

            newRecord.setDeviceObject(deviceObject);
            newRecord.setStartDate(LocalDateTime.now());
            newRecord.setRevision(revision);
            //log.info("Insert contZPointId:{}, deviceObjectId:{}", contZPoint.getId(), contZPoint.getDeviceObject().getId());
            ContZPointDeviceHistory savedHistory = historyRepository.saveAndFlush(newRecord);
//            log.info("Insert contZPointId:{}, deviceObjectId:{}",
//                savedHistory.getContZPoint().getId(),
//                savedHistory.getDeviceObject().getId());
        }

        return doInsert;

    }


    /**
     *
     * @param contZPoint
     */
    @Transactional
    public void finishHistory(ContZPoint contZPoint) {

        List<ContZPointDeviceHistory> actualHistory = historyRepository.findLastByContZPoint(contZPoint,LIMIT_1);
        actualHistory.forEach(i -> {
                i.setEndDate(LocalDateTime.now());
            historyRepository.saveAndFlush(i);
            }
        );
    }

    /**
     *
     * @param contZPoint
     */
    @Transactional
    public void clearHistory(ContZPoint contZPoint) {
        if (contZPoint == null || contZPoint.getId() == null) {
            return;
        }
        List<ContZPointDeviceHistory> actualHistory = historyRepository.findAllByContZPoint(contZPoint);
        actualHistory.forEach(i -> {
            historyRepository.delete(i);
            }
        );
    }

    @Transactional
    public List<ContZPointDeviceHistoryDTO> findHistory(ContZPoint contZPoint) {
        if (contZPoint == null || contZPoint.getId() == null) {
            return Collections.emptyList();
        }
        List<ContZPointDeviceHistory> historyList = historyRepository.findAllByContZPoint(contZPoint);
        return historyList.stream().map(i -> historyMapper.toDto(i)).collect(Collectors.toList());
    }

}
