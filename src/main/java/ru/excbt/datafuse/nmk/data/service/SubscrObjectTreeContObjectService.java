package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

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
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.support.ColumnHelper;
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
	protected SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 */
	private void checkValidRma(final Long rmaSubscriberId, final Long subscrObjectTreeId) {
		checkNotNull(rmaSubscriberId);
		checkNotNull(subscrObjectTreeId);
		if (!rmaSubscriberId.equals(subscrObjectTreeService.selectRmaSubscriberId(subscrObjectTreeId))) {
			throw new PersistenceException(
					String.format("SubscrObjectTree (id=%d) is not valid for rma", subscrObjectTreeId));
		}
		;

	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectTreeContObjects(final Long rmaSubscriberId, final Long subscrObjectTreeId) {
		checkValidRma(rmaSubscriberId, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectRmaTreeContObjects(final Long rmaSubscriberId, final Long subscrObjectTreeId) {
		checkValidRma(rmaSubscriberId, subscrObjectTreeId);
		List<ContObject> result = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
		subscrContObjectService.processRmaContObjectsHaveSubscr(rmaSubscriberId, result);

		return result;

	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectTreeContObjectIds(final Long rmaSubscriberId, final Long subscrObjectTreeId) {
		checkValidRma(rmaSubscriberId, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjectIds(subscrObjectTreeId);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrObjectTreeId
	 * @param contObjectIds
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void saveTreeContObjects(final Long rmaSubscriberId, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidRma(rmaSubscriberId, subscrObjectTreeId);

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
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void addTreeContObjects(final Long rmaSubscriberId, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidRma(rmaSubscriberId, subscrObjectTreeId);

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
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTreeContObjects(final Long rmaSubscriberId, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

		checkValidRma(rmaSubscriberId, subscrObjectTreeId);

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
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTreeContObjectsAll(final Long rmaSubscriberId, final Long subscrObjectTreeId) {

		checkValidRma(rmaSubscriberId, subscrObjectTreeId);

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
	public List<Long> selectRmaTreeContObjectIdAllLevels(final Long rmaSubscriberId, final Long subscrObjectTreeId) {

		checkValidRma(rmaSubscriberId, subscrObjectTreeId);

		List<Long> resultList = new ArrayList<>();

		ColumnHelper columnHelper = ColumnHelper.newInstance(new String[] { "cont_object_id" });

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");
		sqlString.append(
				" portal.get_subscr_cont_object_tree_cont_object_ids(:p_rma_subscriber_id,:p_subscr_object_tree_id); ");

		Query q1 = em.createNativeQuery(sqlString.toString());
		q1.setParameter("p_rma_subscriber_id", rmaSubscriberId);
		q1.setParameter("p_subscr_object_tree_id", subscrObjectTreeId);
		List<?> queryResultList = q1.getResultList();

		for (Object object : queryResultList) {
			if (object instanceof BigInteger) {
				BigInteger value = (BigInteger) object;
				Long id = value.longValue();
				resultList.add(id);
			} else {
				throw new PersistenceException(String.format(
						"Invalid return result for portal.get_subscr_cont_object_tree_cont_object_ids(%d)",
						subscrObjectTreeId));
			}
		}

		return resultList;
	}

}
