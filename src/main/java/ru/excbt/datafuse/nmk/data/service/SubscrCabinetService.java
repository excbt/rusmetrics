package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.SubscrCabinetInfo;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.PasswordUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrCabinetService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetService.class);

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

	public class ContObjectCabinetInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6659437346714897566L;

		private final ContObjectShortInfo contObjectInfo;

		private final SubscrCabinetInfo subscrCabinetInfo;

		/**
		 * 
		 * @param contObject
		 * @param subscrCabinetInfo
		 */
		public ContObjectCabinetInfo(ContObject contObject, SubscrCabinetInfo subscrCabinetInfo) {
			this.contObjectInfo = contObject.getContObjectShortInfo();
			this.subscrCabinetInfo = subscrCabinetInfo;
		}

		/**
		 * 
		 * @return
		 */
		public ContObjectShortInfo getContObjectInfo() {
			return contObjectInfo;
		}

		/**
		 * 
		 * @return
		 */
		public SubscrCabinetInfo getSubscrCabinetInfo() {
			return subscrCabinetInfo;
		}

	}

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

	/**
	 * 
	 * @return
	 */
	public List<ContObjectCabinetInfo> selectSubscrContObjectCabinetInfoList(Long parentSubscriberId) {

		List<ContObject> contObjectList = ObjectFilters
				.deletedFilter(subscrContObjectService.selectSubscriberContObjects(parentSubscriberId));

		List<Subscriber> childSubscribers = subscriberService.selectChildSubscribers(parentSubscriberId);

		Map<Long, List<Long>> childContObjectIdMap = new HashMap<>();
		Map<Long, List<Long>> childContObjectSubscriberMap = new HashMap<>();
		Map<Long, List<ContObject>> childContObjecMap = new HashMap<>();

		for (Subscriber s : childSubscribers) {
			List<Long> childContObjectIds = subscrContObjectService.selectSubscriberContObjectIds(s.getId());
			List<ContObject> childContObjects = subscrContObjectService.selectSubscriberContObjects(s.getId());
			if (!childContObjectIds.isEmpty()) {
				childContObjectIdMap.put(s.getId(), childContObjectIds);
			}
			if (!childContObjects.isEmpty()) {
				childContObjecMap.put(s.getId(), childContObjects);
			}

			for (Long contObjectId : childContObjectIds) {
				if (childContObjectSubscriberMap.get(contObjectId) == null) {
					childContObjectSubscriberMap.put(contObjectId, new ArrayList<>());
				}
				childContObjectSubscriberMap.get(contObjectId).add(s.getId());
			}

		}

		List<ContObjectCabinetInfo> result = new ArrayList<>();

		for (ContObject contObject : contObjectList) {

			SubscrCabinetInfo subscrCabinetInfo = null;

			if (childContObjectSubscriberMap.get(contObject.getId()) != null
					&& !childContObjectSubscriberMap.get(contObject.getId()).isEmpty()) {

				Long childSubscriberId = childContObjectSubscriberMap.get(contObject.getId()).get(0);

				Optional<Subscriber> optChildSubscriber = childSubscribers.stream()
						.filter(i -> i.getId().equals(childSubscriberId)).findFirst();

				if (optChildSubscriber.isPresent()) {
					List<SubscrUser> childSubscrUsers = subscrUserService.selectBySubscriberId(childSubscriberId);
					if (!childSubscrUsers.isEmpty()) {
						subscrCabinetInfo = new SubscrCabinetInfo(optChildSubscriber.get(), childSubscrUsers.get(0),
								childContObjecMap.get(optChildSubscriber.get().getId()));
					}
				}

			}

			ContObjectCabinetInfo cabinetInfo = new ContObjectCabinetInfo(contObject, subscrCabinetInfo);
			result.add(cabinetInfo);

		}

		return result;
	}

}
