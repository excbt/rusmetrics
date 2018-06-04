package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeValidationService;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.math.BigInteger;
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


    public SubscrObjectTreeContObjectService(SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, SubscrObjectTreeValidationService subscrObjectTreeValidationService, EntityManager em, ObjectAccessService objectAccessService) {
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.subscrObjectTreeValidationService = subscrObjectTreeValidationService;
        this.em = em;
        this.objectAccessService = objectAccessService;
    }

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	protected List<ContObject> selectTreeContObjects2(final SubscriberParam subscriberParam,
			final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
	}

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	public List<ContObject> selectTreeContObjects(final SubscriberParam subscriberParam,
			final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		List<ContObject> result = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
		if (subscriberParam.isRma()) {
            objectAccessService.setupRmaHaveSubscr(subscriberParam,result);
		}

		return result.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).collect(Collectors.toList());

	}

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	public List<Long> selectTreeContObjectIds(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {
        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);
		return subscrObjectTreeContObjectRepository.selectContObjectIds(subscrObjectTreeId);
	}

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void saveTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);

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
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void addTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);

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
	}

    /**
     *
     * @param subscriberParam
     * @param subscrObjectTreeId
     * @param contObjectIds
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void deleteTreeContObjects(final SubscriberParam subscriberParam, final Long subscrObjectTreeId,
			final List<Long> contObjectIds) {

        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);

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
     * @param subscriberParam
     * @param subscrObjectTreeId
     */
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUBSCR_ADMIN })
	@Transactional
	public void deleteTreeContObjectsAll(final SubscriberParam subscriberParam, final Long subscrObjectTreeId) {

        subscrObjectTreeValidationService.checkValidSubscriber(subscriberParam, subscrObjectTreeId);

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		subscrObjectTreeContObjectRepository.delete(contObjects);
	}

    /**
     *
     * @param portalUserIds
     * @param subscrObjectTreeId
     * @return
     */
	@Transactional( readOnly = true)
	public List<Long> selectTreeContObjectIdsAllLevels(final PortalUserIds portalUserIds,
			final Long subscrObjectTreeId) {

        subscrObjectTreeValidationService.checkValidSubscriber(portalUserIds, subscrObjectTreeId);

		List<Long> resultList = new ArrayList<>();

		ColumnHelper columnHelper = new ColumnHelper("cont_object_id");
		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");

		String funcName;

		if (portalUserIds.isRma()) {
			funcName = " portal.get_subscr_cont_object_tree_cont_object_ids_rma(:p_subscriber_id,:p_subscr_object_tree_id); ";
		} else {
			funcName = " portal.get_subscr_cont_object_tree_cont_object_ids_subscr(:p_subscriber_id,:p_subscr_object_tree_id); ";
		}

		sqlString.append(funcName);

		Query q1 = em.createNativeQuery(sqlString.toString());
		q1.setParameter("p_subscriber_id", portalUserIds.getSubscriberId());
		q1.setParameter("p_subscr_object_tree_id", subscrObjectTreeId);

		List<?> queryResultList = q1.getResultList();

        for (Object object : queryResultList) {
            BigInteger value = DBRowUtil.asBigInteger(object,
                () -> new PersistenceException(String.format("Invalid return result for %s, subscrObjectTreeId = %d",
                    funcName, subscrObjectTreeId)));
            resultList.add(value.longValue());
        }

		return resultList;
	}

}
