package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис для работы с привязкой абонентов и объекта учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Service
public class SubscrContObjectService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectService.class);

	private static final List<ContObject> EMPTY_CONT_OBJECTS_LIST = Collections.unmodifiableList(new ArrayList<>());

	private final SubscrContObjectRepository subscrContObjectRepository;

	private final SubscriberService subscriberService;

    private final ContGroupService contGroupService;

    private final ContObjectMapper contObjectMapper;

    private final ContObjectAccessRepository contObjectAccessRepository;

    private final ObjectAccessService objectAccessService;

	@Autowired
    public SubscrContObjectService(SubscrContObjectRepository subscrContObjectRepository, SubscriberService subscriberService, ContGroupService contGroupService, ContObjectMapper contObjectMapper, ContObjectAccessRepository contObjectAccessRepository, ObjectAccessService objectAccessService) {
        this.subscrContObjectRepository = subscrContObjectRepository;
        this.subscriberService = subscriberService;
        this.contGroupService = contGroupService;
        this.contObjectMapper = contObjectMapper;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.objectAccessService = objectAccessService;
    }

    private final Access access = new Access();

	public Access access() {
	    return this.access;
    }


	public class Access {
        public void linkSubscrContObject(Subscriber subscriber, ContObject contObject,
                                         java.time.LocalDate fromDate) {
            checkNotNull(contObject);
            checkNotNull(subscriber);
            checkNotNull(fromDate);

            if (!objectAccessService.checkContObjectId(subscriber.getId(), contObject.getId())
            ) {
                createSubscrContObjectLink(contObject, subscriber, fromDate);
            }
        }

        public void updateSubscrContObjects(Long subscriberId, List<Long> contObjectIds,
                                                        LocalDate subscrBeginDate) {
            updateSubscrContObjectsInternal(subscriberId, contObjectIds, subscrBeginDate);
        }

        public void unlinkSubscrContObject(Subscriber subscriber, ContObject contObject, java.time.LocalDate revokeDate) {
            List<SubscrContObject> delSubscrContObjects = subscrContObjectRepository.selectActualSubscrContObjects(subscriber.getId(),
                contObject.getId());
            deleteSubscrContObject(delSubscrContObjects, revokeDate);
        }


    }



	/**
	 * TO DO
	 * @param objects
	 */
	private void deleteSubscrContObject(List<SubscrContObject> objects, java.time.LocalDate subscrEndDate) {
		checkNotNull(objects);
		checkNotNull(subscrEndDate);
		Date endDate = LocalDateUtils.asDate(subscrEndDate);

		List<SubscrContObject> updateCandidate = new ArrayList<>();
		objects.forEach(i -> {
			if (i.getSubscrEndDate() == null) {
				i.setSubscrEndDate(endDate);
				updateCandidate.add(i);
			}
		});
		subscrContObjectRepository.save(updateCandidate);
	}



    private SubscrContObject createSubscrContObjectLink(ContObject contObject, Subscriber subscriber,
                                                        java.time.LocalDate fromDate) {
        checkNotNull(contObject);
        checkNotNull(subscriber);
        checkNotNull(fromDate);

        SubscrContObject subscrContObject = new SubscrContObject();
        subscrContObject.setContObject(contObject);
        subscrContObject.setSubscriber(subscriber);
        subscrContObject.setSubscrBeginDate(LocalDateUtils.asDate(fromDate));
        return subscrContObjectRepository.save(subscrContObject);
    }



    /**
     *
     * @param contObjectId
     * @param subscriber
     * @param subscrBeginDate
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT)
    @Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
    private SubscrContObject createSubscrContObjectLink(Long contObjectId, Subscriber subscriber,
                                                        LocalDate subscrBeginDate) {
        checkNotNull(contObjectId);
        checkNotNull(subscriber);
        checkNotNull(subscrBeginDate);


        SubscrContObject subscrContObject = new SubscrContObject();
        subscrContObject.setContObject(new ContObject().id(contObjectId));
        subscrContObject.setSubscriber(subscriber);
        subscrContObject.setSubscrBeginDate(subscrBeginDate.toDate());
        return subscrContObjectRepository.save(subscrContObject);
    }

    /**
     *
     * @param subscriberId
     * @param contObjectIds
     * @param subscrBeginDate
     * @return
     */
    private void updateSubscrContObjectsInternal(Long subscriberId, List<Long> contObjectIds,
                                                    LocalDate subscrBeginDate) {

        LocalDate subscrCurrentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);
        Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);

        if (subscrCurrentDate.isBefore(subscrBeginDate)) {
            throw new PersistenceException(
                String.format("Subscriber (id=%d) Subscr Current Date is before subscrBeginDate ", subscriberId));
        }

        List<Long> currentContObjectsIds = objectAccessService.findContObjectIds(subscriberId);

        List<Long> addContObjectIds = new ArrayList<>();
        List<Long> delContObjectIds = new ArrayList<>();

        currentContObjectsIds.forEach(i -> {
            if (!contObjectIds.contains(i)) {
                delContObjectIds.add(i);
            }
        });

        contObjectIds.forEach(i -> {
            if (!currentContObjectsIds.contains(i)) {
                addContObjectIds.add(i);
            }
        });

        List<SubscrContObject> delSubscrContObjects = new ArrayList<>();
        delContObjectIds.forEach(i -> {
            List<SubscrContObject> delCandidate = subscrContObjectRepository.selectActualSubscrContObjects(subscriberId,
                i);
            if (delCandidate.size() > 0) {
                delSubscrContObjects.addAll(delCandidate);
            }
        });

        deleteSubscrContObject(delSubscrContObjects, LocalDateUtils.asLocalDate(subscrCurrentDate.toDate()));

        addContObjectIds.forEach(i -> {
            createSubscrContObjectLink(i, subscriber, subscrBeginDate);
        });

    }



	/**
	 * TODO delete
	 * @param subscriberId
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<ContObjectShortInfo> selectSubscriberContObjectsShortInfo22(Long subscriberId) {
		checkNotNull(subscriberId);

		List<ContObjectShortInfo> result = new ArrayList<>();

		ColumnHelper columnHelper = new ColumnHelper("id", "name", "fullName");

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");
		sqlString.append(ContObject.class.getSimpleName());
		sqlString.append(" c ");
		sqlString.append(" WHERE c.id IN ( ");
		sqlString.append(" SELECT sco.contObjectId FROM ");
		sqlString.append(SubscrContObject.class.getSimpleName());
		sqlString.append(" sco ");
		sqlString.append(" WHERE sco.subscriberId = :subscriberId ");
		sqlString.append(" AND sco.deleted = 0 ");
		sqlString.append(" AND sco.subscrEndDate IS NULL ");
		sqlString.append(" ) ");

		logger.debug("SQL: {}", sqlString.toString());

		Query q1 = em.createQuery(sqlString.toString());

		q1.setParameter("subscriberId", subscriberId);

		List<?> resultRows = q1.getResultList();

		for (Object r : resultRows) {
			if (!(r instanceof Object[])) {
				throw new IllegalStateException();
			}
			Object[] row = (Object[]) r;
			final Long id = columnHelper.getResultAsClass(row, "id", Long.class);
			final String contObjectName = columnHelper.getResultAsClass(row, "name", String.class);
			final String contObjectFullName = columnHelper.getResultAsClass(row, "fullName", String.class);

			ContObjectShortInfo contObjectShortInfo = new ContObjectShortInfo() {

				@Override
				public String getName() {
					return contObjectName;
				}

				@Override
				public String getFullName() {
					return contObjectFullName;
				}

				@Override
				public Long getContObjectId() {
					return id;
				}
			};

			result.add(contObjectShortInfo);

		}

		return result;
	}

}
