package ru.excbt.datafuse.nmk.data.service;

import com.google.common.base.Preconditions;
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

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    private final static int ACCESS_TTL_WEEKS = 1;
    private final static TemporalAmount ACCESS_TTL = Period.ofWeeks(ACCESS_TTL_WEEKS);

    private final static Function<LocalDateTime, LocalDateTime> MAKE_ACCESS_TTL = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);
    private final static Function<ZonedDateTime, ZonedDateTime> MAKE_ACCESS_TTL_TZ = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);

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
        startContZPointAccess(subscriber, contZPoint, LocalDateTime.now(), ZonedDateTime.now());
    }


    /*
    TODO change for update
     */
    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime subscriberDateTime) {
        startContZPointAccess(subscriber, contZPoint, subscriberDateTime, ZonedDateTime.now());
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        finishContZPointAccess(subscriber, contZPoint, LocalDateTime.now(), ZonedDateTime.now());
    }

    private void startContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime subscriberDateTime,
                                       ZonedDateTime currentDateTime) {
        Optional<Long> checkExisting = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (!checkExisting.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.saveAndFlush(access);
            saveStartContZPointAccessHistory(subscriber, contZPoint, subscriberDateTime, currentDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }

    private void finishContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime subscriberDateTime, ZonedDateTime currDateTime) {
        Preconditions.checkNotNull(currDateTime);

        Optional<Long> checkExisting = findContZPointIds(subscriber.getId()).stream().filter((i) -> i.equals(contZPoint.getId())).findFirst();
        if (checkExisting.isPresent()) {
            ContZPointAccess access = new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
            contZPointAccessRepository.delete(access);
            saveFinishContZPointAccessHistory(subscriber, contZPoint, subscriberDateTime, currDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }


    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(Subscriber subscriber, ContObject contObject) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        startContObjectAccess(subscriber, contObject, currDateTime.toLocalDateTime(), currDateTime);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime subscriberDateTime) {
        startContObjectAccess(subscriber, contObject, subscriberDateTime, ZonedDateTime.now());
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContObjectAccess(Subscriber subscriber, ContObject contObject) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        finishContObjectAccess(subscriber, contObject, currDateTime.toLocalDateTime(), currDateTime);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContObjectAccess(ContObject contObject) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        contObjectAccessRepository.findSubscriberByContObject(contObject.getId()).forEach((i) ->
            finishContObjectAccess(new Subscriber().id(i), contObject, currDateTime.toLocalDateTime(), currDateTime)
        );
//        contObjectAccessRepository.findByContObjectId(contObject.getId()).forEach((i)->
//                updateContObjectIdsAccess(new Subscriber().id(i), Arrays.asList(contObject.getId()), null, AccessAction.MERGE)
//            );
    }

    private void startContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime subscriberDateTime, ZonedDateTime currDateTime) {
        Optional<Long> checkExisting = findContObjectIds(subscriber.getId()).stream().filter((i) -> i.equals(contObject.getId())).findFirst();
        if (!checkExisting.isPresent()) {

            ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            ContObjectAccess access = Optional.ofNullable(contObjectAccessRepository.findOne(accessPK))
                .orElse(new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId()));
            access.clearAccessTTL();
            contObjectAccessRepository.saveAndFlush(access);
            saveStartContObjectAccessHistory(subscriber, contObject, subscriberDateTime, currDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

        subscrContObjectService.access().linkSubscrContObject(subscriber, contObject,
            subscriberDateTime != null ? subscriberDateTime.toLocalDate() : LocalDate.now());

    }


    private void finishContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime subscriberDateTime, ZonedDateTime currDateTime) {
        Optional<Long> checkExisting = contObjectAccessRepository.findByPK(subscriber.getId(), contObject.getId());

        final LocalDateTime subscriberDT = subscriberDateTime != null ? subscriberDateTime : LocalDateTime.now();

        if (checkExisting.isPresent()) {
            Preconditions.checkState(checkExisting.get() == 1);
            ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
            ContObjectAccess access = contObjectAccessRepository.findOne(accessPK);

            access.setAccessTtl(MAKE_ACCESS_TTL.apply(subscriberDT));
            access.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(currDateTime));

            contObjectAccessRepository.save(access);
            saveFinishContObjectAccessHistory(subscriber, contObject, subscriberDT, currDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

        subscrContObjectService.access().unlinkSubscrContObject(subscriber, contObject,
            subscriberDT.toLocalDate());

    }

    private void saveStartContObjectAccessHistory(Subscriber subscriber, ContObject contObject, LocalDateTime grantDateTime, ZonedDateTime currDateTime) {

        Preconditions.checkNotNull(currDateTime);

        ContObjectAccessHistory history = new ContObjectAccessHistory();
        history.setContObject(contObject);
        history.setSubscriber(subscriber);
        history.setGrantDate(grantDateTime == null ? LocalDate.now() : grantDateTime.toLocalDate());
        history.setGrantTime(grantDateTime == null ? LocalTime.now() : grantDateTime.toLocalTime());
        history.setGrantTZ(currDateTime);
        contObjectAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveFinishContObjectAccessHistory(Subscriber subscriber, ContObject contObject, LocalDateTime subscriberDateTime,
                                                   ZonedDateTime currentDateTime) {
        Preconditions.checkNotNull(subscriberDateTime);
        Preconditions.checkNotNull(currentDateTime);


        contObjectAccessHistoryRepository.findBySubscriberIdAndContObjectId(subscriber.getId(), contObject.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(subscriberDateTime.toLocalDate());
            i.setRevokeTime(subscriberDateTime.toLocalTime());
            i.setRevokeTZ(currentDateTime);
            i.setAccessTtl(MAKE_ACCESS_TTL.apply(subscriberDateTime));
            i.setAccessTtlTZ(MAKE_ACCESS_TTL_TZ.apply(currentDateTime));
            contObjectAccessHistoryRepository.saveAndFlush(i);
        });
    }

    private void saveStartContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime subscriberDateTime,
                                                  ZonedDateTime currDateTime) {
        Preconditions.checkNotNull(subscriberDateTime);
        Preconditions.checkNotNull(currDateTime);

        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(subscriberDateTime.toLocalDate());
        history.setGrantTime(subscriberDateTime.toLocalTime());
        history.setGrantTZ(currDateTime);
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveFinishContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime subscriberDateTime,
                                                   ZonedDateTime currDateTime) {
        Preconditions.checkNotNull(subscriberDateTime);
        Preconditions.checkNotNull(currDateTime);

        contZPointAccessHistoryRepository.findBySubscriberIdAndContZPointId(subscriber.getId(), contZPoint.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(subscriberDateTime.toLocalDate());
            i.setRevokeTime(subscriberDateTime.toLocalTime());
            i.setRevokeTZ(ZonedDateTime.now());
            contZPointAccessHistoryRepository.saveAndFlush(i);
        });
    }


    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_SUBSCR_CREATE_CABINET})
    public void updateContObjectIdsAccess(final Subscriber subscriber, final List<Long> newContObjectIds,
                                          final LocalDateTime accessDateTime) {

        List<Long> existingContObjectIds = contObjectAccessRepository.findContObjectIdsBySubscriber(subscriber.getId());

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

        ZonedDateTime currDateTime = ZonedDateTime.now();

        delContObjectIds.forEach((id) -> finishContObjectAccess(subscriber, new ContObject().id(id), accessDateTime, currDateTime));

        addContObjectIds.forEach((id) -> startContObjectAccess(subscriber, new ContObject().id(id), accessDateTime, currDateTime));

    }


}
