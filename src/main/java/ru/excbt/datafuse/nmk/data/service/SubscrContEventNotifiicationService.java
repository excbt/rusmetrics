package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContEvent_;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification_;
import ru.excbt.datafuse.nmk.data.model.support.ContEventNotificationsStatus;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeMonitorStatus;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;

@Service
@Transactional
public class SubscrContEventNotifiicationService {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContEventNotifiicationService.class);

	private final static int DEFAULT_PAGE_SIZE = 100;
	private final static Pageable DEFAULT_NOTIFICATION_PAGE_REQUEST = new PageRequest(
			0, DEFAULT_PAGE_SIZE, makeDefaultSort());

	public final static String[] AVAILABLE_SORT_FIELDS = { "contEventTime" };
	public final static List<String> AVAILABLE_SORT_FIELD_LIST = Collections
			.unmodifiableList(Arrays.asList(AVAILABLE_SORT_FIELDS));

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContEventMonitorService contEventMonitorService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	/**
	 * 
	 * @param subscriberId
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SubscrContEventNotification> selectAll(final long subscriberId,
			final Boolean isNew, final Pageable pageable) {

		Pageable pageRequest = setupPageRequest(pageable);

		Page<SubscrContEventNotification> resultPage = subscrContEventNotificationRepository
				.findAll(Specifications.where(specSubscriberId(subscriberId))
						.and(specIsNew(isNew)), pageRequest);

		return resultPage;

	}

	/**
	 * 
	 * @param subscriberId
	 * @param fromDate
	 * @param toDate
	 * @param contObjectList
	 * @param contObjectTypeList
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SubscrContEventNotification> selectByConditions(
			Long subscriberId, final Date fromDate, final Date toDate,
			final List<Long> contObjectList,
			final List<Long> contEventTypeList, final Boolean isNew,
			final Pageable pageable) {

		checkNotNull(subscriberId);

		Pageable pageRequest = setupPageRequest(pageable);

		Specifications<SubscrContEventNotification> specs = Specifications
				.where(specSubscriberId(subscriberId))
				.and(specContEventDate(fromDate, toDate)).and(specIsNew(isNew))
				.and(specContObjectId(contObjectList))
				.and(specContEventTypeId(contEventTypeList));

		return subscrContEventNotificationRepository
				.findAll(specs, pageRequest);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param datePeriod
	 * @param contObjectList
	 * @param contEventTypeList
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SubscrContEventNotification> selectByConditions(
			Long subscriberId, final DatePeriod datePeriod,
			final List<Long> contObjectList,
			final List<Long> contEventTypeList, final Boolean isNew,
			final Pageable pageable) {

		checkNotNull(subscriberId);

		Pageable pageRequest = setupPageRequest(pageable);

		Specifications<SubscrContEventNotification> specs = Specifications
				.where(specSubscriberId(subscriberId))
				.and(specContEventDate(datePeriod.getDateFrom(),
						datePeriod.getDateTo())).and(specIsNew(isNew))
				.and(specContObjectId(contObjectList))
				.and(specContEventTypeId(contEventTypeList));

		return subscrContEventNotificationRepository
				.findAll(specs, pageRequest);

	}

	/**
	 * 
	 * @return
	 */
	public static Sort makeDefaultSort() {
		return new Sort(Direction.DESC, "contEventTime");
	}

	/**
	 * 
	 * @return
	 */
	public static Sort makeSort(Direction sortDirection) {
		if (sortDirection == null) {
			return makeDefaultSort();
		}
		return new Sort(sortDirection, "contEventTime");
	}

	/**
	 * 
	 * @param sort
	 * @return
	 */
	private static boolean checkSort(Sort sort) {
		checkNotNull(sort);
		boolean result = true;
		Iterator<Order> it = sort.iterator();
		while (it.hasNext()) {
			Order o = it.next();
			result = result
					&& AVAILABLE_SORT_FIELD_LIST.contains(o.getProperty());
		}
		return result;
	}

	/**
	 * 
	 * @param pageable
	 * @return
	 */
	private static Pageable setupPageRequest(Pageable pageable) {
		Pageable result = pageable;

		if (result != null) {
			checkState(checkSort(pageable.getSort()));
		} else {
			result = DEFAULT_NOTIFICATION_PAGE_REQUEST;
		}

		return result;
	}

	/**
	 * 
	 * @param pageable
	 * @param sortDesc
	 * @return
	 */
	public static Pageable setupPageRequestSort(Pageable pageable,
			Boolean sortDesc) {

		checkNotNull(pageable);

		Pageable result;
		if (sortDesc == null || Boolean.TRUE.equals(sortDesc)) {
			result = new PageRequest(pageable.getPageNumber(),
					pageable.getPageSize(), makeSort(Direction.DESC));
		} else {
			result = new PageRequest(pageable.getPageNumber(),
					pageable.getPageSize(), makeSort(Direction.ASC));
		}
		return result;
	}

	/**
	 * 
	 * @param searchTerm
	 * @return
	 */
	public static Specification<SubscrContEventNotification> specIsNew(
			final Boolean isNew) {

		return new Specification<SubscrContEventNotification>() {

			@Override
			public Predicate toPredicate(
					Root<SubscrContEventNotification> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				if (isNew == null) {
					return null;
				}

				return cb.equal(
						root.<Boolean> get(SubscrContEventNotification_.isNew),
						Boolean.TRUE);
			}

		};

	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public static Specification<SubscrContEventNotification> specSubscriberId(
			final Long subscriberId) {

		return new Specification<SubscrContEventNotification>() {

			@Override
			public Predicate toPredicate(
					Root<SubscrContEventNotification> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				if (subscriberId == null) {
					return null;
				}

				return cb.equal(root
						.<Long> get(SubscrContEventNotification_.subscriberId),
						subscriberId);
			}

		};

	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public static Specification<SubscrContEventNotification> specContEventDate(
			final Date fromDate, final Date toDate) {
		return new Specification<SubscrContEventNotification>() {

			@Override
			public Predicate toPredicate(
					Root<SubscrContEventNotification> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				if (fromDate == null || toDate == null) {
					return null;
				}

				return cb
						.and(cb.greaterThanOrEqualTo(
								root.<Date> get(SubscrContEventNotification_.contEventTime),
								fromDate),
								cb.lessThanOrEqualTo(
										root.<Date> get(SubscrContEventNotification_.contEventTime),
										toDate));
			}

		};
	}

	/**
	 * 
	 * @param contObjectIdList
	 * @return
	 */
	public static Specification<SubscrContEventNotification> specContObjectId(
			final List<Long> contObjectIdList) {
		return new Specification<SubscrContEventNotification>() {

			@Override
			public Predicate toPredicate(
					Root<SubscrContEventNotification> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (contObjectIdList == null || contObjectIdList.size() == 0) {
					return null;
				}

				return root.get(SubscrContEventNotification_.contObjectId).in(
						contObjectIdList);
			}

		};
	}

	/**
	 * 
	 * @param contEventTypeIdList
	 * @return
	 */
	public static Specification<SubscrContEventNotification> specContEventTypeId(
			final List<Long> contEventTypeIdList) {
		return new Specification<SubscrContEventNotification>() {

			@Override
			public Predicate toPredicate(
					Root<SubscrContEventNotification> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				if (contEventTypeIdList == null
						|| contEventTypeIdList.size() == 0) {
					return null;
				}

				return root.get(SubscrContEventNotification_.contEvent)
						.get(ContEvent_.contEventType).in(contEventTypeIdList);
			}

		};

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrContEventNotification findOne(Long id) {
		return subscrContEventNotificationRepository.findOne(id);
	}

	/**
	 * 
	 * @param subscrContEventNotificationId
	 * @return
	 */
	public SubscrContEventNotification updateOneIsNew(Boolean isNew,
			Long subscrContEventNotificationId, Long revisionSubscrUserId) {

		checkNotNull(isNew);

		SubscrContEventNotification updateCandidate = subscrContEventNotificationRepository
				.findOne(subscrContEventNotificationId);
		if (updateCandidate == null) {
			throw new PersistenceException(String.format(
					"SubscrContEventNotification with id=%d is not found",
					subscrContEventNotificationId));
		}

		updateCandidate.setIsNew(isNew);
		updateCandidate.setRevisionTime(new Date());
		updateCandidate.setRevisionSubscrUserId(revisionSubscrUserId);
		return subscrContEventNotificationRepository.save(updateCandidate);

	}

	/**
	 * 
	 * @param notificationIds
	 */
	public void updateIsNew(Boolean isNew, List<Long> notificationIds,
			Long revisionSubscrUserId) {
		checkNotNull(isNew);
		checkNotNull(notificationIds);
		checkNotNull(revisionSubscrUserId);
		for (Long id : notificationIds) {
			updateOneIsNew(isNew, id, revisionSubscrUserId);
		}
	}

	/**
	 * 
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public long selectNotificationsCount(final Long subscriberId,
			final Long contObjectId, final DatePeriod datePeriod) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository
				.selectNotificatoinsCount(subscriberId, contObjectId,
						datePeriod.getDateFrom(), datePeriod.getDateTo());

		return result == null ? 0 : result.longValue();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public long selectNotificationsCount(final Long subscriberId,
			final Long contObjectId, final DatePeriod datePeriod, Boolean isNew) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkNotNull(isNew);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository
				.selectNotificatoinsCount(subscriberId, contObjectId,
						datePeriod.getDateFrom(), datePeriod.getDateTo(), isNew);

		return result == null ? 0 : result.longValue();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public long selectContEventTypeCount(final Long subscriberId,
			final Long contObjectId, final DatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> typesList = subscrContEventNotificationRepository
				.selectNotificationEventTypeCount(subscriberId, contObjectId,
						datePeriod.getDateFrom(), datePeriod.getDateTo());

		return typesList.size();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContEventTypeMonitorStatus> selectContEventTypeMonitorStatus(
			final Long subscriberId, final Long contObjectId,
			final DatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> typesList = subscrContEventNotificationRepository
				.selectNotificationEventTypeCount(subscriberId, contObjectId,
						datePeriod.getDateFrom(), datePeriod.getDateTo());

		List<ContEventTypeMonitorStatus> result = new ArrayList<>();
		for (Object[] o : typesList) {
			checkState(o.length == 2);
			Long eventTypeId = null;
			if (o[0] instanceof Long) {
				eventTypeId = (Long) o[0];
			}
			ContEventType contEventType = contEventTypeService
					.findOne(eventTypeId);
			checkNotNull(contEventType);

		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContEventNotificationsStatus> selectSubscrEventNotificationsStatus(
			final Long subscriberId, final DatePeriod datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<ContObject> contObjects = subscriberService
				.selectSubscriberContObjects(subscriberId);

		List<ContEventNotificationsStatus> result = new ArrayList<>();
		for (ContObject co : contObjects) {

			logger.trace(
					"Select EventsStatusData for contObjectId:{}, subscriberId:{}",
					co.getId(), subscriberId);

			long allCnt = selectNotificationsCount(subscriberId, co.getId(),
					datePeriod);

			long newCnt = selectNotificationsCount(subscriberId, co.getId(),
					datePeriod, Boolean.TRUE);

			long typesCnt = selectContEventTypeCount(subscriberId, co.getId(),
					datePeriod);

			ContEventLevelColorKey monitorColorKey = contEventMonitorService
					.findContEventMonitorColor(co.getId());

			ContEventLevelColorKey resultColorKey = monitorColorKey;
			if (resultColorKey == null) {
				resultColorKey = allCnt > 0 ? ContEventLevelColorKey.YELLOW
						: ContEventLevelColorKey.GREEN;
			}

			ContEventNotificationsStatus item = ContEventNotificationsStatus
					.newInstance(co);

			item.setTotalCount(allCnt);
			item.setTotalNewCount(newCnt);
			item.setTotalTypesCount(typesCnt);
			item.setContEventLevelColorKey(resultColorKey);

			result.add(item);
		}

		return result;
	}

}
