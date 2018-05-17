package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final SubscrContObjectService subscrContObjectService;

    private final ContZPointRepository contZPointRepository;

    private final static int ACCESS_TTL_WEEKS = 1;
    private final static TemporalAmount ACCESS_TTL = Period.ofWeeks(ACCESS_TTL_WEEKS);

    private final static Function<LocalDateTime, LocalDateTime> MAKE_ACCESS_TTL = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);
    private final static Function<ZonedDateTime, ZonedDateTime> MAKE_ACCESS_TTL_TZ = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);

    private final static Function<LocalDateTime, ZonedDateTime> MAKE_REVOKE_TZ = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).atZone(ZoneId.systemDefault());

    public static final String TRIAL_ACCESS = "TRIAL";

    @Autowired
    public SubscriberAccessService(ContZPointAccessRepository contZPointAccessRepository,
                                   ContZPointAccessHistoryRepository contZPointAccessHistoryRepository,
                                   ContObjectAccessRepository contObjectAccessRepository,
                                   ContObjectAccessHistoryRepository contObjectAccessHistoryRepository,
                                   SubscrContObjectService subscrContObjectService, ContZPointRepository contZPointRepository) {
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessHistoryRepository = contZPointAccessHistoryRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessHistoryRepository = contObjectAccessHistoryRepository;
        this.subscrContObjectService = subscrContObjectService;
        this.contZPointRepository = contZPointRepository;
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
        return contObjectAccessRepository.findAllContObjectIdsNoTtl(subscriberId);
    }

    @Transactional(readOnly = true)
    public List<Long> findContObjectIds(PortalUserIds portalUserIds) {
        return contObjectAccessRepository.findAllContObjectIdsNoTtl(portalUserIds.getSubscriberId());
    }

    @Transactional(readOnly = true)
    public List<Long> findAllContObjectSubscriberIds() {
        return contObjectAccessRepository.findAllSubscriberIdsNoTtl();
    }

    /*
    TODO change for update
     */
    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(ContZPoint contZPoint, Subscriber subscriber) {
        startContZPointAccess(contZPoint, LocalDateTime.now(), subscriber);
    }


    /*
    TODO change for update
     */
    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(ContZPoint contZPoint, LocalDateTime accessDateTime, Subscriber subscriber) {
        startContZPointAccess(contZPoint, accessDateTime, subscriber);
    }

    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(ContZPoint contZPoint, Subscriber subscriber) {
        finishContZPointAccess(contZPoint, LocalDateTime.now(), ZonedDateTime.now(), subscriber);
    }

    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(ContZPoint contZPoint, LocalDateTime revokeDateTime, Subscriber subscriber) {
        finishContZPointAccess(contZPoint, revokeDateTime, ZonedDateTime.now(), subscriber);
    }

    private void startContZPointAccess(ContZPoint contZPoint, LocalDateTime accessDateTime, Subscriber subscriber) {

        ContZPointAccess.PK accessPK = new ContZPointAccess.PK().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
        Optional<ContZPointAccess> checkExisting = Optional.ofNullable(contZPointAccessRepository.findOne(accessPK));
        ContZPointAccess access = checkExisting.orElse(new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId()));

        if (!checkExisting.isPresent() || (checkExisting.isPresent() && checkExisting.get().getRevokeTz() != null)) {
            access.startNewAccess();
            contZPointAccessRepository.saveAndFlush(access);
            saveStartContZPointAccessHistory(contZPoint, accessDateTime, subscriber);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted. Nothing to grant",
                ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }

