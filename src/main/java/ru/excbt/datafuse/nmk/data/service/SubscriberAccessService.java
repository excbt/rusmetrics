package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@Service
public class SubscriberAccessService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberAccessService.class);


    private final ContZPointAccessRepository contZPointAccessRepository;

    private final ContZPointAccessHistoryRepository contZPointAccessHistoryRepository;

    private final ContObjectAccessRepository contObjectAccessRepository;

    private final ContObjectAccessHistoryRepository contObjectAccessHistoryRepository;

    @Autowired
    public SubscriberAccessService(ContZPointAccessRepository contZPointAccessRepository,
                                   ContZPointAccessHistoryRepository contZPointAccessHistoryRepository,
                                   ContObjectAccessRepository contObjectAccessRepository,
                                   ContObjectAccessHistoryRepository contObjectAccessHistoryRepository) {
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessHistoryRepository = contZPointAccessHistoryRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessHistoryRepository = contObjectAccessHistoryRepository;
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findContZPointIds (Long subscriberId) {
        return contZPointAccessRepository.findContZPointIds(subscriberId);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllContZPointSubscriberIds() {
        return contZPointAccessRepository.findAllSubscriberIds();
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findContObjectIds (Long subscriberId) {
        return contObjectAccessRepository.findContObjectIds(subscriberId);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllContObjectSubscriberIds() {
        return contObjectAccessRepository.findAllSubscriberIds();
    }

    @Transactional
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        Optional<Long> checkExistsing = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (!checkExistsing.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.saveAndFlush(access);
            saveContZPointGrantHistory(subscriber, contZPoint);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }

    @Transactional
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        Optional<Long> checkExisting = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (checkExisting.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.delete(access);
            saveContZPointRevokeHistory(subscriber, contZPoint);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }


    private void saveContZPointGrantHistory(Subscriber subscriber, ContZPoint contZPoint) {
        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(LocalDate.now());
        history.setGrantTime(LocalTime.now());
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveContZPointRevokeHistory(Subscriber subscriber, ContZPoint contZPoint) {
        contZPointAccessHistoryRepository.findBySubscriberIdAndContZPointId(subscriber.getId(), contZPoint.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(LocalDate.now());
            i.setGrantTime(LocalTime.now());
            contZPointAccessHistoryRepository.saveAndFlush(i);
        });
    }

    @Transactional
    public void grantContObjectAccess(Subscriber subscriber, ContObject contObject) {
        Optional<Long> checkExistsing = findContObjectIds(subscriber.getId()).stream().filter((i) -> i.equals(contObject.getId())).findFirst();
        if (!checkExistsing.isPresent()) {
            ContObjectAccess access = new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            contObjectAccessRepository.saveAndFlush(access);
            saveContObjectGrantHistory(subscriber, contObject);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }
    }


    @Transactional
    public void revokeContObjectAccess(Subscriber subscriber, ContObject contObject) {
        Optional<Long> checkExisting = findContObjectIds(subscriber.getId()).stream().filter((i) -> i.equals(contObject.getId())).findFirst();
        if (checkExisting.isPresent()) {
            ContObjectAccess access = new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            contObjectAccessRepository.delete(access);
            saveContObjectRevokeHistory(subscriber, contObject);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }
    }


    /**
     *
     * @param subscriber
     * @param contObject
     */
    private void saveContObjectGrantHistory(Subscriber subscriber, ContObject contObject) {
        ContObjectAccessHistory history = new ContObjectAccessHistory();
        history.setContObject(contObject);
        history.setSubscriber(subscriber);
        history.setGrantDate(LocalDate.now());
        history.setGrantTime(LocalTime.now());
        contObjectAccessHistoryRepository.saveAndFlush(history);
    }

    /**
     *
     * @param subscriber
     * @param contObject
     */
    private void saveContObjectRevokeHistory(Subscriber subscriber, ContObject contObject) {
        contObjectAccessHistoryRepository.findBySubscriberIdAndContObjectId(subscriber.getId(), contObject.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(LocalDate.now());
            i.setGrantTime(LocalTime.now());
            contObjectAccessHistoryRepository.saveAndFlush(i);
        });
    }

}
