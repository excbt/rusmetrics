package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.PasswordUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrCabinetService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetService.class);

	public static class SubscrCabinetInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2991125903866112134L;

		private final Subscriber subscriber;
		private final SubscrUser subscrUser;

		private final List<ContObject> contObjects;

		private SubscrCabinetInfo(Subscriber subscriber, SubscrUser subscrUser, List<ContObject> contObjects) {
			this.subscriber = subscriber;
			this.subscrUser = subscrUser;
			this.contObjects = Collections.unmodifiableList(contObjects);
		}

		public Subscriber getSubscriber() {
			return subscriber;
		}

		public SubscrUser getSubscrUser() {
			return subscrUser;
		}

		public List<ContObject> getContObjects() {
			return contObjects;
		}

	}

	@Autowired
	protected SubscrContObjectService subscrContObjectService;

	@Autowired
	protected SubscrUserService subscrUserService;

	@Autowired
	protected SubscrRoleService subscrRoleService;

	@Autowired
	protected SubscriberService subscriberService;

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
	public SubscrCabinetInfo createSubscrUserCabinet(Subscriber parentSubscriber, Long[] contObjectIds) {
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

		newSubscriber = subscriberService.saveSubscriber(newSubscriber);

		List<ContObject> contObjects = subscrContObjectService.updateSubscrContObjects(newSubscriber.getId(),
				Arrays.asList(contObjectIds), LocalDate.now());

		SubscrUser subscrUser = new SubscrUser();
		subscrUser.setSubscriber(newSubscriber);
		subscrUser.setSubscriberId(newSubscriber.getId());
		subscrUser.setSubscrRoles(subscrRoleService.subscrCabinetRoles());
		subscrUser.setUserName(subscrCabinetNr.toString());
		subscrUser.setFirstName("Не задано");
		subscrUser.setLastName("Не задано");
		subscrUser.setUserComment(contObject.getFullName());
		subscrUser.setPassword(PasswordUtils.generateRandomPassword());
		subscrUser.setUserDescription(contObject.getFullName());

		subscrUserService.createSubscrUser(subscrUser, subscrUser.getPassword());

		SubscrCabinetInfo result = new SubscrCabinetInfo(newSubscriber, subscrUser, contObjects);

		return result;
	}

	/**
	 * 
	 * @param cabinetSubscriber
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_ADMIN })
	public void deleteSubscrUserCabinet(Subscriber cabinetSubscriber) {
		checkNotNull(cabinetSubscriber);
		checkArgument(!cabinetSubscriber.isNew());

		if (!SubscrTypeKey.CABINET.getKeyname().equals(cabinetSubscriber.getSubscrType())) {
			throw new PersistenceException(String.format("Subscriber (id=%d) is not type of CABINET. Actual type: %s",
					cabinetSubscriber.getId(), cabinetSubscriber.getSubscrType()));
		}

		subscrUserService.deleteSubscrUsers(cabinetSubscriber.getId());

		List<SubscrContObject> subscrContObjects = subscrContObjectService
				.selectSubscrContObjects(cabinetSubscriber.getId());

		subscrContObjectService.deleteSubscrContObjectPermanent(subscrContObjects);

		subscriberService.deleteSubscriber(cabinetSubscriber);

	}

}
