package ru.excbt.datafuse.nmk.service;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.data.service.SystemParamService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.utils.DBEntityUtil;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
//import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscriberManageService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberManageService.class);

    private static final TimeBasedGenerator subscriberUUIDGen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

    private final SubscriberRepository subscriberRepository;
    private final SubscriberTimeService subscriberTimeService;
    private final SubscrServiceAccessService subscrServiceAccessService;
    private final SystemParamService systemParamService;
    private final SubscriberLdapService subscriberLdapService;
    private final ReportParamsetService reportParamsetService;
    private final SubscrUserManageService subscrUserManageService;
    private final SubscriberMapper subscriberMapper;

    public SubscriberManageService(SubscriberRepository subscriberRepository, SubscriberAccessService subscriberAccessService, SubscriberTimeService subscriberTimeService, SubscrServiceAccessService subscrServiceAccessService, SystemParamService systemParamService, SubscriberLdapService subscriberLdapService, ReportParamsetService reportParamsetService, SubscrUserManageService subscrUserManageService, SubscriberMapper subscriberMapper) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberTimeService = subscriberTimeService;
        this.subscrServiceAccessService = subscrServiceAccessService;
        this.systemParamService = systemParamService;
        this.subscriberLdapService = subscriberLdapService;
        this.reportParamsetService = reportParamsetService;
        this.subscrUserManageService = subscrUserManageService;
        this.subscriberMapper = subscriberMapper;
    }


    /**
     *
     * @param subscriber
     * @param rmaSubscriberId
     * @return
     */
    @Transactional
    @Secured({AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Subscriber createRmaSubscriberOld(Subscriber subscriber, Long rmaSubscriberId) {
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(rmaSubscriberId);
        checkArgument(subscriber.isNew());
        subscriber.setRmaSubscriberId(rmaSubscriberId);
        checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));
        checkArgument(subscriber.getDeleted() == 0);

        subscriber.setSubscrType(SubscrTypeKey.NORMAL.getKeyname());
        subscriber.setSubscriberUUID(subscriberUUIDGen.generate());

        Subscriber resultSubscriber = subscriberRepository.save(subscriber);

        // Can Create Child LDAP ou set
        if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
            Subscriber s = subscriberRepository.findOne(subscriber.getId());

            DBEntityUtil.requireNotNull(s, subscriber.getId(), Subscriber.class);

            if (s.getChildLdapOu() == null || s.getChildLdapOu().isEmpty()) {
                subscriber.setChildLdapOu(SubscriberLdapService.buildCabinetsOuName(subscriber.getId()));
            } else {
                subscriber.setChildLdapOu(s.getChildLdapOu());
            }
        }
        // End of can Create Child LDAP

        // Can Create Child LDAP action
        if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
            String[] ldapOu = subscriberLdapService.buildSubscriberLdapOu(subscriber);
            String childDescription = subscriberLdapService.buildChildDescription(subscriber);
            subscriberLdapService.createOuIfNotExists(ldapOu, subscriber.getChildLdapOu(), childDescription);
        }
        // End of can Create Child LDAP action

        java.time.LocalDate accessDate = LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(resultSubscriber.getId()));
        subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

        // Make default Report Paramset
        reportParamsetService.createDefaultReportParamsets(resultSubscriber);

        return resultSubscriber;
    }

    private String processLdapOus(Subscriber subscriber) {

        String rmaOu = Optional.of(subscriber).map(Subscriber::getRmaLdapOu).orElse("");
        subscriberLdapService.createOuIfNotExists(new String[0], rmaOu, "Subscriber");

        String childLdapOu = null;
        if (Boolean.TRUE.equals(subscriber.getCanCreateChild())) {
            childLdapOu = SubscriberLdapService.buildCabinetsOuName(subscriber);

            String[] ldapOuUnits = subscriberLdapService.buildLdapOu(subscriber);
            String ldapDescription = subscriberLdapService.buildLdapDescription() + subscriber.getSubscriberName();
            log.debug("Creating subscriber:\n id:{}\n subscriberName:{}\n ldapOuUnits:{}\n ldapDescription:{} \n",
                subscriber.getId(), subscriber.getSubscriberName(), ldapOuUnits, ldapDescription);

            boolean ldapResult = subscriberLdapService.createOuIfNotExists(ldapOuUnits, childLdapOu, ldapDescription);
            if (!ldapResult) {
                log.error("Ldap is not initialized");
            }
        }
        return childLdapOu;
    }

    @Transactional
    @Secured({AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Optional<Subscriber> createNormalSubscriber(SubscriberVM subscriberVM, PortalUserIds portalUserIds) {
        Objects.requireNonNull(subscriberVM);
        Objects.requireNonNull(portalUserIds);
        if (subscriberVM.getId() != null || !portalUserIds.isRma()) {
            return Optional.empty();
        }

        Subscriber resultSubscriber;
        {
            Subscriber newSubscr = subscriberMapper.toEntity(subscriberVM);
            newSubscr.setRmaSubscriberId(portalUserIds.getSubscriberId());
            newSubscr.setSubscriberUUID(subscriberUUIDGen.generate());
            newSubscr.setIsRma(false);
            newSubscr.setSubscrType(SubscrTypeKey.NORMAL.getKeyname());
            resultSubscriber = subscriberRepository.saveAndFlush(newSubscr);
        }

        String childLdapOu = processLdapOus(resultSubscriber);
        resultSubscriber.setChildLdapOu(childLdapOu);

        log.debug("Processing accessDate");
        java.time.LocalDate accessDate = LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(resultSubscriber.getId()));
        subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

        // Make default Report Paramset
        log.debug("Make Default Report Paramset");
        reportParamsetService.createDefaultReportParamsets(resultSubscriber);

        return Optional.of(subscriberRepository.save(resultSubscriber));
    }

    @Transactional
    @Secured({AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Optional<Subscriber> createRmaSubscriber(SubscriberVM subscriberVM, PortalUserIds portalUserIds) {
        Objects.requireNonNull(subscriberVM);
        Objects.requireNonNull(portalUserIds);
        if (subscriberVM.getId() != null || !portalUserIds.isRma()) {
            return Optional.empty();
        }

        Subscriber resultSubscriber;
        {
            Subscriber newSubscr = subscriberMapper.toEntity(subscriberVM);
            newSubscr.setSubscriberUUID(subscriberUUIDGen.generate());
            newSubscr.setIsRma(true);
            newSubscr.setSubscrType(SubscrTypeKey.RMA.getKeyname());
            resultSubscriber = subscriberRepository.saveAndFlush(newSubscr);
        }

        String childLdapOu = processLdapOus(resultSubscriber);
        resultSubscriber.setChildLdapOu(childLdapOu);

        log.debug("Processing accessDate");
        java.time.LocalDate accessDate = LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(resultSubscriber.getId()));
        subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

        // Make default Report Paramset
        log.debug("Make Default Report Paramset");
        reportParamsetService.createDefaultReportParamsets(resultSubscriber);

        return Optional.of(subscriberRepository.save(resultSubscriber));
    }


    /**
     *
     * @param subscriberVM
     * @return
     */
    @Transactional
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Optional<Subscriber> updateNormalSubscriber(SubscriberVM subscriberVM, PortalUserIds portalUserIds) {
        Objects.requireNonNull(subscriberVM);

        checkArgument(subscriberVM.getId() != null);


        Subscriber existingSubscriber = subscriberRepository.findOne(subscriberVM.getId());

        if (existingSubscriber == null) {
            return Optional.empty();
        }

        if (!portalUserIds.getSubscriberId().equals(existingSubscriber.getRmaSubscriberId())) {
            throw DBExceptionUtil.newAccessDeniedException(Subscriber.class, subscriberVM.getId());
        }

        String childLdapOu = processLdapOus(existingSubscriber);
        existingSubscriber.setChildLdapOu(childLdapOu);

        subscriberMapper.updateSubscriber(existingSubscriber, subscriberVM);
        Subscriber updatedSubscriber = subscriberRepository.save(existingSubscriber);

        // Make default Report Paramset
        reportParamsetService.createDefaultReportParamsets(updatedSubscriber);
        subscrUserManageService.setupSubscriberAdminUserRoles(updatedSubscriber);

        return Optional.ofNullable(updatedSubscriber);
    }


    /**
     *
     * @param subscriberVM
     * @return
     */
    @Transactional
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Optional<Subscriber> updateRmaSubscriber(SubscriberVM subscriberVM, PortalUserIds portalUserIds) {
        Objects.requireNonNull(subscriberVM);

        checkArgument(subscriberVM.getId() != null);


        Subscriber existingSubscriber = subscriberRepository.findOne(subscriberVM.getId());

        if (existingSubscriber == null) {
            return Optional.empty();
        }

        if (!Boolean.TRUE.equals(existingSubscriber.getIsRma())) {
            throw DBExceptionUtil.newAccessDeniedException(Subscriber.class, subscriberVM.getId());
        }

        String childLdapOu = processLdapOus(existingSubscriber);
        existingSubscriber.setChildLdapOu(childLdapOu);

        subscriberMapper.updateSubscriber(existingSubscriber, subscriberVM);
        Subscriber updatedSubscriber = subscriberRepository.save(existingSubscriber);

        // Make default Report Paramset
        reportParamsetService.createDefaultReportParamsets(updatedSubscriber);
        subscrUserManageService.setupSubscriberAdminUserRoles(updatedSubscriber);

        return Optional.ofNullable(updatedSubscriber);
    }


    /**
     *
     * @param subscriberId
     * @param rmaSubscriberId
     */
    @Transactional
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public void deleteRmaSubscriberPermanent(Long subscriberId, Long rmaSubscriberId) {
        Objects.requireNonNull(subscriberId);
        Objects.requireNonNull(rmaSubscriberId);


        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        DBEntityUtil.requireNotNull(subscriber, subscriberId, Subscriber.class);

        if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
            throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
        }
        subscrServiceAccessService.deleteSubscriberAccess(subscriberId);
        subscriberRepository.delete(subscriber);
    }


    /**
     *
     * @param subscriber
     * @param rmaSubscriberId
     * @return
     */
    @Transactional
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Subscriber updateRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(rmaSubscriberId);
        checkArgument(!subscriber.isNew());
        subscriber.setRmaSubscriberId(rmaSubscriberId);
        checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));

        Subscriber checkSubscriber = subscriberRepository.findOne(subscriber.getId());
        if (checkSubscriber == null || checkSubscriber.getDeleted() == 1) {
            throw new PersistenceException(
                String.format("Subscriber (id=%d) is not found or deleted", subscriber.getId()));
        }

        // Can Create Child LDAP ou set
        if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
            Subscriber s = subscriberRepository.findOne(subscriber.getId());
            if (s.getChildLdapOu() == null || s.getChildLdapOu().isEmpty()) {
                subscriber.setChildLdapOu(subscriberLdapService.buildCabinetsOuName(subscriber.getId()));
            } else {
                subscriber.setChildLdapOu(s.getChildLdapOu());
            }
        }
        // End of can Create Child LDAP

        Subscriber resultSubscriber = subscriberRepository.save(subscriber);

        // Can Create Child LDAP action
        if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
            String[] ldapOu = subscriberLdapService.buildSubscriberLdapOu(subscriber);
            String childDescription = subscriberLdapService.buildChildDescription(subscriber);
            subscriberLdapService.createOuIfNotExists(ldapOu, subscriber.getChildLdapOu(), childDescription);
        }
        // End of can Create Child LDAP action

        // Make default Report Paramset
        reportParamsetService.createDefaultReportParamsets(resultSubscriber);
        subscrUserManageService.setupSubscriberAdminUserRoles(resultSubscriber);

        return resultSubscriber;
    }


    /**
     * TODO Check arguments
     * @param subscriberId
     * @param rmaSubscriberId
     */
    @Transactional(value = TxConst.TX_DEFAULT)
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public void deleteRmaSubscriber(Long subscriberId, Long rmaSubscriberId) {
        Objects.requireNonNull(subscriberId);
        Objects.requireNonNull(rmaSubscriberId);

        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
            throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
        }
        subscriberRepository.save(EntityActions.softDelete(subscriber));
    }



}
