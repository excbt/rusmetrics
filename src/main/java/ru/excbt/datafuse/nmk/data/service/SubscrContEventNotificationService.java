package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

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

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification_;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfo;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.service.utils.DBSpecUtil;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * Сервис для работы с уведомлениями для абонентов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
@Service
public class SubscrContEventNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationService.class);

	private final static int DEFAULT_PAGE_SIZE = 100;
	private final static Pageable DEFAULT_NOTIFICATION_PAGE_REQUEST = new PageRequest(0, DEFAULT_PAGE_SIZE,
			makeDefaultSort());

	public final static String[] AVAILABLE_SORT_FIELDS = { "contEventTime", "id" };
	public final static List<String> AVAILABLE_SORT_FIELD_LIST = Collections
			.unmodifiableList(Arrays.asList(AVAILABLE_SORT_FIELDS));

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	@Autowired
	private ContEventService contEventService;

	/**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since dd.02.2016
	 *
	 */
	public static class SearchConditions {

		private final long subscriberId;
		private final DateInterval dateInterval;
		private final Boolean isNew;
		private final List<Long> contObjectIdList = new ArrayList<>();
		private final List<Long> contEventTypeList = new ArrayList<>();
		private final List<String> contEventCategoryList = new ArrayList<>();
		private final List<String> contEventDeviationList = new ArrayList<>();

		public SearchConditions(long subscriberId, DateInterval dateInterval) {
			this.subscriberId = subscriberId;
			this.dateInterval = dateInterval;
			this.isNew = null;
		}

		public SearchConditions(long subscriberId, DateInterval dateInterval, Boolean isNew) {
			this.subscriberId = subscriberId;
			this.dateInterval = dateInterval;
			this.isNew = isNew;
		}

		public void initContObjectIds(List<Long> contObjectIdList) {
			this.contObjectIdList.clear();
			if (contObjectIdList != null) {
				this.contObjectIdList.addAll(contObjectIdList);
			}
		}

		public void initContEventCategories(List<String> contEventCategoryList) {
			this.contEventCategoryList.clear();
			if (contEventCategoryList != null) {
				this.contEventCategoryList.addAll(contEventCategoryList);
			}
		}

		public void initContEventTypes(List<Long> contEventTypeList) {
			this.contEventTypeList.clear();
			if (contEventTypeList != null) {
				this.contEventTypeList.addAll(contEventTypeList);
			}
		}

		public void initContEventDeviations(List<String> contEventDeviationList) {
			this.contEventDeviationList.clear();
			if (contEventDeviationList != null) {
				this.contEventDeviationList.addAll(contEventDeviationList);
			}
		}

		public void initContEventDeviations(String[] contEventDeviationList) {
			this.contEventDeviationList.clear();
			if (contEventDeviationList != null) {
				this.contEventDeviationList.addAll(Arrays.asList(contEventDeviationList));
			}
		}

		public long getSubscriberId() {
			return subscriberId;
		}

		public DateInterval getPeriod() {
			return dateInterval;
		}

		public Boolean getIsNew() {
			return isNew;
		}
	}

	/**
	 *
	 * @param subscriberId
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<SubscrContEventNotification> selectAll(final long subscriberId, final Boolean isNew,
			final Pageable pageable) {

		Pageable pageRequest = setupPageRequest(pageable);

		Page<SubscrContEventNotification> resultPage = subscrContEventNotificationRepository
				.findAll(Specifications.where(specSubscriberId(subscriberId)).and(specIsNew(isNew)), pageRequest);

		initContEvent(resultPage.getContent());
		contEventService.loadContEventTypeModel(resultPage.getContent());

		return resultPage;

	}

	/**
	 *
	 * @param specs
	 * @return
	 */
	//	private <T> Specifications<T> andFilterBuild(List<Specification<T>> specList) {
	//		if (specList == null) {
	//			return null;
	//		}
	//		Specifications<T> result = null;
	//		for (Specification<T> i : specList) {
	//			if (i == null) {
	//				continue;
	//			}
	//			result = result == null ? Specifications.where(i) : result.and(i);
	//		}
	//
	//		return result;
	//	}

	/**
	 *
	 * @param searchConditions
	 * @param pageable
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<SubscrContEventNotification> selectNotificationByConditions(SearchConditions searchConditions,
			final Pageable pageable) {

		checkNotNull(searchConditions.subscriberId);

		Pageable pageRequest = setupPageRequest(pageable);

		List<Specification<SubscrContEventNotification>> andFilter = Arrays.asList( //
				specSubscriberId(searchConditions.subscriberId), //
				specContEventDate(searchConditions.dateInterval), //
				specIsNew(searchConditions.isNew), //
				specContObjectId(searchConditions.contObjectIdList), //
				specContEventTypeId(searchConditions.contEventTypeList), //
				specContEventCategory(searchConditions.contEventCategoryList), //
				specContEventDevation(searchConditions.contEventDeviationList));

		Specifications<SubscrContEventNotification> specs = DBSpecUtil.specsAndFilterBuild(andFilter);

		Page<SubscrContEventNotification> result = subscrContEventNotificationRepository.findAll(specs, pageRequest);

		initContEvent(result.getContent());
		contEventService.loadContEventTypeModel(result.getContent());

		return result;

	}

    /**
     *
     * @param subscriberParam
     * @param dateInterval
     * @param contObjectList
     * @param contEventTypeList
     * @param isNew
     * @param revisionIsNew
     * @param revisionSubscrUserId
     */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateRevisionByConditions(final SubscriberParam subscriberParam, final DateInterval dateInterval,
			final List<Long> contObjectList, final List<Long> contEventTypeList, final Boolean isNew,
			final Boolean revisionIsNew, Long revisionSubscrUserId) {

		checkNotNull(subscriberParam);

		Specifications<SubscrContEventNotification> specs = Specifications
				.where(specSubscriberId(subscriberParam.getSubscriberId()))
				.and(specContEventDate(dateInterval.getFromDate(), dateInterval.getToDate())).and(specIsNew(isNew))
				.and(specContObjectId(contObjectList)).and(specContEventTypeId(contEventTypeList));

		Iterable<SubscrContEventNotification> updateCandidates = subscrContEventNotificationRepository.findAll(specs);
		for (SubscrContEventNotification n : updateCandidates) {
			updateNotificationRevision(subscriberParam, n, revisionIsNew);
		}
	}

    /**
     *
     * @param subscriberParam
     * @param datePeriod
     * @param contObjectIds
     * @param contEventTypeIds
     * @param revisionIsNew
     */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateRevisionByConditionsFast(SubscriberParam subscriberParam, final DateInterval datePeriod,
			final List<Long> contObjectIds, final List<Long> contEventTypeIds, final Boolean revisionIsNew) {

		checkNotNull(subscriberParam);

		if ((contObjectIds == null || contObjectIds.isEmpty())
				&& (contEventTypeIds == null || contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(subscriberParam.getSubscriberId(),
					subscriberParam.getSubscrUserId());
		} else // another case
		if ((contObjectIds != null && !contObjectIds.isEmpty())
				&& (contEventTypeIds == null || contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(subscriberParam.getSubscriberId(),
					subscriberParam.getSubscrUserId(), contObjectIds);
		} else // another case
		if ((contObjectIds != null && !contObjectIds.isEmpty())
				&& (contEventTypeIds != null && !contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(subscriberParam.getSubscriberId(),
					subscriberParam.getSubscrUserId(), contObjectIds, contEventTypeIds);
		}

	}

	/**
	 *
	 * @return
	 */
	private static Sort makeDefaultSort() {
		return new Sort(new Order(Direction.DESC, "contEventTime"), new Order(Direction.DESC, "id"));
	}

	/**
	 *
	 * @return
	 */
	private static Sort makeSort(Direction sortDirection) {
		if (sortDirection == null) {
			return makeDefaultSort();
		}
		return new Sort(new Order(sortDirection, "contEventTime"), new Order(sortDirection, "id"));
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
			result = result && AVAILABLE_SORT_FIELD_LIST.contains(o.getProperty());
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
	public static Pageable setupPageRequestSort(Pageable pageable, Boolean sortDesc) {

		checkNotNull(pageable);

		Pageable result;
		if (sortDesc == null || Boolean.TRUE.equals(sortDesc)) {
			result = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), makeSort(Direction.DESC));
		} else {
			result = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), makeSort(Direction.ASC));
		}
		return result;
	}

    /**
     *
     * @param isNew
     * @return
     */
	private static Specification<SubscrContEventNotification> specIsNew(final Boolean isNew) {
		return (root, query, cb) -> {
			if (isNew == null) {
				return null;
			}
			return cb.equal(root.<Boolean> get(SubscrContEventNotification_.isNew), Boolean.TRUE);
		};

	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specSubscriberId(final Long subscriberId) {
		return (root, query, cb) -> {
			if (subscriberId == null) {
				return null;
			}
			return cb.equal(root.get(SubscrContEventNotification_.subscriberId), subscriberId);
		};
	}

    /**
     *
     * @param fromDate
     * @param toDate
     * @return
     */
	private static Specification<SubscrContEventNotification> specContEventDate(final Date fromDate,
			final Date toDate) {
		return (root, query, cb) -> {
			if (fromDate == null || toDate == null) {
				return null;
			}
			return cb.and(
					cb.greaterThanOrEqualTo(root.get(SubscrContEventNotification_.contEventTime), LocalDateUtils.asLocalDateTime(fromDate)),
					cb.lessThanOrEqualTo(root.get(SubscrContEventNotification_.contEventTime), LocalDateUtils.asLocalDateTime(toDate)));
		};
	}

    /**
     *
     * @param interval
     * @return
     */
	private static Specification<SubscrContEventNotification> specContEventDate(final DateInterval interval) {
		return (root, query, cb) -> {
			if (interval == null || interval.isInvalidEq()) {
				return null;
			}
			return cb.and(
					cb.greaterThanOrEqualTo(root.get(SubscrContEventNotification_.contEventTime),
                        LocalDateUtils.asLocalDateTime(interval.getFromDate())),
					cb.lessThanOrEqualTo(root.get(SubscrContEventNotification_.contEventTime),
                        LocalDateUtils.asLocalDateTime(interval.getToDate())));
		};
	}

	/**
	 *
	 * @param contObjectIdList
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specContObjectId(final List<Long> contObjectIdList) {
		return (root, query, cb) -> {
			if (contObjectIdList == null || contObjectIdList.size() == 0) {
				return null;
			}
			return root.get(SubscrContEventNotification_.contObjectId).in(contObjectIdList);
		};
	}

	/**
	 *
	 * @param contEventTypeIdList
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specContEventTypeId(
			final List<Long> contEventTypeIdList) {
		return (root, query, cb) -> {
			if (contEventTypeIdList == null || contEventTypeIdList.size() == 0) {
				return null;
			}
			//return root.get(SubscrContEventNotification_.contEvent).get(ContEvent_.contEventType)
			//		.in(contEventTypeIdList);
			return root.get(SubscrContEventNotification_.contEventTypeId).in(contEventTypeIdList);
		};
	}

	/**
	 *
	 * @param contEventCategoryList
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specContEventCategory(
			final List<String> contEventCategoryList) {
		return (root, query, cb) -> {
			if (contEventCategoryList == null || contEventCategoryList.size() == 0) {
				return null;
			}

			return cb.or(root.get(SubscrContEventNotification_.contEventCategoryKeyname).in(contEventCategoryList));
			//. (ContEvent_.contEventType)
			//.get(ContEventType_.contEventCategory).in(contEventCategoryList));
			//return cb.or(root.get(SubscrContEventNotification_.contEvent).get(ContEvent_.contEventType)
			//		.get(ContEventType_.contEventCategory).in(contEventCategoryList));
		};
	}

	private static Specification<SubscrContEventNotification> specContEventDevation(
			final List<String> contEventDeviationList) {
		return (root, query, cb) -> {
			if (contEventDeviationList == null || contEventDeviationList.size() == 0) {
				return null;
			}
			return cb.or(root.get(SubscrContEventNotification_.contEventDeviationKeyname).in(contEventDeviationList));
			//			return cb.or(root.get(SubscrContEventNotification_.contEvent).get(ContEvent_.contEventDeviationKeyname)
			//					.in(contEventDeviationList));
		};
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrContEventNotification findNotification(Long id) {
		SubscrContEventNotification result = subscrContEventNotificationRepository.findOne(id);
		initContEvent(result);
		return result;
	}

	/**
	 *
	 * @param subscrContEventNotificationId
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContEventNotification updateNotificationRevision(final SubscriberParam subscriberParam,
			final Boolean isNew, final Long subscrContEventNotificationId) {

		checkNotNull(isNew);

		SubscrContEventNotification updateCandidate = subscrContEventNotificationRepository
				.findOne(subscrContEventNotificationId);
		if (updateCandidate == null) {
			throw new PersistenceException(String.format("SubscrContEventNotification with id=%d is not found",
					subscrContEventNotificationId));
		}

		return updateNotificationRevision(subscriberParam, updateCandidate, isNew);

	}

    /**
     *
     * @param subscriberParam
     * @param subscrContEventNotification
     * @param isNew
     * @return
     */
	@Deprecated
	//@Transactional(value = TxConst.TX_DEFAULT)
	private SubscrContEventNotification updateNotificationRevision(SubscriberParam subscriberParam,
			SubscrContEventNotification subscrContEventNotification, Boolean isNew) {

		checkNotNull(subscrContEventNotification);
		subscrContEventNotification.setIsNew(isNew);
        ZonedDateTime revisionDate = ZonedDateTime.now();


		subscrContEventNotification.setRevisionTime(revisionDate.toLocalDateTime());
		subscrContEventNotification.setRevisionTimeTZ(revisionDate);
		subscrContEventNotification.setRevisionSubscrUserId(subscriberParam.getSubscrUserId());
		SubscrContEventNotification result = subscrContEventNotificationRepository.save(subscrContEventNotification);
		initContEvent(result);
		return result;
	}

    /**
     *
     * @param subscriberParam
     * @param notificationIds
     * @param isNew
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> updateNotificationsRevisions(SubscriberParam subscriberParam, List<Long> notificationIds,
			Boolean isNew) {

		if (notificationIds != null && !notificationIds.isEmpty()) {
			subscrContEventNotificationRepository.updateSubscriberRevisions(subscriberParam.getSubscriberId(),
					subscriberParam.getSubscrUserId(), notificationIds, isNew);

		}

		return notificationIds;
	}

	/**
	 *
	 * @param notificationIds
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateNotificationRevision(SubscriberParam subscriberParam, Boolean isNew, List<Long> notificationIds) {
		checkNotNull(isNew);
		checkNotNull(notificationIds);
		checkNotNull(subscriberParam);
		for (Long id : notificationIds) {
			updateNotificationRevision(subscriberParam, isNew, id);
		}
	}

	/**
	 *
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public long selectNotificationsCount(final Long subscriberId, final Long contObjectId,
			final DateInterval datePeriod) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository.selectNotificatoinsCount(subscriberId, contObjectId,
				datePeriod.getFromDate(), datePeriod.getToDate());

		return result == null ? 0 : result.longValue();
	}

	/**
	 *
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public long selectNotificationsCount(final Long subscriberId, final Long contObjectId,
			final DateInterval datePeriod, Boolean isNew) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkNotNull(isNew);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository.selectNotificatoinsCount(subscriberId, contObjectId,
				datePeriod.getFromDate(), datePeriod.getToDate(), isNew);

		return result == null ? 0 : result.longValue();
	}

	/**
	 *
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public long selectContEventTypeCountGroup(final Long subscriberId, final Long contObjectId,
			final DateInterval datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> typesList = subscrContEventNotificationRepository.selectNotificationEventTypeCountGroup(
				subscriberId, contObjectId, datePeriod.getFromDate(), datePeriod.getToDate());

		return typesList.size();
	}

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CounterInfo> selectContEventNotificationCounterInfo(final Long subscriberId,
			final List<Long> contObjectIds, final DateInterval datePeriod) {
		return selectContEventNotificationCounterInfo(subscriberId, contObjectIds, datePeriod, null);
	}

	/**
	 *
     * ****************
     * **** Actual ****
     * ****************
	 * Selects ContEvent notifications count by array of contObjectIds
     *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @param isNew
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CounterInfo> selectContEventNotificationCounterInfo(final Long subscriberId,
                                                                    final List<Long> contObjectIds, final DateInterval datePeriod, Boolean isNew) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		if (contObjectIds == null || contObjectIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<Object[]> selectResult = null;
		if (isNew == null) {
			selectResult = subscrContEventNotificationRepository.selectContObjectNotificatoinsCountList(subscriberId,
					contObjectIds, datePeriod.getFromDate(), datePeriod.getToDate());
		} else {
			selectResult = subscrContEventNotificationRepository.selectContObjectNotificatoinsCountList(subscriberId,
					contObjectIds, datePeriod.getFromDate(), datePeriod.getToDate(), isNew);
		}

		checkNotNull(selectResult);

		// objects[0] - contObjectId
        // objects[1] - count(*)
		List<CounterInfo> resultList = selectResult.stream()
				.map((objects) -> new CounterInfo (
                                            DBRowUtil.asLong(objects[0]),
                                            DBRowUtil.asLong(objects[1]),
                                            CounterInfo.IdRole.CONT_OBJECT)
                ).collect(Collectors.toList());

		return resultList;
	}

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CounterInfo> selectContObjectEventTypeGroupCounterInfo(final Long subscriberId,
			final List<Long> contObjectIds, final DateInterval datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCountGroup(
				subscriberId, contObjectIds, datePeriod.getFromDate(), datePeriod.getToDate());

		List<CounterInfo> resultList = selectResult.stream()
				.map((objects) -> CounterInfo.newInstance(objects[0], objects[1])).collect(Collectors.toList());

		return resultList;
	}

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CounterInfo> selectContObjectEventTypeGroupCollapseCounterInfo(final Long subscriberId,
			final List<Long> contObjectIds, final DateInterval datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository
				.selectNotificationEventTypeCountGroupCollapse(subscriberId, contObjectIds, datePeriod.getFromDate(),
						datePeriod.getToDate());

		List<CounterInfo> resultList = selectResult.stream()
				.map((objects) -> new CounterInfo(
                                DBRowUtil.asLong(objects[0]),
                                DBRowUtil.asLong(objects[1]),
                                CounterInfo.IdRole.CONT_OBJECT)
                ).collect(Collectors.toList());

		return resultList;
	}

	/**
	 *
	 * @param notifications
	 */
	private void initContEvent(Collection<SubscrContEventNotification> notifications) {

		Collection<Long> contEventIds = notifications.stream().map(i -> i.getContEventId()).collect(Collectors.toSet());
		List<ContEvent> contEvents = contEventService.selectContEventsByIds(contEventIds);

		final Map<Long, ContEvent> contEventsMap = contEvents.stream()
				.collect(Collectors.toMap(ContEvent::getId, Function.identity()));

		notifications.forEach(i -> {
			i.setContEvent(contEventsMap.get(i.getContEventId()));
		});

	}

	/**
	 *
	 * @param notification
	 */
	private void initContEvent(SubscrContEventNotification notification) {
		initContEvent(Arrays.asList(notification));
	}

}
