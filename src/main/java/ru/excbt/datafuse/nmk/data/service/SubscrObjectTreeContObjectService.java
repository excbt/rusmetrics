package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.sql.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeValidationService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscrObjectTreeContObjectService {

	private static final Logger log = LoggerFactory.getLogger(SubscrObjectTreeContObjectService.class);

	private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;


	private final SubscrObjectTreeValidationService subscrObjectTreeValidationService;

	protected final EntityManager em;

	protected final ObjectAccessService objectAccessService;

    private final QueryDSLService queryDSLService;

    public SubscrObjectTreeContObjectService(SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, SubscrObjectTreeValidationService subscrObjectTreeValidationService, EntityManager em, ObjectAccessService objectAccessService, QueryDSLService queryDSLService) {
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.subscrObjectTreeValidationService = subscrObjectTreeValidationService;
        this.em = em;
        this.objectAccessService = objectAccessService;
        this.queryDSLService = queryDSLService;
    }

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	protected List<ContObject> selectTreeContObjects2(final PortalUserIds portalUserIds,
			final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	public List<ContObject> selectTreeContObjects(final PortalUserIds portalUserIds,
			final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);
		List<ContObject> result = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
		if (portalUserIds.isRma()) {
            objectAccessService.setupRmaHaveSubscr(portalUserIds,result);
		}

		return result.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).collect(Collectors.toList());

	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	public List<Long> selectTreeContObjectIds(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjectIds(subscrObjectTreeId);
	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void saveTreeContObjects(final PortalUserIds portalUserIds, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

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
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void addTreeContObjects(final PortalUserIds portalUserIds, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

//        subscrObjectTreeValidationService.checkValidSubscriberOk_new(portalUserIds, subscrObjectTreeId);

		boolean isLinkDeny = subscrObjectTreeValidationService.selectIsLinkDeny(subscrObjectTreeId);

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
        subscrObjectTreeContObjectRepository.flush();
	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void removeTreeContObjects(final PortalUserIds portalUserIds, final Long subscrObjectTreeId,
                                      final List<Long> contObjectIds) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		List<SubscrObjectTreeContObject> deleteContObjects = new ArrayList<>();

		for (SubscrObjectTreeContObject co : contObjects) {
			if (contObjectIds.contains(co.getContObjectId())) {
				deleteContObjects.add(co);
			}
		}

		subscrObjectTreeContObjectRepository.delete(deleteContObjects);
        subscrObjectTreeContObjectRepository.flush();
	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void deleteTreeContObjectsAll(final PortalUserIds portalUserIds, final Long subscrObjectTreeId) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		subscrObjectTreeContObjectRepository.delete(contObjects);
	}


    private final static class StoredProcResultPaths {
        private static final NumberPath<Long> contObjectId = Expressions.numberPath(Long.class, "cont_object_id");
    }


    private List<Long> selectTreeContObjectIdsAllLevelsInt(final PortalUserIds portalUserIds,
                                                       final Long subscrObjectTreeId,
                                                       String storedProcName) {

        StringTemplate storedProcFunction = Expressions.stringTemplate(storedProcName,
            Expressions.asNumber(portalUserIds.getSubscriberId()),
            Expressions.asNumber(subscrObjectTreeId));

        List<Long> resultList = queryDSLService.doReturningWork((c) -> {
            SQLQuery<Long> query = new SQLQuery<>(c, QueryDSLService.templates)
                .select(StoredProcResultPaths.contObjectId)
                .from(storedProcFunction);
            List<Long> resultIds = query.fetch();
            return resultIds;
        });

        return resultList;
    }


    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
    @Deprecated
	@Transactional( readOnly = true)
	public List<Long> selectTreeContObjectIdsAllLevels(final PortalUserIds portalUserIds,
			final Long subscrObjectTreeId) {

        String storedProcName;
        if (portalUserIds.isRma()) {
            storedProcName = " portal.get_subscr_cont_object_tree_cont_object_ids_rma({0},{1}) ";
        } else {
            storedProcName = " portal.get_subscr_cont_object_tree_cont_object_ids_subscr({0},{1})";
        }

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);
	    return selectTreeContObjectIdsAllLevelsInt (portalUserIds, subscrObjectTreeId, storedProcName);

	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
    @Transactional( readOnly = true)
    public List<Long> selectTreeContObjectIdsAllLevels_new(final PortalUserIds portalUserIds,
                                                            final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriberOk_new(portalUserIds, subscrObjectTreeId);
        String storedProcName = " portal.get_subscr_cont_object_tree_cont_object_ids_subscr({0},{1})";
        return selectTreeContObjectIdsAllLevelsInt (portalUserIds, subscrObjectTreeId, storedProcName);

    }


}
