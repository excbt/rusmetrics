package ru.excbt.datafuse.nmk.data.service;

import com.google.common.base.Preconditions;
import org.mapstruct.ap.internal.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@Service
public class SubscriberAccessService implements SecuredRoles {

    private static final Logger log = LoggerFactory.getLogger(SubscriberAccessService.class);

    private final ContZPointAccessRepository contZPointAccessRepository;

    private final ContZPointAccessHistoryRepository contZPointAccessHistoryRepository;

    private final ContObjectAccessRepository contObjectAccessRepository;

    private final ContObjectAccessHistoryRepository contObjectAccessHistoryRepository;

    private final SubscrContObjectService subscrContObjectService;

    @Autowired
    public SubscriberAccessService(ContZPointAccessRepository contZPointAccessRepository,
                                   ContZPointAccessHistoryRepository contZPointAccessHistoryRepository,
                                   ContObjectAccessRepository contObjectAccessRepository,
                                   ContObjectAccessHistoryRepository contObjectAccessHistoryRepository,
                                   SubscrContObjectService subscrContObjectService) {
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessHistoryRepository = contZPointAccessHistoryRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessHistoryRepository = contObjectAccessHistoryRepository;
        this.subscrContObjectService = subscrContObjectService;
    }

    /**
     * @param subscriberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findContZPointIds(Long subscriberId) {
        return contZPointAccessRepository.findContZPointIds(subscriberId);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllContZPointSubscriberIds() {
        return contZPointAccessRepository.findAllSubscriberIds();
    }

    /**
     * @param subscriberId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findContObjectIds(Long subscriberId) {
        return contObjectAccessRepository.findContObjectIdsBySubscriber(subscriberId);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllContObjectSubscriberIds() {
        return contObjectAccessRepository.findAllSubscriberIds();
    }

    /*
    TODO change for update
     */
    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        startContZPointAccess(subscriber, contZPoint, null);
    }


    /*
    TODO change for update
     */
    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime grantDateTime) {
        startContZPointAccess(subscriber, contZPoint, grantDateTime);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        finishContZPointAccess(subscriber, contZPoint, null);
    }

    private void startContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime grantDateTime) {
        Optional<Long> checkExistsing = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (!checkExistsing.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.saveAndFlush(access);
            saveStartContZPointAccessHistory(subscriber, contZPoint, grantDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }

    private void finishContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime revokeDateTime) {
        Optional<Long> checkExisting = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (checkExisting.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.delete(access);
            saveFinishContZPointAccessHistory(subscriber, contZPoint, revokeDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }


    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(Subscriber subscriber, ContObject contObject) {
        startContObjectAccess(subscriber, contObject, null);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime accessDateTime) {
        startContObjectAccess(subscriber, contObject, null);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContObjectAccess(Subscriber subscriber, ContObject contObject) {
        finishContObjectAccess(subscriber, contObject, null);
    }

    @Transactional
    @Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
    public void revokeContObjectAccess(ContObject contObject) {
        contObjectAccessRepository.findSubscriberByContObject(contObject.getId()).forEach((i) ->
            finishContObjectAccess(new Subscriber().id(i), contObject, null)
        );
//        contObjectAccessRepository.findByContObjectId(contObject.getId()).forEach((i)->
//                updateContObjectIdsAccess(new Subscriber().id(i), Arrays.asList(contObject.getId()), null, AccessAction.MERGE)
//            );
    }

    private void startContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime grantDateTime) {
        Optional<Long> checkExistsing = findContObjectIds(subscriber.getId()).stream().filter((i) -> i.equals(contObject.getId())).findFirst();
        if (!checkExistsing.isPresent()) {
            ContObjectAccess access = new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            contObjectAccessRepository.saveAndFlush(access);
            saveStartContObjectAccessHistory(subscriber, contObject, grantDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

        subscrContObjectService.access().linkSubscrContObject(subscriber, contObject,
            grantDateTime != null ? grantDateTime.toLocalDate() : LocalDate.now());

    }


    private void finishContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime revokeDateTime) {
        Optional<Long> checkExisting = contObjectAccessRepository.findByPK(subscriber.getId(), contObject.getId());

        if (checkExisting.isPresent()) {
            Preconditions.checkState(checkExisting.get() == 1);
            ContObjectAccess access = new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            contObjectAccessRepository.delete(access);
            saveFinishContObjectAccessHistory(subscriber, contObject, revokeDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

        subscrContObjectService.access().unlinkSubscrContObject(subscriber, contObject,
            revokeDateTime != null ? revokeDateTime.toLocalDate() : LocalDate.now());

    }

    private void saveStartContObjectAccessHistory(Subscriber subscriber, ContObject contObject, LocalDateTime grantDateTime) {
        ContObjectAccessHistory history = new ContObjectAccessHistory();
        history.setContObject(contObject);
        history.setSubscriber(subscriber);
        history.setGrantDate(grantDateTime == null ? LocalDate.now() : grantDateTime.toLocalDate());
        history.setGrantTime(grantDateTime == null ? LocalTime.now() : grantDateTime.toLocalTime());
        history.setGrantTZ(ZonedDateTime.now());
        contObjectAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveFinishContObjectAccessHistory(Subscriber subscriber, ContObject contObject, LocalDateTime revokeDateTime) {
        contObjectAccessHistoryRepository.findBySubscriberIdAndContObjectId(subscriber.getId(), contObject.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(revokeDateTime == null ? LocalDate.now() : revokeDateTime.toLocalDate());
            i.setRevokeTime(revokeDateTime == null ? LocalTime.now() : revokeDateTime.toLocalTime());
            i.setRevokeTZ(ZonedDateTime.now());
            contObjectAccessHistoryRepository.saveAndFlush(i);
        });
    }

    private void saveStartContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime grantDateTime) {
        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(grantDateTime == null ? LocalDate.now() : grantDateTime.toLocalDate());
        history.setGrantTime(grantDateTime == null ? LocalTime.now() : grantDateTime.toLocalTime());
        history.setGrantTZ(ZonedDateTime.now());
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveFinishContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime revokeDateTime) {
        contZPointAccessHistoryRepository.findBySubscriberIdAndContZPointId(subscriber.getId(), contZPoint.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(revokeDateTime == null ? LocalDate.now() : revokeDateTime.toLocalDate());
            i.setRevokeTime(revokeDateTime == null ? LocalTime.now() : revokeDateTime.toLocalTime());
            i.setRevokeTZ(ZonedDateTime.now());
            contZPointAccessHistoryRepository.saveAndFlush(i);
        });
    }



    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_SUBSCR_CREATE_CABINET})
    public void updateContObjectIdsAccess(final Subscriber subscriber, final List<Long> newContObjectIds,
                                          final LocalDateTime accessDateTime) {

        List<Long> existingContObjectIds = findContObjectIds(subscriber.getId());

        List<Long> addContObjectIds = new ArrayList<>();
        List<Long> delContObjectIds = new ArrayList<>();

        existingContObjectIds.forEach(i -> {
            if (!newContObjectIds.contains(i)) {
                delContObjectIds.add(i);
            }
        });

        newContObjectIds.forEach(i -> {
            if (!existingContObjectIds.contains(i)) {
                addContObjectIds.add(i);
            }
        });

        delContObjectIds.forEach((id) -> finishContObjectAccess(subscriber, new ContObject().id(id), accessDateTime));

        addContObjectIds.forEach((id) -> startContObjectAccess(subscriber, new ContObject().id(id), accessDateTime));

    }


}
