package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObject_;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject_;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.ContZPointService.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.ColumnHelper;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

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
	@Autowired
	private SubscrContObjectRepository subscrContObjectRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	protected ContGroupService contGroupService;

	/**
	 * TODO delete. // Comment date 11.05.2016
	 * 
	 * @param objects
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT)
	private void deleteOne2(List<SubscrContObject> objects) {
		checkNotNull(objects);

		objects.forEach(i -> {
			i.setDeleted(1);
			if (i.getSubscrEndDate() == null && i.getSubscriberId() != null) {
				Date subscrDate = subscriberService.getSubscriberCurrentTime(i.getSubscriberId());
				i.setSubscrEndDate(subscrDate);
			}
		});

		subscrContObjectRepository.save(objects);
	}

	/**
	 * 
	 * @param objects
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteSubscrContObject(List<SubscrContObject> objects, LocalDate subscrEndDate) {
		checkNotNull(objects);
		checkNotNull(subscrEndDate);
		Date endDate = subscrEndDate.toDate();

		List<SubscrContObject> updateCandidate = new ArrayList<>();
		objects.forEach(i -> {
			if (i.getSubscrEndDate() == null) {
				i.setSubscrEndDate(endDate);
				updateCandidate.add(i);
			}
		});
		subscrContObjectRepository.save(updateCandidate);
	}

	/**
	 * 
	 * @param subscrContObject
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteSubscrContObject(SubscrContObject subscrContObject, LocalDate subscrEndDate) {
		checkNotNull(subscrContObject);
		if (subscrContObject.getSubscrBeginDate().equals(subscrContObject.getSubscrEndDate())) {
			subscrContObjectRepository.delete(subscrContObject);
		} else {
			subscrContObject.setSubscrEndDate(subscrEndDate.toDate());
			subscrContObjectRepository.save(subscrContObject);
		}
	}

	/**
	 * 
	 * @param objects
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	public void deleteSubscrContObjectPermanent(List<SubscrContObject> objects) {
		subscrContObjectRepository.delete(objects);
	}

	/**
	 * 
	 * @param subscrContObject
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteSubscrContObjectPermanent(SubscrContObject subscrContObject) {
		subscrContObjectRepository.delete(subscrContObject);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContObject> selectByContObjectId(Long contObjectId) {
		return subscrContObjectRepository.findByContObjectId(contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber selectRmaByContObjectId(Long contObjectId) {
		List<SubscrContObject> subscrContObjects = subscrContObjectRepository.findByContObjectId(contObjectId);
		List<SubscrContObject> rmaList = subscrContObjects.stream()
				.filter(i -> Boolean.TRUE.equals(i.getSubscriber().getIsRma())).collect(Collectors.toList());
		if (rmaList.size() > 1) {
			logger.error("ContObject (id={}) has more than one RMA", contObjectId);
		}
		return rmaList.isEmpty() ? null : rmaList.get(0).getSubscriber();
	}

	/**
	 * 
	 * @param subscrContObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public SubscrContObject saveOne(SubscrContObject subscrContObject) {
		return subscrContObjectRepository.save(subscrContObject);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectRmaSubscrContObjectIds(Long subscriberId) {
		checkNotNull(subscriberId);
		LocalDate currentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);
		return subscrContObjectRepository.selectRmaSubscribersContObjectIds(subscriberId, currentDate.toDate());
	}

	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectRmaSubscribersContObjectIds(SubscriberParam subscriberParam) {
		return selectRmaSubscribersContObjectIds(subscriberParam, false);
	}

	/**
	 * 
	 * @param subscriberParam
	 * @param currentDateFilter
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectRmaSubscribersContObjectIds(SubscriberParam subscriberParam, boolean currentDateFilter) {
		checkNotNull(subscriberParam);
		checkState(subscriberParam.isRma());
		if (currentDateFilter) {
			LocalDate currentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberParam.getSubscriberId());
			return subscrContObjectRepository.selectRmaSubscribersContObjectIds(subscriberParam.getSubscriberId(),
					currentDate.toDate());
		}
		return subscrContObjectRepository.selectRmaSubscribersContObjectIds(subscriberParam.getSubscriberId());
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContObject> result = subscrContObjectRepository.selectContObjects(subscriberId);
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriber
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(SubscriberParam subscriberParam) {
		checkNotNull(subscriberParam);
		List<ContObject> result = subscrContObjectRepository.selectContObjects(subscriberParam.getSubscriberId());
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriberParam
	 * @param contGroupId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(SubscriberParam subscriberParam, Long contGroupId) {
		checkNotNull(subscriberParam);

		List<ContObject> result = EMPTY_CONT_OBJECTS_LIST;

		if (contGroupId == null) {
			result = subscrContObjectRepository.selectContObjects(subscriberParam.getSubscriberId());
		} else {
			result = contGroupService.selectContGroupObjects(subscriberParam, contGroupId);
		}

		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(Long subscriberId, List<Long> contObjectIds) {
		checkNotNull(subscriberId);
		List<ContObject> result = subscrContObjectRepository.selectContObjectsByIds(subscriberId, contObjectIds);
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriberParam
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(SubscriberParam subscriberParam, List<Long> contObjectIds) {
		checkNotNull(subscriberParam);
		List<ContObject> result = subscrContObjectRepository.selectContObjectsByIds(subscriberParam.getSubscriberId(),
				contObjectIds);
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjectsNoSort(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContObject> result = subscrContObjectRepository.selectContObjectsNoSort(subscriberId);
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectShortInfo> selectSubscriberContObjectsShortInfo2(Long subscriberId) {
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

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectShortInfo> selectSubscriberContObjectsShortInfo(Long subscriberId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Subquery<Long> subq = q.subquery(Long.class);

		Root<ContObject> co = q.from(ContObject.class);
		Root<SubscrContObject> sco = subq.from(SubscrContObject.class);

		ParameterExpression<Long> nameParameter = cb.parameter(Long.class, "subscriberId");

		subq.select(sco.get(SubscrContObject_.contObjectId)).where(cb.equal(sco.get(SubscrContObject_.deleted), 0),
				sco.get(SubscrContObject_.subscrEndDate).isNull(),
				cb.equal(sco.get(SubscrContObject_.subscriberId), nameParameter));

		ColumnHelper columnHelper = new ColumnHelper(ContObject_.id, ContObject_.name, ContObject_.fullName);

		q.select(cb.tuple(columnHelper.getSelection(co))).where(cb.equal(co.get(ContObject_.deleted), 0),
				cb.in(co.get(ContObject_.id)).value(subq));

		//		q.select(cb.tuple(co.get(ContObject_.id), co.get(ContObject_.name), co.get(ContObject_.fullName)))
		//				.where(cb.equal(co.get(ContObject_.deleted), 0), cb.in(co.get(ContObject_.id)).value(subq));

		List<Tuple> resultTuples = em.createQuery(q).setParameter("subscriberId", subscriberId).getResultList();

		List<ContObjectShortInfo> result = new ArrayList<>();

		for (Tuple t : resultTuples) {

			//columnHelper.indexOf(column)

			final Long id = columnHelper.getTupleValue(t, ContObject_.id);
			final String contObjectName = columnHelper.getTupleValue(t, ContObject_.name);
			final String contObjectFullName = columnHelper.getTupleValue(t, ContObject_.fullName);

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

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContObject> selectSubscrContObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		List<SubscrContObject> result = subscrContObjectRepository.selectSubscrContObjects(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjectsExcludingIds(Long subscriberId, List<Long> contObjectIds) {
		checkNotNull(subscriberId);
		List<ContObject> result = null;
		if (contObjectIds.isEmpty()) {
			result = subscrContObjectRepository.selectContObjects(subscriberId);
		} else {
			result = subscrContObjectRepository.selectContObjectsExcludingIds(subscriberId, contObjectIds);
		}
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectRmaSubscriberContObjects(Long rmaSubscriberId) {
		checkNotNull(rmaSubscriberId);
		List<ContObject> result = selectSubscriberContObjects(rmaSubscriberId);
		rmaInitHaveSubscr(rmaSubscriberId, result);

		return result;
	}

	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectRmaSubscriberContObjects(SubscriberParam subscriberParam) {
		checkNotNull(subscriberParam);
		List<ContObject> result = selectSubscriberContObjects(subscriberParam);
		rmaInitHaveSubscr(subscriberParam, result);

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectRmaSubscriberContObjectsExcludingIds(Long rmaSubscriberId, List<Long> contObjectIds) {
		checkNotNull(rmaSubscriberId);
		checkNotNull(contObjectIds);

		List<ContObject> result = selectSubscriberContObjectsExcludingIds(rmaSubscriberId, contObjectIds);
		rmaInitHaveSubscr(rmaSubscriberId, result);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectSubscriberContObjectIds(Long subscriberId) {
		checkNotNull(subscriberId);
		List<Long> result = subscrContObjectRepository.selectContObjectIds(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public int selectSubscriberContObjectCount(Long subscriberId) {
		checkNotNull(subscriberId);
		List<Long> result = subscrContObjectRepository.selectContObjectIds(subscriberId);
		return result.size();
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContObjectSubscription(Long subscriberId, Long contObjectId) {
		checkNotNull(subscriberId);
		checkNotNull(contObjectId);
		List<Long> resultIds = subscrContObjectRepository.selectContObjectId(subscriberId, contObjectId);
		return resultIds.size() > 0;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPoint> selectSubscriberContZPoints(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContZPoint> result = subscrContObjectRepository.selectContZPoints(subscriberId);
		result.forEach(i -> {
			i.getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});
		});
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectSubscriberContZPointIds(Long subscriberId) {
		checkNotNull(subscriberId);
		List<Long> result = subscrContObjectRepository.selectContZPointIds(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointShortInfo> selectSubscriberContZPointShortInfo(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContZPointShortInfo> result = new ArrayList<>();

		String[] QUERY_COLUMNS = new String[] { "id", "contObjectId", "customServiceName", "contServiceTypeKeyname",
				"caption" };

		ColumnHelper columnHelper = new ColumnHelper(QUERY_COLUMNS);

		List<Object[]> queryResult = subscrContObjectRepository.selectContZPointShortInfo(subscriberId);

		for (Object[] row : queryResult) {

			Long contZPointId = columnHelper.getResultAsClass(row, "id", Long.class);
			Long contObjectId = columnHelper.getResultAsClass(row, "contObjectId", Long.class);
			String customServiceName = columnHelper.getResultAsClass(row, "customServiceName", String.class);
			String contServiceType = columnHelper.getResultAsClass(row, "contServiceTypeKeyname", String.class);
			String contServiceTypeCaption = columnHelper.getResultAsClass(row, "caption", String.class);
			ContZPointShortInfo info = new ContZPointShortInfo(contZPointId, contObjectId, customServiceName,
					contServiceType, contServiceTypeCaption);
			result.add(info);
		}

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObject> selectDeviceObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		return subscrContObjectRepository.selectDeviceObjects(subscriberId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectAvailableContObjects(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);
		return subscrContObjectRepository.selectAvailableContObjects(subscriberId, rmaSubscriberId);
	}

	/**
	 * 
	 * @param contObject
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public SubscrContObject createSubscrContObject(ContObject contObject, Subscriber subscriber,
			LocalDate subscrBeginDate) {
		checkNotNull(contObject);
		checkNotNull(subscriber);
		checkNotNull(subscrBeginDate);

		SubscrContObject subscrContObject = new SubscrContObject();
		subscrContObject.setContObject(contObject);
		subscrContObject.setSubscriber(subscriber);
		subscrContObject.setSubscrBeginDate(subscrBeginDate.toDate());
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
	public SubscrContObject createSubscrContObject(Long contObjectId, Subscriber subscriber,
			LocalDate subscrBeginDate) {
		checkNotNull(contObjectId);
		checkNotNull(subscriber);
		checkNotNull(subscrBeginDate);

		ContObject contObject = contObjectService.findContObject(contObjectId);

		SubscrContObject subscrContObject = new SubscrContObject();
		subscrContObject.setContObject(contObject);
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
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	public List<ContObject> updateSubscrContObjects(Long subscriberId, List<Long> contObjectIds,
			LocalDate subscrBeginDate) {

		LocalDate subscrCurrentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);
		Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);

		if (subscrCurrentDate.isBefore(subscrBeginDate)) {
			throw new PersistenceException(
					String.format("Subscriber (id=%d) Subscr Current Date is before subscrBeginDate ", subscriberId));
		}

		List<Long> currentContObjectsIds = selectSubscriberContObjectIds(subscriberId);

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

		deleteSubscrContObject(delSubscrContObjects, subscrCurrentDate);

		addContObjectIds.forEach(i -> {
			createSubscrContObject(i, subscriber, subscrBeginDate);
		});

		List<ContObject> resultContObjects = selectSubscriberContObjects(subscriberId);
		resultContObjects.forEach(i -> {
			i.getId();
		});

		return resultContObjects;
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param contObjects
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public void rmaInitHaveSubscr(final Long rmaSubscriberId, final List<ContObject> contObjects) {
		List<Long> subscrContObjectIds = selectRmaSubscrContObjectIds(rmaSubscriberId);

		Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
		contObjects.forEach(i -> {
			boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
			i.set_haveSubscr(haveSubscr);
		});

	}

	/**
	 * 
	 * @param subscriberParam
	 * @param contObjects
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public void rmaInitHaveSubscr(final SubscriberParam subscriberParam, final List<ContObject> contObjects) {
		checkNotNull(subscriberParam);
		checkNotNull(contObjects);

		if (!subscriberParam.isRma()) {
			return;
		}

		List<Long> subscrContObjectIds = selectRmaSubscribersContObjectIds(subscriberParam);

		Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
		contObjects.forEach(i -> {
			boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
			i.set_haveSubscr(haveSubscr);
		});

	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean canAccessContObjects(Long subscriberId, Long[] contObjectIds) {

		if (contObjectIds == null || contObjectIds.length == 0) {
			return false;
		}

		List<Long> subscrContObjectIds = selectSubscriberContObjectIds(subscriberId);

		return checkIds(contObjectIds, subscrContObjectIds);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contZPointIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean canAccessContZPoint(Long subscriberId, Long[] contZPointIds) {
		if (contZPointIds == null || contZPointIds.length == 0) {
			return false;
		}

		List<Long> availableIds = selectSubscriberContZPointIds(subscriberId);

		return checkIds(contZPointIds, availableIds);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectContObjectSubscriberIdsByRma(Long rmaSubscriberId, Long contObjectId) {
		return subscrContObjectRepository.selectContObjectSubscriberIdsByRma(rmaSubscriberId, contObjectId);
	}

}
