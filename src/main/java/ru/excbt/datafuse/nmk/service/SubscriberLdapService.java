package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.SystemParamService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.service.utils.DBEntityUtil;

import java.util.Objects;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscriberLdapService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberLdapService.class);

    private final static String LDAP_DESCRIPTION_SUFFIX_PARAM = "LDAP_CABINETS_DESCRIPTION_SUFFIX";
    private final static String LDAP_DESCRIPTION_SUFFIX_DEFAULT = "Cabinets-";

    private final SubscriberRepository subscriberRepository;
    private final SystemParamService systemParamService;
    private final LdapService ldapService;

    public SubscriberLdapService(SubscriberRepository subscriberRepository, SystemParamService systemParamService, LdapService ldapService) {
        this.subscriberRepository = subscriberRepository;
        this.systemParamService = systemParamService;
        this.ldapService = ldapService;
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    public static String buildCabinetsOuName(Long subscriberId) {
        Objects.requireNonNull(subscriberId);
        return "Cabinets-" + subscriberId;
    }


    /**
     *
     * @param subscriber
     * @return
     */
    public String[] buildSubscriberLdapOu(Subscriber subscriber, Function<Long, String> ldapOuProvider) {
        checkNotNull(subscriber);

        String rmaOu = null;
        String childLdapOu = null;
        String[] orgUnits = null;

        if (Boolean.TRUE.equals(subscriber.getIsChild())) {
            rmaOu = ldapOuProvider.apply(subscriber.getParentSubscriberId());
            Subscriber parentSubscriber = subscriberRepository.findOne(subscriber.getParentSubscriberId());
            DBEntityUtil.requireNotNull(parentSubscriber, subscriber.getParentSubscriberId(), Subscriber.class);
            childLdapOu = parentSubscriber.getChildLdapOu();

            orgUnits = new String[] { rmaOu, childLdapOu };

        } else {
            rmaOu = ldapOuProvider.apply(subscriber.getId());
            orgUnits = new String[] { rmaOu };
        }

        checkNotNull(orgUnits);

        return orgUnits;
    }

    public String[] buildSubscriberLdapOu(Subscriber subscriber) {
        return buildSubscriberLdapOu(subscriber, this::getRmaLdapOu);
    }



    /**
     *
     * @param subscriberId
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public String getRmaLdapOu(Long subscriberId) {
        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            return null;
        }
        if (Boolean.TRUE.equals(subscriber.getIsRma())) {
            return subscriber.getRmaLdapOu();
        }

        if (subscriber.getRmaLdapOu() != null) {
            return subscriber.getRmaLdapOu();
        }

        if (subscriber.getRmaSubscriberId() == null) {
            return null;
        }

        Subscriber rmaSubscriber = subscriberRepository.findOne(subscriber.getRmaSubscriberId());
        return rmaSubscriber == null ? null : rmaSubscriber.getRmaLdapOu();
    }


    /**
     *
     * @param subscriber
     * @return
     */
    public String buildChildDescription(Subscriber subscriber) {
        checkNotNull(subscriber);
        checkNotNull(subscriber.getSubscriberName());
        String suffix = null;
        try {
            suffix = systemParamService.getParamValueAsString(LDAP_DESCRIPTION_SUFFIX_PARAM);
        } catch (Exception e) {
            log.error("System param {} not found", LDAP_DESCRIPTION_SUFFIX_PARAM);
        }

        if (suffix == null || suffix.isEmpty()) {
            log.error("System param {} is empty use default: {}", LDAP_DESCRIPTION_SUFFIX_PARAM,
                LDAP_DESCRIPTION_SUFFIX_DEFAULT);
            suffix = LDAP_DESCRIPTION_SUFFIX_DEFAULT;
        }

        return suffix + subscriber.getSubscriberName();
    }

    public void createOuIfNotExists(String[] subscriberOurgUnits, String ouName, String description) {
        ldapService.createOuIfNotExists(subscriberOurgUnits, ouName, description);
    }

}
