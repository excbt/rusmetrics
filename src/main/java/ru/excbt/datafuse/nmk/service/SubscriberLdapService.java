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
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscriberLdapService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberLdapService.class);

    // private final static String LDAP_DESCRIPTION_SUFFIX_PARAM = "LDAP_CABINETS_DESCRIPTION_SUFFIX";
    private final static String LDAP_DESCRIPTION_PREFIX_PARAM = "LDAP_CABINETS_DESCRIPTION_PREFIX";
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

    public static String buildCabinetsOuName(Subscriber subscriber) {
        String suffix = Optional.ofNullable(subscriber)
            .flatMap(s -> Optional.ofNullable(s.getId())).map(Object::toString).orElse("Unknown-Subscriber");
        return "Cabinets-" + suffix;
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

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public Optional<String> findRmaLdapOu(Long subscriberId) {
        Optional<Subscriber> subscriber =  Optional.ofNullable(subscriberId).map(subscriberRepository::findOne);

        Optional<String> currOU = subscriber.filter(s -> Boolean.TRUE.equals(s.getIsRma()))
            .map(Subscriber::getRmaLdapOu);

        if (currOU.isPresent()) {
            return currOU;
        } else {
            return subscriber.filter(s -> s.getRmaSubscriberId() != null).flatMap(s -> findRmaLdapOu(s.getRmaSubscriberId()));
        }
    }


    /**
     *
     * @param subscriber
     * @return
     */
    public String buildChildDescription(Subscriber subscriber) {
        String prefix = systemParamService.findOptParamValueAsString(LDAP_DESCRIPTION_PREFIX_PARAM)
            .filter(s -> !s.isEmpty())
            .orElse(LDAP_DESCRIPTION_SUFFIX_DEFAULT);
        return prefix + subscriber.getSubscriberName();
    }

    public String buildLdapDescription() {
        return systemParamService.findOptParamValueAsString(LDAP_DESCRIPTION_PREFIX_PARAM)
            .filter(s -> !s.isEmpty())
            .orElse(LDAP_DESCRIPTION_SUFFIX_DEFAULT);
    }

    public boolean createOuIfNotExists(String[] subscriberOurgUnits, String ouName, String description) {
        for (String s: subscriberOurgUnits) {
            if (s == null || s.isEmpty()) {
                return false;
            }
        }
        if (ouName == null || ouName.isEmpty()) {
            return false;
        }
        ldapService.createOuIfNotExists(subscriberOurgUnits, ouName, description);
        return true;
    }

    public String[] buildLdapOu(Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        String rmaOu = null;
        String childLdapOu = null;
        String[] orgUnits = null;

        if (Boolean.TRUE.equals(subscriber.getIsChild())) {
            rmaOu = Optional.ofNullable(subscriber.getParentSubscriberId()).flatMap(this::findRmaLdapOu).orElse("");
            childLdapOu = Optional.ofNullable(subscriber.getParentSubscriberId())
                .map(subscriberRepository::findOne).map(Subscriber::getChildLdapOu).orElse("");
            orgUnits = new String[] { rmaOu, childLdapOu };
        } else {
            rmaOu = Optional.of(subscriber.getId()).flatMap(this::findRmaLdapOu).orElse("");
            orgUnits = new String[] { rmaOu };
        }

        checkNotNull(orgUnits);

        return orgUnits;
    }

    public String[] buildBaseLdapOu(Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        String rmaOu = Optional.of(subscriber.getId()).flatMap(this::findRmaLdapOu).orElse("");
        String[] orgUnits = new String[] { rmaOu };
        return orgUnits;
    }


}
