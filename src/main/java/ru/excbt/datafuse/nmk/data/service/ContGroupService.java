package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroupItem;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrContGroupItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContGroupRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с группами ContGroup объектов ContObject
 *
 * @author STATIC.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */

@Service
public class ContGroupService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetService.class);

	@Autowired
	private SubscrContGroupRepository contGroupRepository;

	@Autowired
	private SubscrContGroupItemRepository contGroupItemRepository;

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrContGroup createOne(SubscrContGroup entity, Long[] contObjectIds) {
		checkNotNull(entity);
		checkArgument(entity.isNew());

		SubscrContGroup result = contGroupRepository.save(entity);
		if (contObjectIds != null) {
			updateObjectsToGroup(result.getId(), contObjectIds);
		}

		return result;
	}

	/**
	 *
	 * @param entity
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(SubscrContGroup entity) {
		checkNotNull(entity);
		contGroupRepository.delete(entity);
	}

	/**
	 *
	 * @param SubscrContGroup
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrContGroup updateOne(SubscrContGroup entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());

		SubscrContGroup result = null;
		result = contGroupRepository.save(entity);
		return result;
	}

	/**
	 *
	 * @param contGroup
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrContGroup updateOne(SubscrContGroup contGroup, Long[] contObjectIds) {
		SubscrContGroup result = updateOne(contGroup);

		if (contObjectIds != null) {
			updateObjectsToGroup(result.getId(), contObjectIds);
		}
		return result;
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateObjectsToGroup(final Long contGroupId, final Long[] objectIds) {
		checkNotNull(contGroupId);
		checkNotNull(objectIds);

		List<Long> newObjectIdList = Arrays.asList(objectIds);

		List<Long> currentIds = contGroupItemRepository.selectObjectIds(contGroupId);
		for (Long currentId : currentIds) {
			if (!newObjectIdList.contains(currentId)) {
				logger.trace("removing objectId:{}", currentId);
				deleteObjectsFromGroup(contGroupId, currentId);
			}
		}

		for (Long newId : newObjectIdList) {
			if (!currentIds.contains(newId)) {
				addObjectToGroup(contGroupId, newId);
			}
		}
	}

	/**
	 *
	 * @param reportParamsetUnitId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteObjectsFromGroup(final Long contGroupId, final Long contObjectId) {
		checkNotNull(contGroupId);
		checkNotNull(contObjectId);

		List<Long> ids = contGroupItemRepository.selectItemIds(contGroupId, contObjectId);

		if (ids.size() > 1) {
			logger.trace("Can't delete ReportParamsetUnit. Too Many Rows. (contGroupId={}, contObjectId={})",
					contGroupId, contObjectId);
			throw new PersistenceException(
					String.format("Can't delete ReportParamsetUnit. Too Many Rows. (contGroupId=%d, contObjectId=%d)",
							contGroupId, contObjectId));
		}
		if (ids.size() == 0) {
			logger.trace("Can't delete ReportParamsetUnit. No Rows Found. (contGroupId={}, contObjectId={})",
					contGroupId, contObjectId);
			throw new PersistenceException(
					String.format("Can't delete ReportParamsetUnit. No Rows Found. (contGroupId=%d, contObjectId=%d)",
							contGroupId, contObjectId));
		}

		contGroupItemRepository.delete(ids.get(0));
	}

	/**
	 *
	 * @param reportParamsetId
	 * @param objectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContGroupObject(Long contGroupId, Long objectId) {
		checkNotNull(contGroupId);
		checkNotNull(objectId);
		return contGroupItemRepository.selectItemIds(contGroupId, objectId).size() > 0;
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContGroupItem addObjectToGroup(SubscrContGroup contGroup, Long objectId) {
		checkNotNull(contGroup);
		checkNotNull(objectId);
		checkArgument(!contGroup.isNew());

		if (checkContGroupObject(contGroup.getId(), objectId)) {
			throw new PersistenceException(String.format(
					"ContGroupItem error. A pair of ContGroup (id=%d) and Object (id=%d) is alredy exists",
					contGroup.getId(), objectId));
		}

		SubscrContGroupItem ci = new SubscrContGroupItem();
		ci.setContObjectId(objectId);
		ci.setContGroup(contGroup);
		SubscrContGroupItem result = contGroupItemRepository.save(ci);

		return result;
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContGroupItem addObjectToGroup(Long contGroupId, Long objectId) {
		checkNotNull(contGroupId);
		SubscrContGroup cg = findOne(contGroupId);
		checkNotNull(cg);
		return addObjectToGroup(cg, objectId);
	}

	/**
	 *
	 * @param id
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(Long contGroupId) {
		checkNotNull(contGroupId);
		if (contGroupRepository.exists(contGroupId)) {
			List<Long> ids = contGroupItemRepository.selectItemIds(contGroupId);
			for (Long id : ids) {
				contGroupItemRepository.delete(id);
			}
			contGroupRepository.delete(contGroupId);
		} else {
			throw new PersistenceException(String.format("Can't delete ContGroup(id=%d)", contGroupId));
		}

	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrContGroup findOne(Long contGroupId) {
		checkNotNull(contGroupId);
		SubscrContGroup result = contGroupRepository.findOne(contGroupId);
		return result;
	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectContGroupObjects(SubscriberParam subscriberParam, Long contGroupId) {
		checkNotNull(contGroupId);
		checkNotNull(subscriberParam);
		return contGroupItemRepository.selectContGroupObjects(subscriberParam.getSubscriberId(), contGroupId);
	}

	/**
	 *
	 * @param contGroupId
	 * @param SubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectAvailableContGroupObjects(SubscriberParam subscriberParam, Long contGroupId) {
		checkNotNull(contGroupId);
		checkNotNull(subscriberParam);

		return contGroupItemRepository.selectAvailableContGroupObjects(subscriberParam.getSubscriberId(), contGroupId);
	}

	/**
	 *
	 * @param SubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContGroup> selectSubscriberGroups(SubscriberParam subscriberParam) {
		checkNotNull(subscriberParam);
		return contGroupRepository.findBySubscriberId(subscriberParam.getSubscriberId());
	}

}
