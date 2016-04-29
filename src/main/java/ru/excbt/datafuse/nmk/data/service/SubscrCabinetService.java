package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.data.model.support.SubscrCabinetInfo;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService.LdapAction;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.PasswordUtils;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
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

	@Autowired
	private SubscrContObjectRepository subscrContObjectRepository;

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private LdapService ldapService;

	/*
	 * 
	 */
	private class SubscCabinetContObjectStats {
		private final Long contObjectId;
		private final Long count;

		public SubscCabinetContObjectStats(Long contObjectId, Long count) {
			this.contObjectId = contObjectId;
			this.count = count;
		}

		public Long getContObjectId() {
			return contObjectId;
		}

		public Long getCount() {
			return count;
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
		subscrUser.setUserNickname("Не задано");
		subscrUser.setUserComment(contObject.getFullName());
		subscrUser.setPassword(PasswordUtils.generateRandomPassword());
		subscrUser.setUserDescription(contObject.getFullName());

		subscrUserService.createSubscrUser(subscrUser, subscrUser.getPassword());

		if (!checkIfSubscriberCabinetsOK(parentSubscriber.getId())) {

			throw new PersistenceException(
					String.format("Can't create Child Subscriber for contObjects=%s", contObjectIds.toString()));

		}

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
	 * @param cabinetSubscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_ADMIN })
	public void deleteSubscrUserCabinet(Long cabinetSubscriberId) {
		Subscriber subscriber = subscriberService.findOne(cabinetSubscriberId);
		if (subscriber == null) {

			throw new PersistenceException(String.format("Subscriber (id=%d) is not found", cabinetSubscriberId));

		}
		deleteSubscrUserCabinet(subscriber);
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

	/**
	 * 
	 * @param parentSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<SubscCabinetContObjectStats> selectChildSubscrCabinetContObjectsStats(Long parentSubscriberId) {
		List<Object[]> qryResult = subscrContObjectRepository
				.selectChildSubscrCabinetContObjectsStats(parentSubscriberId);
		List<SubscCabinetContObjectStats> result = new ArrayList<>();

		for (Object[] row : qryResult) {

			if (!(row[0] instanceof Long) || !(row[1] instanceof Long)) {

				throw new PersistenceException(String.format(
						"Can't calculate SubscCabinetContObjectStats for Subscriber (id=%d)", parentSubscriberId));

			}
			SubscCabinetContObjectStats stats = new SubscCabinetContObjectStats((Long) row[0], (Long) row[1]);
			result.add(stats);

		}

		return result;
	}

	/**
	 * 
	 * @param parentSubscriberId
	 * @param contObjectIds
	 * @return
	 */
	public boolean checkIfSubscriberCabinetsOK(Long parentSubscriberId) {
		List<SubscCabinetContObjectStats> stats = selectChildSubscrCabinetContObjectsStats(parentSubscriberId);
		return !stats.stream().filter(i -> (i.getCount() != null) && (i.getCount() > 1)).findAny().isPresent();
	}

	/**
	 * 
	 * @param subscrUser
	 */
	private void checkCabinerSubscrUserValid(Long parentSubscriberId, Long subscrUserId, SubscrUser subscrUser) {

		checkNotNull(parentSubscriberId);

		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}

		if (subscrUser.getSubscriber() == null) {
			throw new PersistenceException(
					String.format("Subscriber of SubscrUser (id=%d) is not found", subscrUserId));
		}

		if (!SubscrTypeKey.CABINET.getKeyname().equals(subscrUser.getSubscriber().getSubscrType())) {
			throw new IllegalArgumentException("SubscrUser (id=%d) is not of type CABINET");
		}

		if (!parentSubscriberId.equals(subscrUser.getSubscriber().getParentSubscriberId())) {
			throw new AccessDeniedException(String.format("Access denied to SubscrUser (id=%d) ", subscrUserId));
		}

	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUserWrapper saveCabinelSubscrUser(Long parentSubscriberId, SubscrUserWrapper entity) {
		checkNotNull(entity);
		checkNotNull(entity.getSubscrUser());
		checkArgument(!entity.getSubscrUser().isNew());

		SubscrUser entitySubsrUser = entity.getSubscrUser();

		SubscrUser currentSubscrUser = subscrUserRepository.findOne(entitySubsrUser.getId());
		checkCabinerSubscrUserValid(parentSubscriberId, entitySubsrUser.getId(), currentSubscrUser);

		currentSubscrUser.setUserComment(entitySubsrUser.getUserComment());
		currentSubscrUser.setUserNickname(entitySubsrUser.getUserNickname());
		currentSubscrUser.setUserDescription(entitySubsrUser.getUserDescription());
		currentSubscrUser.setContactEmail(entitySubsrUser.getContactEmail());
		currentSubscrUser.setDevComment(entitySubsrUser.getDevComment());

		final String pass = entity.getPasswordPocket();

		if (pass != null) {
			currentSubscrUser.setPassword(null);
		}

		SubscrUser result = subscrUserRepository.save(currentSubscrUser);

		if (pass != null) {

			LdapAction action = (u) -> {
				//ldapService.changePassword(u, passwords[0], passwords[1]);
				ldapService.changePassword(u, pass);
			};

			subscrUserService.processLdapAction(result, action);

		}

		return new SubscrUserWrapper(result);
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrUser selectCabinelSubscrUser(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser currentSubscrUser = subscrUserRepository.findOne(subscrUserId);
		if (currentSubscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}

		if (!SubscrTypeKey.CABINET.getKeyname().equals(currentSubscrUser.getSubscriber().getSubscrType())) {
			throw new IllegalArgumentException("SubscrUser (id=%d) is not of type CABINET");
		}

		return currentSubscrUser;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUserWrapper saveCabinelSubscrUserPassword(Long parentSubscriberId, Long subscrUserId,
			String password) {
		checkNotNull(subscrUserId);
		checkNotNull(password);

		SubscrUser currentSubscrUser = subscrUserRepository.findOne(subscrUserId);

		checkCabinerSubscrUserValid(parentSubscriberId, subscrUserId, currentSubscrUser);

		currentSubscrUser.setPassword(password);

		SubscrUser result = subscrUserRepository.save(currentSubscrUser);

		LdapAction action = (u) -> {
			//ldapService.changePassword(u, passwords[0], passwords[1]);
			ldapService.changePassword(u, password);
		};

		subscrUserService.processLdapAction(result, action);

		return new SubscrUserWrapper(result);

	}

}
