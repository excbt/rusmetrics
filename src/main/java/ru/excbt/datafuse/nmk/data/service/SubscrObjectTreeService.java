package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;

@Service
public class SubscrObjectTreeService extends AbstractService {

	@Autowired
	private SubscrObjectTreeRepository subscrObjectTreeRepository;

	@Autowired
	private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrObjectTree findSubscrObjectTree(Long id) {
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
				i.setIsRma(node.getIsRma());
				i.setDeleted(node.getDeleted());

				// Check props
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
	private SubscrObjectTree softDeleteSubscrObjectTree(SubscrObjectTree node) {
		checkNotNull(node);

		softDelete(node);
		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
			node.getChildObjectList().forEach(i -> {
				softDeleteSubscrObjectTree(i);
			});
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
	 * @param rmaSubscriberId
	 * @param templateId
	 * @return
	 */
	public SubscrObjectTree newSubscrObjectTree(Long rmaSubscriberId, Long templateId) {
		SubscrObjectTree root = new SubscrObjectTree();

		root.setRmaSubscriberId(rmaSubscriberId);
		root.setIsRma(true);
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
	private SubscrObjectTree _searchObject(SubscrObjectTree node, String objectName, Integer searchLevel, int level) {
		checkNotNull(node);
		checkNotNull(objectName);

		checkArgument(level >= 0);

		if (objectName.equals(node.getObjectName()) && (searchLevel == null || searchLevel.equals(level))) {
			return node;
		}

		if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
			for (SubscrObjectTree child : node.getChildObjectList()) {
				SubscrObjectTree result = _searchObject(child, objectName, searchLevel, level + 1);
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
		return _searchObject(node, objectName, searchLevel, 0);
	}

	/**
	 * 
	 * @param node
	 * @param objectName
	 * @return
	 */
	public SubscrObjectTree searchObject(SubscrObjectTree node, String objectName) {
		return _searchObject(node, objectName, null, 0);
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
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrObjectTree saveRootSubscrObjectTree(SubscrObjectTree entity) {
		checkArgument(entity.getParent() == null);

		return saveSubscrObjectTree(entity);

	}

	/**
	 * 
	 * @param subscrObjectTreeId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteRootSubscrObjectTree(Long subscrObjectTreeId) {
		SubscrObjectTree node = findSubscrObjectTree(subscrObjectTreeId);
		checkNotNull(node);
		subscrObjectTreeRepository.save(softDeleteSubscrObjectTree(node));
	}

}
