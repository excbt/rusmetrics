package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrCabinetService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetService.class);

	@Autowired
	protected SubscrContObjectService subscrContObjectService;

	@Autowired
	protected SubscrUserService subscrUserService;

	@Autowired
	protected SubscriberService subscriberService;

	@Autowired
	protected SubscriberRepository subscriberRepository;

	@Autowired
	private ContObjectService contObjectService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Long getSubscrCabinetNr() {
		Query q = em.createNativeQuery("select nextval('portal.seq_subscr_cabinet_nr') as nr");

		Object qryResult = q.getSingleResult();

		Long result = null;

		if (qryResult instanceof BigInteger) {
			BigInteger nr = (BigInteger) qryResult;
			result = nr.longValue();
		} else {

			throw new PersistenceException(
					"result of select nextval('portal.seq_subscr_cabinet_nr') as nr is invalid type");

		}

		return result;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_ADMIN })
	public Subscriber createSubscrUserCabinet(Subscriber parentSubscriber, Long[] contObjectIds) {
		checkNotNull(parentSubscriber);
		checkNotNull(parentSubscriber.getId());
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length >= 1);

		if (!subscrContObjectService.canAccessContObjects(parentSubscriber.getId(), contObjectIds)) {
			throw new PersistenceException(String.format("Subscriber (id=%d) can't access contObjects (%s)",
					parentSubscriber.getId(), contObjectIds.toString()));
		}

		if (parentSubscriber.getChildLdapOu() == null) {
			throw new PersistenceException(
					String.format("Subscriber (id=%d) have not childLdapOu propterty", parentSubscriber.getId()));
		}

		ContObject contObject = contObjectService.findContObject(contObjectIds[0]);

		if (contObject == null) {
			throw new PersistenceException(String.format("ContObjectId (%d) is not found", contObjectIds[0]));
		}

		Long subscrCabinetNr = getSubscrCabinetNr();

		logger.trace("subscriberCabinetNr:{}", subscrCabinetNr);

		Subscriber newSubscriber = new Subscriber();
		newSubscriber.setSubscrCabinetNr(subscrCabinetNr != null ? subscrCabinetNr.toString() : null);
		newSubscriber.setParentSubscriberId(parentSubscriber.getId());
		newSubscriber.setSubscrType(SubscrTypeKey.CABINET.getKeyname());
		newSubscriber.setSubscriberName(contObject.getFullName());
		newSubscriber.setTimezoneDef(parentSubscriber.getTimezoneDef());
		newSubscriber.setIsChild(true);

		return subscriberRepository.save(newSubscriber);
	}

}
