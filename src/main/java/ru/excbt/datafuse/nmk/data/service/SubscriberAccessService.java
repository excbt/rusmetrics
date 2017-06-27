package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@Service
public class SubscriberAccessService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberAccessService.class);

    public SubscriberAccessService(ContZPointAccessRepository contZPointAccessRepository) {
        this.contZPointAccessRepository = contZPointAccessRepository;
    }

    private final ContZPointAccessRepository contZPointAccessRepository;

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
            contZPointAccessRepository.save(access);
        } else {
            log.warn("Access for ContZPoint (id={}) for Subscriber(id={}) already granted", contZPoint.getId(), subscriber.getId());
        }
    }

    @Transactional
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        Optional<Long> checkExistsing = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (checkExistsing.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.delete(access);
        } else {
            log.warn("Access for ContZPoint (id={}) for Subscriber(id={}) already revoked", contZPoint.getId(), subscriber.getId());
        }
    }

}
