package ru.excbt.datafuse.nmk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;


@Service
public class SubscrObjectTreeValidationService {


    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    public SubscrObjectTreeValidationService(SubscrObjectTreeRepository subscrObjectTreeRepository) {
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
    }

//    /**
//     *
//     * @param subscriberParam
//     * @param subscrObjectTreeId
//     */
//    @Transactional( readOnly = true)
//    public void checkValidSubscriber(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {
//
//        if (!checkValidSubscriberOk(subscriberParam, subscrObjectTreeId)) {
//            throw new PersistenceException(
//                String.format("SubscrObjectTree (id=%d) is not valid for subscriber", subscrObjectTreeId));
//        }
//
//    }
    @Deprecated
    @Transactional( readOnly = true)
    public void checkValidSubscriber(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {
        Objects.requireNonNull(portalUserIds);
        Objects.requireNonNull(subscrObjectTreeId);

//        if (!checkValidSubscriberOk(portalUserIds, subscrObjectTreeId)) {
//            throw new PersistenceException(
//                String.format("SubscrObjectTree (id=%d) is not valid for subscriber", subscrObjectTreeId));
//        }
    }


    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
    @Deprecated
    @Transactional( readOnly = true)
    public boolean checkValidSubscriberOk(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {
        Objects.requireNonNull(portalUserIds);
        Objects.requireNonNull(subscrObjectTreeId);

//        Long checkTreeSubscriberId = portalUserIds.isRma() ? selectRmaSubscriberId(subscrObjectTreeId)
//            : selectSubscriberId(subscrObjectTreeId);
//
//        return Long.valueOf(portalUserIds.getSubscriberId()).equals(checkTreeSubscriberId);
        return true;

    }

    @Transactional( readOnly = true)
    public boolean checkValidSubscriberOk_new(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {
        Objects.requireNonNull(portalUserIds);
        Objects.requireNonNull(subscrObjectTreeId);

        Long checkTreeSubscriberId = selectSubscriberId(subscrObjectTreeId);

        return Long.valueOf(portalUserIds.getSubscriberId()).equals(checkTreeSubscriberId);

    }


    private Long selectRmaSubscriberId(final Long subscrObjectTreeId) {
        List<Long> ids = subscrObjectTreeRepository.selectRmaSubscriberIds(subscrObjectTreeId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private Long selectSubscriberId(final Long subscrObjectTreeId) {
        List<Long> ids = subscrObjectTreeRepository.selectSubscriberIds(subscrObjectTreeId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    /**
     *
     * @param subscrObjectTreeId
     * @return
     */
    @Transactional( readOnly = true)
    public boolean selectIsLinkDeny(final Long subscrObjectTreeId) {
        List<Boolean> ids = subscrObjectTreeRepository.selectIsLinkDeny(subscrObjectTreeId);
        return !ids.isEmpty() && Boolean.TRUE.equals(ids.get(0));
    }

}
