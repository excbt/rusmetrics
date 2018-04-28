package ru.excbt.datafuse.nmk.service;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.data.service.SystemParamService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.utils.DBEntityUtil;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscriberManageService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberManageService.class);

    private final SubscriberRepository subscriberRepository;
    private final SubscriberTimeService subscriberTimeService;
    private final SubscrServiceAccessService subscrServiceAccessService;
    private final SystemParamService systemParamService;
    private final SubscriberLdapService subscriberLdapService;
    private final ReportParamsetService reportParamsetService;

    public SubscriberManageService(SubscriberRepository subscriberRepository, SubscriberAccessService subscriberAccessService, SubscriberTimeService subscriberTimeService, SubscrServiceAccessService subscrServiceAccessService, SystemParamService systemParamService, SubscriberLdapService subscriberLdapService, ReportParamsetService reportParamsetService) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberTimeService = subscriberTimeService;
        this.subscrServiceAccessService = subscrServiceAccessService;
        this.systemParamService = systemParamService;
        this.subscriberLdapService = subscriberLdapService;
        this.reportParamsetService = reportParamsetService;
    }


    /**
     *
     * @param subscriber
     * @param rmaSubscriberId
     * @return
     */
    @Transactional
    @Secured({AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public Subscriber createRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
        checkNotNull(subscriber);
        checkNotNull(rmaSubscriberId);
        checkArgument(subscriber.isNew());
        subscriber.setRmaSubscriberId(rmaSubscriberId);
        checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));
        checkArgument(subscriber.getDeleted() == 0);

        subscriber.setSubscrType(SubscrTypeKey.NORMAL.getKeyname());

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





    /**
     *
     * @param subscriberId
     * @param rmaSubscriberId
     */
    @Transactional
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    public void deleteRmaSubscriberPermanent(Long subscriberId, Long rmaSubscriberId) {
        checkNotNull(subscriberId);
        checkNotNull(rmaSubscriberId);


        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        DBEntityUtil.requireNotNull(subscriber, subscriberId, Subscriber.class);

        if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
            throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
        }
        subscrServiceAccessService.deleteSubscriberAccess(subscriberId);
        subscriberRepository.delete(subscriber);
    }




}
