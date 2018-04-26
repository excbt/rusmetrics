package ru.excbt.datafuse.nmk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional(readOnly = true)
public class SubscriberTimeService {

    @PersistenceContext(unitName = "nmk-p")
    private EntityManager em;

    /**
     *
     * @param subscriberId
     * @return
     */
    public Date getSubscriberCurrentTime(Long subscriberId) {
        checkNotNull(subscriberId);
        Query q = em.createNativeQuery("SELECT get_subscriber_current_time(?1);");
        Object dbResult = q.setParameter(1, subscriberId).getSingleResult();
        if (dbResult == null) {
            return null;
        }
        return (Date) dbResult;
    }

    public Date getSubscriberCurrentTime(PortalUserIds portalUserIds) {
        return getSubscriberCurrentTime(portalUserIds.getSubscriberId());
    }

}
