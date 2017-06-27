package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccessHistory;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
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

    @Autowired
    public SubscriberAccessService(ContZPointAccessRepository contZPointAccessRepository, ContZPointAccessHistoryRepository contZPointAccessHistoryRepository) {
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessHistoryRepository = contZPointAccessHistoryRepository;
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
    public List<Long> findAllSubscribers() {
        return contZPointAccessRepository.findAllSubscriberIds();
    }

    @Transactional
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        Optional<Long> checkExistsing = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (!checkExistsing.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.saveAndFlush(access);
            saveGrantHistory(subscriber, contZPoint);
        } else {
            log.warn("Access for ContZPoint (id={}) for Subscriber(id={}) already granted", contZPoint.getId(), subscriber.getId());
        }
    }

    @Transactional
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        Optional<Long> checkExisting = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (checkExisting.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.delete(access);
            saveRevokeHistory(subscriber, contZPoint);
        } else {
            log.warn("Access for ContZPoint (id={}) for Subscriber(id={}) already revoked", contZPoint.getId(), subscriber.getId());
        }
    }


    private void saveGrantHistory(Subscriber subscriber, ContZPoint contZPoint) {
        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(LocalDate.now());
        history.setGrantTime(LocalTime.now());
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveRevokeHistory(Subscriber subscriber, ContZPoint contZPoint) {
        contZPointAccessHistoryRepository.findBySubscriberIdAndContZPointId(subscriber.getId(), contZPoint.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(LocalDate.now());
            i.setGrantTime(LocalTime.now());
            contZPointAccessHistoryRepository.saveAndFlush(i);
        });
    }

}
