package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.QSubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrObjectTreeMapper;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeDataVM;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscrObjectTreeService {

	private static final Logger logger = LoggerFactory.getLogger(SubscrObjectTreeService.class);

	public static final String VM_MODE_NORMAL = "NORMAL";
	public static final String ADD_MODE_SIBLING = "sibling";
	public static final String ADD_MODE_CHILD = "child";

	private final SubscrObjectTreeRepository subscrObjectTreeRepository;

	private final SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

	private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

	private final SubscrObjectTreeMapper subscrObjectTreeMapper;

    private final EntityManager entityManager;

    private final SubscrObjectTreeValidationService subscrObjectTreeValidationService;

    public SubscrObjectTreeService(SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeTemplateService subscrObjectTreeTemplateService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, SubscrObjectTreeMapper subscrObjectTreeMapper, EntityManager entityManager, SubscrObjectTreeValidationService subscrObjectTreeValidationService) {
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeTemplateService = subscrObjectTreeTemplateService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
        this.subscrObjectTreeMapper = subscrObjectTreeMapper;
        this.entityManager = entityManager;
        this.subscrObjectTreeValidationService = subscrObjectTreeValidationService;
    }

    /**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since dd.02.2016
	 *
	 */
	private interface CheckConditionOperator {
		boolean checkCondition(SubscrObjectTree node);
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
		void doOperation(SubscrObjectTree node);

		enum TYPE {
			PRE, POST
		}

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrObjectTree selectSubscrObjectTree(Long id) {
		return subscrObjectTreeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrObjectTree.class, id));
	}

    /**
     *
     * @param id
     * @return
     */
	@Transactional( readOnly = true)
	public SubscrObjectTreeDTO findSubscrObjectTreeDTO(Long id) {
	    SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrObjectTree.class, id));
        SubscrObjectTreeDTO dto = subscrObjectTreeMapper.toDto(ObjectFilters.deletedFilter(subscrObjectTree));
        sortChildObjects(dto);
		return dto;
	}

	private void sortChildObjects(SubscrObjectTreeDTO dto) {
	    if (dto == null || dto.getChildObjectList() == null) {
	        return;
        }

        if (!dto.getChildObjectList().isEmpty()) {
            dto.getChildObjectList().removeIf(i -> i.getDeleted() == 1);
            dto.getChildObjectList().sort(Comparator.comparing(SubscrObjectTreeDTO::getObjectName));
        }
        for (SubscrObjectTreeDTO child : dto.getChildObjectList()) {
	        sortChildObjects(child);
        }
    }

    @Transactional( readOnly = true)
    public SubscrObjectTreeVM findSubscrObjectTreeVM(Long id) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrObjectTree.class, id));
        SubscrObjectTreeVM result = subscrObjectTreeMapper.toVM(ObjectFilters.deletedFilter(subscrObjectTree));
        return result;
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

    private SubscrObjectTree buildSubscrObjectTree(SubscrObjectTree node, ObjectTreeTypeKeyname objectTreeType,
                                                   int level, List<SubscrObjectTreeTemplateItem> templateItems) {
        checkNotNull(node);
        checkNotNull(objectTreeType);
        node.setObjectTreeType(objectTreeType.getKeyname());
        if (node.getChildObjectList() != null && !node.getChildObjectList().isEmpty()) {
            for (SubscrObjectTree child: node.getChildObjectList()) {

                // init Props
                child.setParent(node);
                child.setParentId(node.getId());
                child.setTemplateId(node.getTemplateId());
                child.setRmaSubscriberId(node.getRmaSubscriberId());
                child.setSubscriberId(node.getSubscriberId());
                child.setIsRma(node.getIsRma());
                child.setDeleted(node.getDeleted());
                child.setTreeMode(node.getTreeMode());

                // Check props

                if (child.getTemplateId() != null && child.getTemplateItemId() == null) {
                    throw new ModelIsNotValidException(String.format(
                        "SubscrObjectTree is not valid. SubscrObjectTreeTemplate (id=%d)", child.getTemplateId()));
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

                buildSubscrObjectTree(child, objectTreeType, level + 1, templateItems);
            }
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
	@Transactional
	public SubscrObjectTree initSubscrObjectTree(SubscrObjectTree node, ObjectTreeTypeKeyname objectTreeType) {

		checkNotNull(node);

		List<SubscrObjectTreeTemplateItem> templateItems = node.getTemplateId() != null
				? subscrObjectTreeTemplateService.selectSubscrObjectTreeTemplateItems(node.getTemplateId()) : null;

		return _initSubscrObjectTree(node, objectTreeType, 0, templateItems);
	}

	/**
	 *
	 * @param tree
	 * @param portalUserIds
	 */
	private void initTreeSubscriber(SubscrObjectTree tree, PortalUserIds portalUserIds) {
		if (portalUserIds.isRma()) {
			tree.setRmaSubscriberId(portalUserIds.getSubscriberId());
		} else {
			tree.setSubscriberId(portalUserIds.getSubscriberId());
		}
		tree.setIsRma(portalUserIds.isRma());
	}

    /**
     *
     * @param portalUserIds
     * @param templateId
     * @return
     */
	public SubscrObjectTree newSubscrObjectTree(PortalUserIds portalUserIds, Long templateId) {
		SubscrObjectTree root = new SubscrObjectTree();

		initTreeSubscriber(root, portalUserIds);

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
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public SubscrObjectTree saveRootSubscrObjectTree(SubscrObjectTree entity) {

		checkArgument(entity.getParent() == null);

		return saveSubscrObjectTree(entity);

	}

	/**
	 *
	 * @param subscrObjectTreeId
	 */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void deleteRootSubscrObjectTree(final PortalUserIds portalUserIds, Long subscrObjectTreeId) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

		SubscrObjectTree node = selectSubscrObjectTree(subscrObjectTreeId);
		if (node.getParent() != null) {
			throw new PersistenceException(
					String.format("Delete not root node (subscrObjectTreeId=%d) is not supported by this method",
							subscrObjectTreeId));
		}
		checkNotNull(node);
		_deleteTreeContObjects(portalUserIds, node);

		List<Long> contObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels_new(portalUserIds,
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
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void deleteChildSubscrObjectTreeNode(final PortalUserIds portalUserIds, Long subscrObjectTreeId,
			Long childSubscrObjectTreeId) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

		SubscrObjectTree node = selectSubscrObjectTree(subscrObjectTreeId);
		checkNotNull(node);

		SubscrObjectTree childToDelete = searchObject(node, childSubscrObjectTreeId, null);

		if (childToDelete == null) {
			throw new PersistenceException(
					String.format("Child node (subscrObjectTreeId=%d) is not found", childSubscrObjectTreeId));
		}

		logger.info("Found child node {}", childToDelete.getId());

		_deleteTreeContObjects(portalUserIds, childToDelete);
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
	private SubscrObjectTree _deleteTreeContObjects(final PortalUserIds portalUserIds,
			final SubscrObjectTree node) {
		checkNotNull(node);
		TreeNodeOperator operator = (i) -> {
			if (i == null || i.getId() == null) {
				return;
			}
			subscrObjectTreeContObjectService.deleteTreeContObjectsAll(portalUserIds, i.getId());
		};
		return _subscrObjectTreeOperation(node, operator, TreeNodeOperator.TYPE.POST);
	}

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional( readOnly = true)
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
     * @param portalUserIds
     * @return
     */
	@Transactional( readOnly = true)
	public List<SubscrObjectTreeVM> selectSubscrObjectTreeShortVM(ObjectTreeTypeKeyname objectTreeTypeKeyname, final PortalUserIds portalUserIds) {


		List<Object[]> results = portalUserIds.isRma()
				? subscrObjectTreeRepository.selectRmaSubscrObjectTreeShort2(portalUserIds.getSubscriberId())
				: subscrObjectTreeRepository.selectSubscrObjectTreeShort(portalUserIds.getSubscriberId());

		ColumnHelper helper = new ColumnHelper("id", "subscriberId", "rmaSubscriberId", "objectTreeType", "objectName", "isActive");

		List<SubscrObjectTreeVM> resultList = new ArrayList<>();

		for (Object[] row : results) {
            SubscrObjectTreeVM t = new SubscrObjectTreeVM();
			t.setId(helper.getResultAsClass(row, "id", Long.class));
			t.setSubscriberId(helper.getResultAsClass(row, "subscriberId", Long.class));
			t.setRmaSubscriberId(helper.getResultAsClass(row, "rmaSubscriberId", Long.class));
			t.setObjectTreeType(helper.getResultAsClass(row, "objectTreeType", String.class));
			t.setObjectName(helper.getResultAsClass(row, "objectName", String.class));
			t.setIsActive(helper.getResultAsClass(row, "isActive", Boolean.class));;
			resultList.add(t);
		}

		return resultList;
	}


	@Transactional( readOnly = true)
	public Page<SubscrObjectTreeVM> selectSubscrObjectTreeShortVMPage(final PortalUserIds portalUserIds,
                                                                      Long subscriberId,
                                                                      ObjectTreeTypeKeyname treeTypeKeyname,
                                                                      String searchString,
                                                                      Pageable pageable) {


	    QSubscrObjectTree qSubscrObjectTree = QSubscrObjectTree.subscrObjectTree;

        BooleanExpression subscriberExpr = qSubscrObjectTree.deleted.eq(0)
            .and(qSubscrObjectTree.parentId.isNull())
            .and(qSubscrObjectTree.subscriberId.eq(subscriberId))
            .and(qSubscrObjectTree.objectTreeType.eq(treeTypeKeyname.getKeyname()));
//            .and(portalUserIds.isRma()
//                ? qSubscrObjectTree.rmaSubscriberId.eq(subscriberId).or(qSubscrObjectTree.subscriberId.eq(subscriberId))
//                : qSubscrObjectTree.subscriberId.eq(subscriberId));



        WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder().and(subscriberExpr);

        if (searchString != null) {
            BooleanExpression searchCondition  = QueryDSLUtil.buildSearchCondition(searchString,
                (s1) -> qSubscrObjectTree.objectName.toLowerCase().like(QueryDSLUtil.lowerCaseLikeStr.apply(s1)));
            whereClauseBuilder.and(searchCondition);
        }

        Page<SubscrObjectTree> subscrObjectTreePage = subscrObjectTreeRepository.findAll(whereClauseBuilder, pageable);

        Page<SubscrObjectTreeVM> subscrObjectTreeVMPage = subscrObjectTreePage.map(subscrObjectTreeMapper::toVM);

		return subscrObjectTreeVMPage;
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional( readOnly = true)
	public Long selectRmaSubscriberId(final Long subscrObjectTreeId) {
		List<Long> ids = subscrObjectTreeRepository.selectRmaSubscriberIds(subscrObjectTreeId);
		return ids.isEmpty() ? null : ids.get(0);
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional( readOnly = true)
	public Long selectSubscriberId(final Long subscrObjectTreeId) {
		List<Long> ids = subscrObjectTreeRepository.selectSubscriberIds(subscrObjectTreeId);
		return ids.isEmpty() ? null : ids.get(0);
	}

//	@Transactional( readOnly = true)
//	public boolean selectIsLinkDeny(final Long subscrObjectTreeId) {
//		List<Boolean> ids = subscrObjectTreeRepository.selectIsLinkDeny(subscrObjectTreeId);
//		return ids.isEmpty() ? false : Boolean.TRUE.equals(ids.get(0));
//	}


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

    @Transactional
    public Optional<SubscrObjectTreeDTO> addSubscrObjectTree(String treeName,
                                                              Long templateId,
                                                              ObjectTreeTypeKeyname objectTreeType,
                                                              PortalUserIds portalUserIds,
                                                              Long subscriberId) {

        checkNotNull(treeName);

        SubscrObjectTree root = new SubscrObjectTree();
        root.setObjectName(treeName);
        root.setSubscriberId(subscriberId);
        root.setIsLinkDeny(true);
        root.setTreeMode("TEMPLATE");
        root.setIsActive(true);


        List<SubscrObjectTreeTemplateItem> templateItems = templateId != null
            ? subscrObjectTreeTemplateService.selectSubscrObjectTreeTemplateItems(templateId) : null;

        SubscrObjectTree preSave = buildSubscrObjectTree(root, objectTreeType, 0, templateItems);

        SubscrObjectTree result = subscrObjectTreeRepository.saveAndFlush(preSave);

        return Optional.ofNullable(subscrObjectTreeMapper.toDto(result));
    }

    @Transactional
    public Optional<SubscrObjectTreeVM> setActiveSubscrObjectTree(Long treeNodeId,
                                             boolean isActive,
                                             PortalUserIds portalUserIds,
                                             Long subscriberId) {

        Optional<SubscrObjectTree> nodeOpt = subscrObjectTreeRepository.findById(treeNodeId);
        if (!nodeOpt.isPresent()) {
            return Optional.empty();
        }
        if (!nodeOpt.get().getSubscriberId().equals(subscriberId)) {
            return Optional.empty();
        }

        nodeOpt.get().setIsActive(isActive);
        SubscrObjectTree savedNode = subscrObjectTreeRepository.saveAndFlush(nodeOpt.get());

	    return Optional.ofNullable(subscrObjectTreeMapper.toVM(savedNode));
    }

    @Transactional
    public boolean deleteSubscrObjectTreeNode(Long treeNodeId,
                                              ObjectTreeTypeKeyname objectTreeType,
                                              PortalUserIds portalUserIds,
                                              Long subscriberId) {
        Optional<SubscrObjectTree> deleteCanidate = subscrObjectTreeRepository.findById(treeNodeId);

        if (!deleteCanidate.isPresent()) {
            return false;
        }

        if (!deleteCanidate.get().getSubscriberId().equals(subscriberId) || !portalUserIds.isRma()) {
            return false;
        }

        if (!deleteCanidate.get().getObjectTreeType().equals(objectTreeType.getKeyname())) {
            return false;
        }

        deleteCanidate.get().setDeleted(1);
        subscrObjectTreeRepository.saveAndFlush(deleteCanidate.get());
        return true;
    }

    @Transactional
    public Optional<SubscrObjectTreeVM> addSubscrObjectTreeNode(SubscrObjectTreeVM vm,
                                                                PortalUserIds portalUserIds,
                                                                Long subscriberId,
                                                                String addMode) {

	    if (vm.getParentId() == null) {
	        return Optional.empty();
        }

        if (!ADD_MODE_SIBLING.equals(addMode) && !ADD_MODE_CHILD.equals(addMode)) {
            return Optional.empty();
        }



        Optional<SubscrObjectTree> parentNodeOpt = subscrObjectTreeRepository.findById(vm.getParentId());

	    if (!parentNodeOpt.isPresent() || parentNodeOpt.filter(n -> !n.getSubscriberId().equals(subscriberId)).isPresent()) {
            return Optional.empty();
        }

        SubscrObjectTree parentNode = parentNodeOpt.get();
        if (ADD_MODE_SIBLING.equals(addMode) && parentNodeOpt.get().getParent() != null) {
	        parentNode = parentNode.getParent();
        }

        Optional<SubscrObjectTree> editedNodeOpt = vm.getId() != null ?
            subscrObjectTreeRepository.findById(vm.getId()) :
            Optional.empty();

        SubscrObjectTree editedNode;

	    if (editedNodeOpt.isPresent()) {
	        editedNode = editedNodeOpt.get();
            subscrObjectTreeMapper.updateTreeFromVM(editedNode, vm);
            editedNode.setObjectTreeType( vm.getObjectTreeType() );
            editedNode.setObjectName( vm.getObjectName() );
            editedNode.setObjectDescription( vm.getObjectDescription() );
            editedNode.setIsLinkDeny( vm.getIsLinkDeny() );
            editedNode.setVersion( vm.getVersion() );
            editedNode.setTreeMode( parentNode.getTreeMode() );

        } else {
            editedNode = subscrObjectTreeMapper.toEntity(vm);
            editedNode.setParent(parentNode);
            editedNode.setParentId(parentNode.getId());
            editedNode.setTemplateId(parentNode.getTemplateId());
            editedNode.setRmaSubscriberId(parentNode.getRmaSubscriberId());
            editedNode.setSubscriberId(parentNode.getSubscriberId());
            editedNode.setIsRma(parentNode.getIsRma());
            editedNode.setTreeMode(parentNode.getTreeMode());
            editedNode.setDeleted(parentNode.getDeleted());
        }

        SubscrObjectTree resultNode = subscrObjectTreeRepository.saveAndFlush(editedNode);
	    entityManager.refresh(parentNode);

	    return Optional.ofNullable(subscrObjectTreeMapper.toVM(resultNode));
    }

    @Transactional
    public Optional<SubscrObjectTreeVM> updateSubscrObjectTreeNode(SubscrObjectTreeVM vm,
                                                                PortalUserIds portalUserIds,
                                                                Long subscriberId) {

        if (vm.getId() == null) {
            return Optional.empty();
        }

        Optional<SubscrObjectTree> editedNodeOpt = vm.getId() != null ?
            subscrObjectTreeRepository.findById(vm.getId()) :
            Optional.empty();

        SubscrObjectTree editedNode;

        if (editedNodeOpt.isPresent()) {
            editedNode = editedNodeOpt.get();
            if (editedNode.getParent() != null) {
                editedNode.setObjectName( vm.getObjectName() );
                editedNode.setObjectDescription( vm.getObjectDescription() );
                editedNode.setIsLinkDeny( vm.getIsLinkDeny() );
                editedNode.setVersion( vm.getVersion() );

            } else {
                editedNode.setObjectName( vm.getObjectName() );
                editedNode.setVersion( vm.getVersion() );
            }
        } else {
            return Optional.empty();
        }

        SubscrObjectTree resultNode = subscrObjectTreeRepository.saveAndFlush(editedNode);
        if (editedNode.getParent() != null) {
            entityManager.refresh(editedNode.getParent());
        }

        return Optional.ofNullable(subscrObjectTreeMapper.toVM(resultNode));
    }

    @Transactional
    public boolean addContObjectsToNode(Long subscrObjectTreeId,
                                        Long nodeId,
                                     PortalUserIds portalUserIds,
                                     Long subscriberId,
                                     SubscrObjectTreeDataVM dataVM) {

	    if (dataVM == null || dataVM.getContObjectIds() == null) {
	        return false;
        }

        if (dataVM.getContObjectIds().isEmpty()) {
	        return true;
        }

        subscrObjectTreeValidationService.checkValidSubscriberOk_new(portalUserIds, subscrObjectTreeId);

        List<Long> existingContObjectIds = subscrObjectTreeContObjectService
            .selectTreeContObjectIdsAllLevels_new(portalUserIds, nodeId);

	    long checkCount = dataVM.getContObjectIds().stream().filter(existingContObjectIds::equals).count();

	    if (checkCount > 0) {
            throw new PersistenceException(
                String.format("Twice link ContObjects for subscrObjectTreeId(%d) is not allowed", nodeId));
        }

        subscrObjectTreeContObjectService.addTreeContObjects(portalUserIds, nodeId,
            dataVM.getContObjectIds());

        return true;

    }

    @Transactional
    public boolean removeContObjectsFromNode(Long subscrObjectTreeId,
                                        Long nodeId,
                                     PortalUserIds portalUserIds,
                                     Long subscriberId,
                                     SubscrObjectTreeDataVM dataVM) {

	    if (dataVM == null || dataVM.getContObjectIds() == null) {
	        return false;
        }

        if (dataVM.getContObjectIds().isEmpty()) {
	        return true;
        }

        subscrObjectTreeValidationService.checkValidSubscriberOk_new(portalUserIds, subscrObjectTreeId);

        subscrObjectTreeContObjectService.removeTreeContObjects(portalUserIds, nodeId,
            dataVM.getContObjectIds());

        return true;

    }


    public List<Long> findNodeOnlyLinkedContObjectIds(Long nodeId,
                                                      PortalUserIds portalUserIds,
                                                      Long subscriberId) {

	    return subscrObjectTreeContObjectService.selectTreeContObjectIds(portalUserIds, nodeId);

    }

    public List<Long> findAllLinkedContObjectIds(Long nodeId,
                                              PortalUserIds portalUserIds,
                                              Long subscriberId) {
        return subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels_new(portalUserIds,
            nodeId);
    }



}
