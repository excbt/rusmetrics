package ru.excbt.datafuse.nmk.data.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrObjectTreeContObjectService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrObjectTreeContObjectService.class);

	@Autowired
	private SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager em;

    @Autowired
	protected ObjectAccessService objectAccessService;

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 */
	private void checkValidSubscriber(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {
		subscrObjectTreeService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	protected List<ContObject> selectTreeContObjects2(final SubscriberParam subscriberParam,
			final Long subscrObjectTreeId) {
		checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectTreeContObjects(final SubscriberParam subscriberParam,
			final Long subscrObjectTreeId) {
		checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		List<ContObject> result = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
		if (subscriberParam.isRma()) {
            objectAccessService.setupRmaHaveSubscr(subscriberParam,result);
		}

		return result.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).collect(Collectors.toList());

	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectTreeContObjectIds(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {
		checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjectIds(subscrObjectTreeId);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @param contObjectIds
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void saveTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		List<SubscrObjectTreeContObject> saveContObjects = new ArrayList<>();
		List<SubscrObjectTreeContObject> deleteContObjects = new ArrayList<>();

		for (SubscrObjectTreeContObject co : contObjects) {
			if (contObjectIds.contains(co.getContObjectId())) {
				saveContObjects.add(co);
			} else {
				deleteContObjects.add(co);
			}
		}

		List<Long> savedIds = saveContObjects.stream().map(i -> i.getContObjectId()).collect(Collectors.toList());

		for (Long id : contObjectIds) {
			if (!savedIds.contains(id)) {
				SubscrObjectTreeContObject co = new SubscrObjectTreeContObject();
				co.setSubscrObjectTreeId(subscrObjectTreeId);
				co.setContObjectId(id);
				saveContObjects.add(co);
			}
		}

		subscrObjectTreeContObjectRepository.save(saveContObjects);
		subscrObjectTreeContObjectRepository.delete(deleteContObjects);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @param contObjectIds
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void addTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		boolean isLinkDeny = subscrObjectTreeService.selectIsLinkDeny(subscrObjectTreeId);

		if (isLinkDeny) {
			throw new PersistenceException(
					String.format("Link ContObjects for subscrObjectTreeId(%d) is not allowed", subscrObjectTreeId));
		}

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		List<Long> currentContObjectsIds = contObjects.stream().map(i -> i.getContObjectId())
				.collect(Collectors.toList());

		List<SubscrObjectTreeContObject> saveContObjects = new ArrayList<>();

		for (Long id : contObjectIds) {
			if (!currentContObjectsIds.contains(id)) {
				SubscrObjectTreeContObject co = new SubscrObjectTreeContObject();
				co.setSubscrObjectTreeId(subscrObjectTreeId);
				co.setContObjectId(id);
				saveContObjects.add(co);
			}
		}

		subscrObjectTreeContObjectRepository.save(saveContObjects);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @param contObjectIds
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		List<SubscrObjectTreeContObject> deleteContObjects = new ArrayList<>();

		for (SubscrObjectTreeContObject co : contObjects) {
			if (contObjectIds.contains(co.getContObjectId())) {
				deleteContObjects.add(co);
			}
		}

		subscrObjectTreeContObjectRepository.delete(deleteContObjects);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTreeContObjectsAll(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		subscrObjectTreeContObjectRepository.delete(contObjects);
	}

	/**
	 *
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectTreeContObjectIdsAllLevels(final SubscriberParam subscriberParam,
			final Long subscrObjectTreeId) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		List<Long> resultList = new ArrayList<>();

		ColumnHelper columnHelper = new ColumnHelper("cont_object_id");
		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");

		String funcName = null;

		if (subscriberParam.isRma()) {
			funcName = " portal.get_subscr_cont_object_tree_cont_object_ids_rma(:p_subscriber_id,:p_subscr_object_tree_id); ";
		} else {
			funcName = " portal.get_subscr_cont_object_tree_cont_object_ids_subscr(:p_subscriber_id,:p_subscr_object_tree_id); ";
		}

		sqlString.append(funcName);

		Query q1 = em.createNativeQuery(sqlString.toString());
		q1.setParameter("p_subscriber_id", subscriberParam.getSubscriberId());
		q1.setParameter("p_subscr_object_tree_id", subscrObjectTreeId);

		List<?> queryResultList = q1.getResultList();

		for (Object object : queryResultList) {
			if (object instanceof BigInteger) {
				BigInteger value = (BigInteger) object;
				Long id = value.longValue();
				resultList.add(id);
			} else {
				throw new PersistenceException(String.format("Invalid return result for %s, subscrObjectTreeId = %d",
						funcName, subscrObjectTreeId));
			}
		}

		return resultList;
	}

}
