package ru.excbt.datafuse.nmk.data.service;

import com.google.common.base.Preconditions;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final ContZPointRepository contZPointRepository;

    private final static int ACCESS_TTL_WEEKS = 1;
    //private final static TemporalAmount ACCESS_TTL = Period.ofWeeks(ACCESS_TTL_WEEKS);
    private final static TemporalAmount ACCESS_TTL = Duration.ofMinutes(5);

//    private final static Function<LocalDateTime, LocalDateTime> MAKE_ACCESS_TTL = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);
    private final static Function<LocalDateTime, LocalDateTime> MAKE_ACCESS_TTL = (d) -> LocalDateTime.now().plus(ACCESS_TTL);
//    private final static Function<ZonedDateTime, ZonedDateTime> MAKE_ACCESS_TTL_TZ = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).plus(ACCESS_TTL);
    private final static Function<ZonedDateTime, ZonedDateTime> MAKE_ACCESS_TTL_TZ = (d) -> ZonedDateTime.now().plus(ACCESS_TTL);

    private final static Function<LocalDateTime, ZonedDateTime> MAKE_REVOKE_TZ = (d) -> d.truncatedTo(ChronoUnit.DAYS).plusDays(1).atZone(ZoneId.systemDefault());

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
    public List<Long> findAllContObjectSubscriberIds() {
        return contObjectAccessRepository.findAllSubscriberIdsNoTtl();
    }

    /*
    TODO change for update
     */
    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        startContZPointAccess(subscriber, contZPoint, LocalDateTime.now());
    }


    /*
    TODO change for update
     */
    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void grantContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime accessDateTime) {
        startContZPointAccess(subscriber, contZPoint, accessDateTime);
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint) {
        finishContZPointAccess(subscriber, contZPoint, LocalDateTime.now(), ZonedDateTime.now());
    }

    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN})
    public void revokeContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime revokeDateTime) {
        finishContZPointAccess(subscriber, contZPoint, revokeDateTime, ZonedDateTime.now());
    }

    private void startContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime accessDateTime) {

        ContZPointAccess.PK accessPK = new ContZPointAccess.PK().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
        Optional<ContZPointAccess> checkExisting = Optional.ofNullable(contZPointAccessRepository.findOne(accessPK));
        ContZPointAccess access = checkExisting.orElse(new ContZPointAccess().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId()));
        access.startNewAccess();
        contZPointAccessRepository.saveAndFlush(access);
        saveStartContZPointAccessHistory(subscriber, contZPoint, accessDateTime);
        if (checkExisting.isPresent()) {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContZPoint.class.getSimpleName(), contZPoint.getId(), subscriber.getId());
        }
    }

    private void finishContZPointAccess(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime revokeDateTime,
                                        ZonedDateTime currentDateTime) {

        checkNotNull(currentDateTime);

        ContZPointAccess.PK accessPK = new ContZPointAccess.PK().subscriberId(subscriber.getId()).contZPointId(contZPoint.getId());
        Optional<ContZPointAccess> checkExisting = Optional.ofNullable(contZPointAccessRepository.findOne(accessPK));

        final LocalDateTime revokeDT = revokeDateTime != null ? revokeDateTime : LocalDateTime.now();

        if (checkExisting.isPresent()) {
            ContZPointAccess access = checkExisting.get();
            access.setRevokeTZ(MAKE_REVOKE_TZ.apply(revokeDT));
            if (revokeDT.toLocalDate().isAfter(LocalDate.now()) || revokeDT.toLocalDate().isEqual(LocalDate.now())){
                access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
                access.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(currentDateTime));
            }

            access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
            access.setAccessTtlTz(MAKE_ACCESS_TTL_TZ.apply(revokeDT.atZone(ZoneId.systemDefault())));
            contZPointAccessRepository.save(access);
            saveFinishContZPointAccessHistory(subscriber, contZPoint, revokeDateTime);
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
        contObjectAccessRepository.findSubscriberByContObjectNoTtl(contObject.getId()).forEach((i) ->
            finishContObjectAccess(new Subscriber().id(i), contObject, currDateTime.toLocalDateTime(), currDateTime)
        );
    }

    private void startContObjectAccess(Subscriber subscriber,
                                       ContObject contObject,
                                       LocalDateTime subscriberDateTime,
                                       ZonedDateTime currDateTime) {

        ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
        Optional<ContObjectAccess> checkExisting = Optional.ofNullable(contObjectAccessRepository.findOne(accessPK));
        ContObjectAccess access = checkExisting.orElse(new ContObjectAccess().subscriberId(subscriber.getId()).contObjectId(contObject.getId()));
        access.startNewAccess();
        contObjectAccessRepository.saveAndFlush(access);
        saveStartContObjectAccessHistory(subscriber, contObject, subscriberDateTime, currDateTime);

        if (checkExisting.isPresent()) {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already granted", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

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


        contZPointRepository.findContZPointIds(contObject.getId()).forEach(i -> startContZPointAccess(subscriber, new ContZPoint().id(i), subscriberDateTime));


    }


    private void finishContObjectAccess(Subscriber subscriber, ContObject contObject, LocalDateTime revokeDateTime, ZonedDateTime currDateTime) {
        ContObjectAccess.PK accessPK = new ContObjectAccess.PK().subscriberId(subscriber.getId()).contObjectId(contObject.getId());
        Optional<ContObjectAccess> checkExisting = Optional.ofNullable(contObjectAccessRepository.findOne(accessPK));

        final LocalDateTime revokeDT = revokeDateTime != null ? revokeDateTime : LocalDateTime.now();


        if (checkExisting.isPresent()) {
            ContObjectAccess access = checkExisting.get();

            access.setRevokeTZ(MAKE_REVOKE_TZ.apply(revokeDT));
            if (revokeDT.toLocalDate().isAfter(LocalDate.now()) || revokeDT.toLocalDate().isEqual(LocalDate.now())){
                access.setAccessTtl(MAKE_ACCESS_TTL.apply(revokeDT));
                access.setAccessTtlTz(ZonedDateTime.now().plus(ACCESS_TTL));
            }

            contObjectAccessRepository.save(access);
            saveFinishContObjectAccessHistory(subscriber, contObject, revokeDT, currDateTime);
        } else {
            log.warn("Access for {} (id={}) for Subscriber(id={}) already revoked", ContObject.class.getSimpleName(), contObject.getId(), subscriber.getId());
        }

        subscrContObjectService.access().unlinkSubscrContObject(subscriber, contObject,
            revokeDT.toLocalDate());

        contZPointRepository.findContZPointIds(contObject.getId()).forEach(i -> finishContZPointAccess(subscriber, new ContZPoint().id(i), revokeDateTime, currDateTime));

    }

    private void saveStartContObjectAccessHistory(Subscriber subscriber, ContObject contObject, LocalDateTime grantDateTime, ZonedDateTime currDateTime) {

        checkNotNull(currDateTime);

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

    private void saveStartContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime accessDateTime) {
        checkNotNull(accessDateTime);

        ContZPointAccessHistory history = new ContZPointAccessHistory();
        history.setContZPoint(contZPoint);
        history.setSubscriber(subscriber);
        history.setGrantDate(accessDateTime.toLocalDate());
        history.setGrantTime(accessDateTime.toLocalTime());
        history.setGrantTZ(accessDateTime.atZone(ZoneId.systemDefault()));
        contZPointAccessHistoryRepository.saveAndFlush(history);
    }

    private void saveFinishContZPointAccessHistory(Subscriber subscriber, ContZPoint contZPoint, LocalDateTime revokeDateTime) {
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


    @Transactional
    @Secured({ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_SUBSCR_CREATE_CABINET})
    public void updateContObjectIdsAccess(final Subscriber subscriber, final List<Long> newContObjectIds,
                                          final LocalDateTime subscriberDateTime) {

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


        delContObjectIds.forEach((id) -> finishContObjectAccess(subscriber, new ContObject().id(id), accessDateTime, currDateTime));

        addContObjectIds.forEach((id) -> startContObjectAccess(subscriber, new ContObject().id(id), accessDateTime, currDateTime));

    }

    @Scheduled(cron = "0 */2 * * * ?")
    @Transactional
    public void cleanupAccessByTtl() {
        cleanupContObjectAccess();
        cleanupContZPointAccess();
    }


    @Transactional
    public void cleanupContObjectAccess() {
        log.info("\nCONT_OBJECT END OF ACCESS");
        contObjectAccessRepository.findAllAccessTtl(LocalDateTime.now()).forEach( a -> {
                log.info("Subscriber {}, ContObjectId {}, AccessTTL: {}", a.getSubscriberId(), a.getContObjectId(), a.getAccessTtl());
                contObjectAccessRepository.delete(a);
            }
        );
    }

    @Transactional
    public void cleanupContZPointAccess() {
        log.info("\nCONT_ZPOINT END OF ACCESS");
        contZPointAccessRepository.findAllAccessTtl(LocalDateTime.now()).forEach( a -> {
                log.info("Subscriber {}, ContZPoint {}, AccessTTL: {}", a.getSubscriberId(), a.getContZPointId(), a.getAccessTtl());
                contZPointAccessRepository.delete(a);
            }
        );
    }

}
