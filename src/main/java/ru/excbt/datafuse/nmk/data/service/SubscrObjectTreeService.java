package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrObjectTreeService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrObjectTreeService.class);

	@Autowired
	private SubscrObjectTreeRepository subscrObjectTreeRepository;

	@Autowired
	private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

	@Autowired
	private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

	/**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since dd.02.2016
	 *
	 */
	private interface CheckConditionOperator {
		public boolean checkCondition(SubscrObjectTree node);
	}

	/**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since dd.02.2016
	 *
	 */
	private interface TreeNodeOperator {
		public void doOperation(SubscrObjectTree node);

		public static enum TYPE {
			PRE, POST
		}

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrObjectTree selectSubscrObjectTree(Long id) {
		return subscrObjectTreeRepository.findOne(id);
	}

	/**
	 *
	 * @param node
	 * @param objectTreeType
	 * @return
	 */
	private SubscrObjectTree _initSubscrObjectTree(SubscrObjectTree node, ObjectTreeTypeKeyname objectTreeType,
			int level, List<SubscrObjectTreeTemplateItem> templateItems) {
		checkNotNull(node);
		checkNotNull(objectTreeType);
		node.setObjectTreeType(objectTreeType.getKeyname());
		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
			node.getChildObjectList().forEach(i -> {
				// init Props
				i.setParent(node);
				i.setParentId(node.getId());
				i.setTemplateId(node.getTemplateId());
				i.setRmaSubscriberId(node.getRmaSubscriberId());
				i.setSubscriberId(node.getSubscriberId());
				i.setIsRma(node.getIsRma());
				i.setDeleted(node.getDeleted());

				// Check props

				if (i.getTemplateId() != null && i.getTemplateItemId() == null) {
					throw new ModelIsNotValidException(String.format(
							"SubscrObjectTree is not valid. SubscrObjectTreeTemplate (id=%d)", i.getTemplateId()));
				}

				if (templateItems != null && !templateItems.isEmpty()) {
					Optional<SubscrObjectTreeTemplateItem> levelItem = templateItems.stream()
							.filter((item) -> item.getItemLevel() != null && item.getItemLevel().equals(level))
							.findAny();

					if (!levelItem.isPresent()) {
						throw new ModelIsNotValidException(
								String.format("SubscrObjectTree is not valid. SubscrObjectTreeTemplate (id=%d)",
										node.getTemplateId()));
					}
				}

				_initSubscrObjectTree(i, objectTreeType, level + 1, templateItems);
			});
		}
		return node;
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	private SubscrObjectTree _softDeleteSubscrObjectTree(SubscrObjectTree node) {
		checkNotNull(node);
		TreeNodeOperator operator = (i) -> EntityActions.softDelete(i);
		return _subscrObjectTreeOperation(node, operator, TreeNodeOperator.TYPE.POST);
	}

	/**
	 *
	 * @param node
	 * @param operator
	 * @return
	 */
	private SubscrObjectTree _subscrObjectTreeOperation(final SubscrObjectTree node, final TreeNodeOperator operator,
			final TreeNodeOperator.TYPE opType) {
		checkNotNull(node);

		if (opType == TreeNodeOperator.TYPE.PRE) {
			logger.info("Process operator for {}", node.isNew() ? "new" : node.getId());
			operator.doOperation(node);
		}

		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {

			List<SubscrObjectTree> childNodes = new ArrayList<>(node.getChildObjectList());

			for (int j = 0; j < childNodes.size(); j++) {
				SubscrObjectTree child = childNodes.get(j);
				if (child != null && node.getChildObjectList().contains(child)) {
					_subscrObjectTreeOperation(child, operator, opType);
				}
			}
		}

		if (opType == TreeNodeOperator.TYPE.POST) {
			logger.info("Process operator for {}", node.getId());
			operator.doOperation(node);
		}

		return node;
	}

	/**
	 *
	 * @param node
	 * @param objectTreeType
	 * @return
	 */
	public SubscrObjectTree initSubscrObjectTree(SubscrObjectTree node, ObjectTreeTypeKeyname objectTreeType) {

		checkNotNull(node);

		List<SubscrObjectTreeTemplateItem> templateItems = node.getTemplateId() != null
				? subscrObjectTreeTemplateService.selectSubscrObjectTreeTemplateItems(node.getTemplateId()) : null;

		return _initSubscrObjectTree(node, objectTreeType, 0, templateItems);
	}

	/**
	 *
	 * @param tree
	 * @param subscriberParam
	 */
	private void initTreeSubscriber(SubscrObjectTree tree, SubscriberParam subscriberParam) {
		if (subscriberParam.isRma()) {
			tree.setRmaSubscriberId(subscriberParam.getSubscriberId());
		} else {
			tree.setSubscriberId(subscriberParam.getSubscriberId());
		}
		tree.setIsRma(subscriberParam.isRma());
	}

    /**
     *
     * @param subscriberParam
     * @param templateId
     * @return
     */
	public SubscrObjectTree newSubscrObjectTree(SubscriberParam subscriberParam, Long templateId) {
		SubscrObjectTree root = new SubscrObjectTree();

		initTreeSubscriber(root, subscriberParam);

		root.setTemplateId(templateId);
		if (templateId != null) {
			SubscrObjectTreeTemplate template = subscrObjectTreeTemplateService
					.findSubscrObjectTreeTemplate(templateId);
			checkNotNull(template);
			root.setObjectName(template.getTemplateName());

			List<SubscrObjectTreeTemplateItem> items = subscrObjectTreeTemplateService
					.selectSubscrObjectTreeTemplateItems(templateId);

			if (!items.isEmpty()) {
				SubscrObjectTreeTemplateItem templateItem = items.get(0);
				root.setTemplateItemId(templateItem.getId());
				root.setIsLinkDeny(templateItem.getIsLinkDeny());
			}

		}
		root.setObjectTreeType(ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1.getKeyname());
		return root;

	}

	/**
	 *
	 * @param node
	 * @param objectName
	 * @return
	 */
	public SubscrObjectTree addChildObject(SubscrObjectTree node, String objectName) {
		checkNotNull(node);
		SubscrObjectTree child = new SubscrObjectTree();
		child.setObjectName(objectName);
		child.setParent(node);
		child.setRmaSubscriberId(node.getRmaSubscriberId());
		child.setSubscriberId(node.getSubscriberId());
		child.setIsRma(node.getIsRma());
		child.setObjectTreeType(node.getObjectTreeType());
		child.setTemplateId(node.getTemplateId());
		node.getChildObjectList().add(child);
		return child;
	}

	/**
	 *
	 * @param node
	 * @param objectName
	 * @param searchLevel
	 * @param level
	 * @return
	 */
	//	private SubscrObjectTree _searchObject(SubscrObjectTree node, String objectName, Integer searchLevel, int level) {
	//		checkNotNull(node);
	//		checkNotNull(objectName);
	//
	//		checkArgument(level >= 0);
	//
	//		if (objectName.equals(node.getObjectName()) && (searchLevel == null || searchLevel.equals(level))) {
	//			return node;
	//		}
	//
	//		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
	//			for (SubscrObjectTree child : node.getChildObjectList()) {
	//				SubscrObjectTree result = _searchObject(child, objectName, searchLevel, level + 1);
	//				if (result != null) {
	//					return result;
	//				}
	//			}
	//		}
	//
	//		return null;
	//	}

	/**
	 *
	 * @param node
	 * @param operator
	 * @param searchLevel
	 * @param level
	 * @return
	 */
	private SubscrObjectTree _searchObject(final SubscrObjectTree node, final CheckConditionOperator operator,
			final Integer searchLevel, final int level) {
		checkNotNull(node);
		checkNotNull(operator);

		checkArgument(level >= 0);

		if (operator.checkCondition(node) && (searchLevel == null || searchLevel.equals(level))) {
			return node;
		}

		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
			for (SubscrObjectTree child : node.getChildObjectList()) {
				SubscrObjectTree result = _searchObject(child, operator, searchLevel, level + 1);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	/**
	 *
	 * @param node
	 * @param objectName
	 * @param searchLevel
	 * @return
	 */
	public SubscrObjectTree searchObject(SubscrObjectTree node, String objectName, Integer searchLevel) {
		CheckConditionOperator operator = (n) -> objectName.equals(n.getObjectName());
		return _searchObject(node, operator, searchLevel, 0);
	}

    /**
     *
     * @param node
     * @param operator
     * @param searchLevel
     * @return
     */
	public SubscrObjectTree searchObject(SubscrObjectTree node, CheckConditionOperator operator, Integer searchLevel) {
		return _searchObject(node, operator, searchLevel, 0);
	}

	/**
	 *
	 * @param node
	 * @param objectName
	 * @return
	 */
	public SubscrObjectTree searchObject(SubscrObjectTree node, String objectName) {
		return searchObject(node, objectName, null);
	}

	/**
	 *
	 * @param node
	 * @param subscrObjectTreeId
	 * @param searchLevel
	 * @return
	 */
	public SubscrObjectTree searchObject(SubscrObjectTree node, Long subscrObjectTreeId, Integer searchLevel) {
		checkNotNull(subscrObjectTreeId);
		CheckConditionOperator operator = (n) -> subscrObjectTreeId.equals(n.getId());
		return _searchObject(node, operator, searchLevel, 0);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private SubscrObjectTree saveSubscrObjectTree(SubscrObjectTree entity) {
		SubscrObjectTree result = subscrObjectTreeRepository.save(entity);

		//		if (result.getChildNodeList() != null) {
		//			result.getChildNodeList().forEach(i -> {
		//				saveNode(i);
		//			});
		//		}
		return result;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrObjectTree saveRootSubscrObjectTree(SubscrObjectTree entity) {

		checkArgument(entity.getParent() == null);

		return saveSubscrObjectTree(entity);

	}

	/**
	 *
	 * @param subscrObjectTreeId
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteRootSubscrObjectTree(final SubscriberParam subscriberParam, Long subscrObjectTreeId) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		SubscrObjectTree node = selectSubscrObjectTree(subscrObjectTreeId);
		if (node.getParent() != null) {
			throw new PersistenceException(
					String.format("Delete not root node (subscrObjectTreeId=%d) is not supported by this method",
							subscrObjectTreeId));
		}
		checkNotNull(node);
		//subscrObjectTreeRepository.save(_softDeleteSubscrObjectTree(node));
		_deleteTreeContObjects(subscriberParam, node);

		List<Long> contObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(subscriberParam,
				subscrObjectTreeId);

		if (!contObjectIds.isEmpty()) {
			throw new PersistenceException(String
					.format("Error during delete ContObjectIds from SubscrObjectTree (id=%d)", subscrObjectTreeId));
		}

		_deleteTreeNode(node);

	}

	/**
	 *
	 * @param subscrObjectTreeId
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteChildSubscrObjectTreeNode(final SubscriberParam subscriberParam, Long subscrObjectTreeId,
			Long childSubscrObjectTreeId) {

		checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		SubscrObjectTree node = selectSubscrObjectTree(subscrObjectTreeId);
		checkNotNull(node);

		SubscrObjectTree childToDelete = searchObject(node, childSubscrObjectTreeId, null);

		if (childToDelete == null) {
			throw new PersistenceException(
					String.format("Child node (subscrObjectTreeId=%d) is not found", childSubscrObjectTreeId));
		}

		logger.info("Found child node {}", childToDelete.getId());

		_deleteTreeContObjects(subscriberParam, childToDelete);
		_deleteTreeNode(childToDelete);
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	private SubscrObjectTree _deleteTreeNode(final SubscrObjectTree node) {
		checkNotNull(node);
		final TreeNodeOperator operator = (i) -> {
			if (i == null) {
				return;
			}
			if (i.getParent() != null && i.getParent().getChildObjectList() != null) {
				i.getParent().getChildObjectList().remove(i);
			}
			subscrObjectTreeRepository.delete(i);
		};
		return _subscrObjectTreeOperation(node, operator, TreeNodeOperator.TYPE.POST);
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	private SubscrObjectTree _deleteTreeContObjects(final SubscriberParam subscriberParam,
			final SubscrObjectTree node) {
		checkNotNull(node);
		TreeNodeOperator operator = (i) -> {
			if (i == null || i.getId() == null) {
				return;
			}
			subscrObjectTreeContObjectService.deleteTreeContObjectsAll(subscriberParam, i.getId());
		};
		return _subscrObjectTreeOperation(node, operator, TreeNodeOperator.TYPE.POST);
	}

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrObjectTree> selectSubscrObjectTreeShort(final PortalUserIds portalUserIds) {
		List<Object[]> results = portalUserIds.isRma()
				? subscrObjectTreeRepository.selectRmaSubscrObjectTreeShort(portalUserIds.getSubscriberId())
				: subscrObjectTreeRepository.selectSubscrObjectTreeShort(portalUserIds.getSubscriberId());

		ColumnHelper helper = new ColumnHelper("id", "subscriberId", "rmaSubscriberId", "objectTreeType", "objectName");

		List<SubscrObjectTree> resultList = new ArrayList<>();

		for (Object[] row : results) {
			SubscrObjectTree t = new SubscrObjectTree();
			t.setId(helper.getResultAsClass(row, "id", Long.class));
			t.setSubscriberId(helper.getResultAsClass(row, "subscriberId", Long.class));
			t.setRmaSubscriberId(helper.getResultAsClass(row, "rmaSubscriberId", Long.class));
			t.setObjectTreeType(helper.getResultAsClass(row, "objectTreeType", String.class));
			t.setObjectName(helper.getResultAsClass(row, "objectName", String.class));
			resultList.add(t);
		}

		return resultList;
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Long selectRmaSubscriberId(final Long subscrObjectTreeId) {
		List<Long> ids = subscrObjectTreeRepository.selectRmaSubscriberIds(subscrObjectTreeId);
		return ids.isEmpty() ? null : ids.get(0);
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Long selectSubscriberId(final Long subscrObjectTreeId) {
		List<Long> ids = subscrObjectTreeRepository.selectSubscriberIds(subscrObjectTreeId);
		return ids.isEmpty() ? null : ids.get(0);
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean selectIsLinkDeny(final Long subscrObjectTreeId) {
		List<Boolean> ids = subscrObjectTreeRepository.selectIsLinkDeny(subscrObjectTreeId);
		return ids.isEmpty() ? false : Boolean.TRUE.equals(ids.get(0));
	}

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public void checkValidSubscriber(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {

		if (!checkValidSubscriberOk(subscriberParam, subscrObjectTreeId)) {
			throw new PersistenceException(
					String.format("SubscrObjectTree (id=%d) is not valid for subscriber", subscrObjectTreeId));
		}

	}

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public void checkValidSubscriber(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {

        if (!checkValidSubscriberOk(portalUserIds, subscrObjectTreeId)) {
            throw new PersistenceException(
                String.format("SubscrObjectTree (id=%d) is not valid for subscriber", subscrObjectTreeId));
        }
    }


    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkValidSubscriberOk(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {
		checkNotNull(portalUserIds);
		checkNotNull(subscrObjectTreeId);

		Long checkTreeSubscriberId = portalUserIds.isRma() ? selectRmaSubscriberId(subscrObjectTreeId)
				: selectSubscriberId(subscrObjectTreeId);

		return Long.valueOf(portalUserIds.getSubscriberId()).equals(checkTreeSubscriberId);

	}

	/**
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	private boolean checkSame(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}

		return o1.equals(o2);
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	public SubscrObjectTree checkSubscrObjectTreeSubscriber(final SubscrObjectTree node) {
		checkNotNull(node);
		TreeNodeOperator operator = (i) -> {
			if (!checkSame(node.getIsRma(), i.getIsRma())) {
				throw new IllegalStateException("isRma property is not same");
			}
			if (!checkSame(node.getRmaSubscriberId(), i.getRmaSubscriberId())) {
				throw new IllegalStateException("rmaSubscriberId property is not same");
			}
			if (!checkSame(node.getSubscriberId(), i.getSubscriberId())) {
				throw new IllegalStateException("subscriberId property is not same");
			}
		};
		return _subscrObjectTreeOperation(node, operator, TreeNodeOperator.TYPE.PRE);
	}

}