//        access.startNewAccess();
//        contZPointAccessRepository.saveAndFlush(access);
//        saveStartContZPointAccessHistory(subscriber, contZPoint, accessDateTime);
//        if (checkExisting.isPresent()) {
//            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
//        }
    }

    private void finishContZPointAccess(ContZPoint contZPoint,
                                        LocalDateTime revokeDateTime,
                                        ZonedDateTime currentDateTime,
                                        Subscriber subscriber) {

        checkNotNull(currentDateTime);

        ContZPointAccess.PK accessPK = new ContZPointAccess.PK().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
        Optional<ContZPointAccess> checkExisting = Optional.ofNullable(contZPointAccessRepository.findOne(accessPK));

        final LocalDateTime revokeDT = revokeDateTime != null ? revokeDateTime : LocalDateTime.now();

        if (checkExisting.isPresent()) {
            ContZPointAccess access = checkExisting.get();
            access.setRevokeTz(MAKE_REVOKE_TZ.apply(revokeDT));
            if (revokeDT.toLocalDate().isAfter(LocalDate.now()) || revokeDT.toLocalDate().isEqual(LocalDate.now())){
                access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
                access.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(currentDateTime));
            }

            access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
            access.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(revokeDT.atZone(ZoneId.systemDefault())));
            contZPointAccessRepository.save(access);
            saveFinishContZPointAccessHistory(contZPoint, revokeDateTime, subscriber);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked. Nothing to revoke", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }


    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(ContObject contObject, Subscriber subscriber) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        startContObjectAccess(contObject, currDateTime.toLocalDateTime(), currDateTime, subscriber);
    }

    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void grantContObjectAccess(ContObject contObject, LocalDateTime subscriberDateTime, Subscriber subscriber) {
        startContObjectAccess(contObject, subscriberDateTime, ZonedDateTime.now(), subscriber);
    }

    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void revokeContObjectAccess(ContObject contObject, Subscriber subscriber) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        finishContObjectAccess(contObject, currDateTime.toLocalDateTime(), currDateTime, subscriber);
    }

    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN})
    public void revokeContObjectAccess(ContObject contObject) {
        final ZonedDateTime currDateTime = ZonedDateTime.now();
        contObjectAccessRepository.findSubscriberByContObjectNoTtl(contObject.getId()).forEach((i) ->
            finishContObjectAccess(contObject, currDateTime.toLocalDateTime(), currDateTime, new Subscriber().id(i))
        );
    }

    /**
     *
     * @param contObject
     * @param subscriberDateTime
     * @param currDateTime
     * @param subscriber
     */
    private void startContObjectAccess(ContObject contObject,
                                       LocalDateTime subscriberDateTime,
                                       ZonedDateTime currDateTime,
                                       Subscriber subscriber) {

        ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
        Optional<ContObjectAccess> checkExisting = Optional.ofNullable(contObjectAccessRepository.findOne(accessPK));

        ContObjectAccess access = checkExisting.orElse(new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId()));

        if (!checkExisting.isPresent() || (checkExisting.isPresent() && checkExisting.get().getRevokeTz() != null)) {
            access.startNewAccess();
            contObjectAccessRepository.saveAndFlush(access);
            saveStartContObjectAccessHistory(contObject, subscriberDateTime, currDateTime, subscriber);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted. Nothing to grant",
                ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

//        if (checkExisting.isPresent()) {
//            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
//        }

/*
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
*/

        subscrContObjectService.access().linkSubscrContObject(subscriber, contObject,
            subscriberDateTime != null ? subscriberDateTime.toLocalDate() : LocalDate.now());


        contZPointRepository.findContZPointIds(contObject.getId()).forEach(i -> startContZPointAccess(new ContZPoint().id(i), subscriberDateTime, subscriber));


    }

    /**
     *
     * @param contObject
     * @param revokeDateTime
     * @param currDateTime
     * @param subscriber
     */
    private void finishContObjectAccess(ContObject contObject, LocalDateTime revokeDateTime, ZonedDateTime currDateTime, Subscriber subscriber) {
        ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
        Optional<ContObjectAccess> checkExisting = Optional.ofNullable(contObjectAccessRepository.findOne(accessPK));

        final LocalDateTime revokeDT = revokeDateTime != null ? revokeDateTime : LocalDateTime.now();


        if (checkExisting.isPresent()) {
            ContObjectAccess access = checkExisting.get();

            access.setRevokeTz(MAKE_REVOKE_TZ.apply(revokeDT));
            if (revokeDT.toLocalDate().isAfter(LocalDate.now()) || revokeDT.toLocalDate().isEqual(LocalDate.now())){
                access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
                access.setAccessTtlTz(ZonedDateTime.now().plus(ACCESS_TTL));
            }

            contObjectAccessRepository.save(access);
            saveFinishContObjectAccessHistory(contObject, revokeDT, currDateTime, subscriber);

            { /// Access for ContZPoint
                subscrContObjectService.access().unlinkSubscrContObject(subscriber, contObject,
                    revokeDT.toLocalDate());
                contZPointRepository.findContZPointIds(contObject.getId()).forEach(i -> finishContZPointAccess(new ContZPoint().id(i), revokeDateTime, currDateTime, subscriber));
            }

        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked. Nothing to revoke", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

    }

    /**
     *
     * @param contObject
     * @param grantDateTime
     * @param currDateTime
     * @param subscriber
     */
    private void saveStartContObjectAccessHistory(ContObject contObject, LocalDateTime grantDateTime, ZonedDateTime currDateTime, Subscriber subscriber) {

        checkNotNull(currDateTime);

        ContObjectAccessHistory history = new ContObjectAccessHistory();
        history.setContObject(contObject);
        history.setSubscriber(subscriber);
        history.setGrantDate(grantDateTime == null ? LocalDate.now() : grantDateTime.toLocalDate());
        history.setGrantTime(grantDateTime == null ? LocalTime.now() : grantDateTime.toLocalTime());
        history.setGrantTZ(currDateTime);
        contObjectAccessHistoryRepository.saveAndFlush(history);
    }

    /**
     *
     * @param contObject
     * @param subscriberDateTime
     * @param currentDateTime
     * @param subscriber
     */
    private void saveFinishContObjectAccessHistory(ContObject contObject, LocalDateTime subscriberDateTime,
                                                   ZonedDateTime currentDateTime,
                                                   Subscriber subscriber) {
        checkNotNull(subscriberDateTime);
        checkNotNull(currentDateTime);


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

    /**
     *
     * @param contZPoint
     * @param accessDateTime
     * @param subscriber
     */
    private void saveStartContZPointAccessHistory(ContZPoint contZPoint, LocalDateTime accessDateTime, Subscriber subscriber) {
        checkNotNull(accessDateTime);

        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(accessDateTime.toLocalDate());
        history.setGrantTime(accessDateTime.toLocalTime());
        history.setGrantTZ(accessDateTime.atZone(ZoneId.systemDefault()));
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    /**
     *
     * @param contZPoint
     * @param revokeDateTime
     * @param subscriber
     */
    private void saveFinishContZPointAccessHistory(ContZPoint contZPoint, LocalDateTime revokeDateTime, Subscriber subscriber) {
        checkNotNull(revokeDateTime);

        contZPointAccessHistoryRepository.findBySubscriberIdAndContZPointId(subscriber.getId(), contZPoint.getId())
            .stream().filter((i) -> i.getRevokeDate() == null).forEach((i) -> {
            i.setRevokeDate(revokeDateTime.toLocalDate());
            i.setRevokeTime(revokeDateTime.toLocalTime());
            i.setRevokeTZ(revokeDateTime.atZone(ZoneId.systemDefault()));
            i.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDateTime));
            i.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(revokeDateTime.atZone(ZoneId.systemDefault())));
            contZPointAccessHistoryRepository.saveAndFlush(i);
        });
    }

    /**
     *
     * @param newContObjectIds
     * @param subscriberDateTime
     * @param subscriber
     */
    @Transactional
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.RMA_CONT_OBJECT_ADMIN, AuthoritiesConstants.SUBSCR_CREATE_CABINET})
    public void updateContObjectIdsAccess(final List<Long> newContObjectIds,
                                          final LocalDateTime subscriberDateTime,
                                          final Subscriber subscriber) {

        List<Long> existingContObjectIds = contObjectAccessRepository.findAllContObjectIdsNoTtl(subscriber.getId());

        List<Long> addContObjectIds = new ArrayList<>();
        List<Long> delContObjectIds = new ArrayList<>();

        LocalDateTime accessDateTime = subscriberDateTime != null ? subscriberDateTime : LocalDateTime.now() ;
        ZonedDateTime currDateTime = ZonedDateTime.now();

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


        delContObjectIds.forEach((id) -> finishContObjectAccess(new ContObject().id(id), accessDateTime, currDateTime, subscriber));

        addContObjectIds.forEach((id) -> startContObjectAccess(
            new ContObject().id(id),
            accessDateTime,
            currDateTime,
            subscriber));

    }

    /**
     *
     */
    @Scheduled(cron = "0 */2 * * * ?")
    @Transactional
    public void sheduleCleanupAccessByTtl() {
        processContObjectRevoke();
        processContZPointRevoke();
        cleanupContObjectAccess();
        cleanupContZPointAccess();
    }


    /**
     *
     */
    @Transactional
    public void cleanupContObjectAccess() {
        log.trace("CONT_OBJECT END OF ACCESS");
        contObjectAccessRepository.findAllAccessTtlTZ(ZonedDateTime.now()).forEach( a -> {
                log.trace("Subscriber {}, ContObjectId {}, AccessTTL: {}", a.getSubscriberId(), a.getContObjectId(), a.getAccessTtl());
                contObjectAccessRepository.delete(a);
            }
        );
    }

    /**
     *
     */
    @Transactional
    public void cleanupContZPointAccess() {
        log.trace("CONT_ZPOINT END OF ACCESS");
        contZPointAccessRepository.findAllAccessTtlTZ(ZonedDateTime.now()).forEach( a -> {
                log.trace("Subscriber {}, ContZPoint {}, AccessTTL: {}", a.getSubscriberId(), a.getContZPointId(), a.getAccessTtl());
                contZPointAccessRepository.delete(a);
            }
        );
    }

    /**
     *
     */
    private void processContObjectRevoke() {
        log.debug("CONT_OBJECT PROCESS REVOKE");
        contObjectAccessRepository.findAllRevokeTZ(ZonedDateTime.now()).stream().filter(a -> a.getAccessTtl() == null)
            .forEach( a -> {
                log.trace("Subscriber {}, ContObjectId {}, AccessTTL: {}", a.getSubscriberId(), a.getContObjectId(), a.getRevokeTz());
                if (TRIAL_ACCESS.equals(a.getAccessType())) {
                    contObjectAccessRepository.delete(a);
                } else {
                    a.setAccessTtl(MAKE_ACCESS_TTL.apply(a.getRevokeTz().toLocalDateTime()));
                    a.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(a.getRevokeTz()));
                    contObjectAccessRepository.save(a);
                }
            }
        );
    }

    private void processContZPointRevoke() {
        log.trace("CONT_ZPOINT PROCESS REVOKE");
        contZPointAccessRepository.findAllRevokeTZ(ZonedDateTime.now()).stream().filter(a -> a.getAccessTtl() == null)
            .forEach( a -> {
                log.trace("Subscriber {}, ContObjectId {}, AccessTTL: {}", a.getSubscriberId(), a.getContZPointId(), a.getRevokeTz());
                if (TRIAL_ACCESS.equals(a.getAccessType())) {
                    contZPointAccessRepository.delete(a);
                } else {
                    a.setAccessTtl(MAKE_ACCESS_TTL.apply(a.getRevokeTz().toLocalDateTime()));
                    a.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(a.getRevokeTz()));
                    contZPointAccessRepository.save(a);
                }
            }
        );
    }



}
