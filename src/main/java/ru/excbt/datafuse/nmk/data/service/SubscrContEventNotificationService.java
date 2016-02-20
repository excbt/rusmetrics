package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContEventType_;
import ru.excbt.datafuse.nmk.data.model.ContEvent_;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification_;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjects;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatus;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventNotificationStatus;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventTypeStatus;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventLevelColorRepository;

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

	public final static String[] AVAILABLE_SORT_FIELDS = { "contEventTime" };
	public final static List<String> AVAILABLE_SORT_FIELD_LIST = Collections
			.unmodifiableList(Arrays.asList(AVAILABLE_SORT_FIELDS));

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	// @Autowired
	// private SubscriberService subscriberService;

	@Autowired
	private ContEventMonitorService contEventMonitorService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	@Autowired
	private ContEventLevelColorRepository contEventLevelColorRepository;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	private static class CounterInfo {
		private final Long id;
		private final Long count;

		private CounterInfo(Long id, Long count) {
			this.id = id;
			this.count = count;
		}

		private static CounterInfo newInstance(Object id, Object count) {

			if (id instanceof Long && count instanceof Long) {
				return new CounterInfo((Long) id, (Long) count);

			} else if (id instanceof Number && count instanceof Number) {

				long idValue = ((Number) id).longValue();
				long countValue = ((Number) count).longValue();
				return new CounterInfo(idValue, countValue);

			}

			throw new IllegalArgumentException("Can't determine type for CounterInfo arguments ");

		}

		public Long getContObjectId() {
			return id;
		}

		public Long getCount() {
			return count;
		}

	}

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	private class ContObjectCounterMap {
		private final Map<Long, CounterInfo> notificationMap;

		private ContObjectCounterMap(List<CounterInfo> srcList) {
			checkNotNull(srcList);
			this.notificationMap = srcList.stream()
					.collect(Collectors.toMap(CounterInfo::getContObjectId, (info) -> info));
		}

		private long getCountValue(Long contObjectId) {
			CounterInfo info = notificationMap.get(contObjectId);
			return (info == null) || (info.count == null) ? 0 : info.count.longValue();
		}
	}

	public static class SearchConditions {

		private final long subscriberId;
		private final LocalDatePeriod period;
		private final Boolean isNew;
		private final List<Long> contObjectIdList = new ArrayList<>();
		private final List<Long> contEventTypeList = new ArrayList<>();
		private final List<String> contEventCategoryList = new ArrayList<>();

		public SearchConditions(long subscriberId, LocalDatePeriod period) {
			this.subscriberId = subscriberId;
			this.period = period;
			this.isNew = null;
		}

		public SearchConditions(long subscriberId, LocalDatePeriod period, Boolean isNew) {
			this.subscriberId = subscriberId;
			this.period = period;
			this.isNew = isNew;
		}

		public void initContObjectIds(List<Long> contObjectIdList) {
			if (contObjectIdList != null) {
				this.contObjectIdList.clear();
				this.contObjectIdList.addAll(contObjectIdList);
			}
		}

		public void initContEventCategories(List<String> contEventCategoryList) {
			if (contEventCategoryList != null) {
				this.contEventCategoryList.clear();
				this.contEventCategoryList.addAll(contEventCategoryList);
			}
		}

		public void initContEventTypes(List<Long> contEventTypeList) {
			if (contEventTypeList != null) {
				this.contEventTypeList.clear();
				this.contEventTypeList.addAll(contEventTypeList);
			}
		}

		public long getSubscriberId() {
			return subscriberId;
		}

		public LocalDatePeriod getPeriod() {
			return period;
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<SubscrContEventNotification> selectByConditions(Long subscriberId, final Date fromDate,
			final Date toDate, final List<Long> contObjectList, final List<Long> contEventTypeList, final Boolean isNew,
			final Pageable pageable) {

		checkNotNull(subscriberId);

		Pageable pageRequest = setupPageRequest(pageable);

		Specifications<SubscrContEventNotification> specs = Specifications.where(specSubscriberId(subscriberId))
				.and(specContEventDate(fromDate, toDate)).and(specIsNew(isNew)).and(specContObjectId(contObjectList))
				.and(specContEventTypeId(contEventTypeList));

		return subscrContEventNotificationRepository.findAll(specs, pageRequest);
	}

	/**
	 * 
	 * @param specs
	 * @return
	 */
	private <T> Specifications<T> andFilterBuild(List<Specification<T>> specList) {
		if (specList == null) {
			return null;
		}
		Specifications<T> result = null;
		for (Specification<T> i : specList) {
			if (i == null) {
				continue;
			}
			result = result == null ? Specifications.where(i) : result.and(i);
		}

		return result;
	}

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
				specContEventDate(searchConditions.period), // 
				specIsNew(searchConditions.isNew), // 
				specContObjectId(searchConditions.contObjectIdList), //
				specContEventTypeId(searchConditions.contEventTypeList), //
				specContEventTypeCategory(searchConditions.contEventCategoryList));

		Specifications<SubscrContEventNotification> specs = andFilterBuild(andFilter);

		return subscrContEventNotificationRepository.findAll(specs, pageRequest);

	}

	/**
	 * 
	 * @param subscriberId
	 * @param datePeriod
	 * @param contObjectList
	 * @param contEventTypeList
	 * @param isNew
	 * @param pageable
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateRevisionByConditions(Long subscriberId, final LocalDatePeriod datePeriod,
			final List<Long> contObjectList, final List<Long> contEventTypeList, final Boolean isNew,
			final Boolean revisionIsNew, Long revisionSubscrUserId) {

		checkNotNull(subscriberId);

		Specifications<SubscrContEventNotification> specs = Specifications.where(specSubscriberId(subscriberId))
				.and(specContEventDate(datePeriod.getDateFrom(), datePeriod.getDateTo())).and(specIsNew(isNew))
				.and(specContObjectId(contObjectList)).and(specContEventTypeId(contEventTypeList));

		Iterable<SubscrContEventNotification> updateCandidates = subscrContEventNotificationRepository.findAll(specs);
		for (SubscrContEventNotification n : updateCandidates) {
			updateNotificationOneIsNew(n, revisionIsNew, revisionSubscrUserId);
		}
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
	 * @param searchTerm
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
	 * @param subscriberId
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specContEventDate(final Date fromDate,
			final Date toDate) {
		return (root, query, cb) -> {
			if (fromDate == null || toDate == null) {
				return null;
			}
			return cb.and(
					cb.greaterThanOrEqualTo(root.<Date> get(SubscrContEventNotification_.contEventTime), fromDate),
					cb.lessThanOrEqualTo(root.<Date> get(SubscrContEventNotification_.contEventTime), toDate));
		};
	}

	/**
	 * 
	 * @param period
	 * @return
	 */
	private static Specification<SubscrContEventNotification> specContEventDate(final LocalDatePeriod period) {
		return (root, query, cb) -> {
			if (period == null || period.isInvalidEq()) {
				return null;
			}
			return cb.and(
					cb.greaterThanOrEqualTo(root.<Date> get(SubscrContEventNotification_.contEventTime),
							period.getDateFrom()),
					cb.lessThanOrEqualTo(root.<Date> get(SubscrContEventNotification_.contEventTime),
							period.getDateTo()));
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
	private static Specification<SubscrContEventNotification> specContEventTypeCategory(
			final List<String> contEventCategoryList) {
		return (root, query, cb) -> {
			if (contEventCategoryList == null || contEventCategoryList.size() == 0) {
				return null;
			}
			return cb.or(
					root.get(SubscrContEventNotification_.contEvent).get(ContEvent_.contEventType)
							.get(ContEventType_.contEventCategory).in(contEventCategoryList),
					root.get(SubscrContEventNotification_.contEvent).get(ContEvent_.contEventType)
							.get(ContEventType_.contEventCategory).isNull());

		};
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrContEventNotification findOneNotification(Long id) {
		return subscrContEventNotificationRepository.findOne(id);
	}

	/**
	 * 
	 * @param subscrContEventNotificationId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContEventNotification updateNotificationOneIsNew(Boolean isNew, Long subscrContEventNotificationId,
			Long revisionSubscrUserId) {

		checkNotNull(isNew);

		SubscrContEventNotification updateCandidate = subscrContEventNotificationRepository
				.findOne(subscrContEventNotificationId);
		if (updateCandidate == null) {
			throw new PersistenceException(String.format("SubscrContEventNotification with id=%d is not found",
					subscrContEventNotificationId));
		}

		return updateNotificationOneIsNew(updateCandidate, isNew, revisionSubscrUserId);

	}

	/**
	 * 
	 * @param subscrContEventNotification
	 * @param isNew
	 * @param revisionSubscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	private SubscrContEventNotification updateNotificationOneIsNew(
			SubscrContEventNotification subscrContEventNotification, Boolean isNew, Long revisionSubscrUserId) {

		checkNotNull(subscrContEventNotification);
		subscrContEventNotification.setIsNew(isNew);
		Date revisionDate = new Date();
		subscrContEventNotification.setRevisionTime(revisionDate);
		subscrContEventNotification.setRevisionTimeTZ(revisionDate);
		subscrContEventNotification.setRevisionSubscrUserId(revisionSubscrUserId);
		return subscrContEventNotificationRepository.save(subscrContEventNotification);
	}

	/**
	 * 
	 * @param notificationIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateNotificationIsNew(Boolean isNew, List<Long> notificationIds, Long revisionSubscrUserId) {
		checkNotNull(isNew);
		checkNotNull(notificationIds);
		checkNotNull(revisionSubscrUserId);
		for (Long id : notificationIds) {
			updateNotificationOneIsNew(isNew, id, revisionSubscrUserId);
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
			final LocalDatePeriod datePeriod) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository.selectNotificatoinsCount(subscriberId, contObjectId,
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public long selectNotificationsCount(final Long subscriberId, final Long contObjectId,
			final LocalDatePeriod datePeriod, Boolean isNew) {
		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkNotNull(isNew);
		checkState(datePeriod.isValidEq());

		Long result = subscrContEventNotificationRepository.selectNotificatoinsCount(subscriberId, contObjectId,
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	@Deprecated
	public long selectContEventTypeCount(final Long subscriberId, final Long contObjectId,
			final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> typesList = subscrContEventNotificationRepository.selectNotificationEventTypeCount(subscriberId,
				contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

		return typesList.size();
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
			final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> typesList = subscrContEventNotificationRepository.selectNotificationEventTypeCountGroup(
				subscriberId, contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

		return typesList.size();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventTypeStatus> selectMonitorContEventTypeStatus(final Long subscriberId,
			final Long contObjectId, final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCount(
				subscriberId, contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

		List<CounterInfo> selectList = selectResult.stream()
				.map((objects) -> CounterInfo.newInstance(objects[0], objects[1])).collect(Collectors.toList());

		List<MonitorContEventTypeStatus> resultList = new ArrayList<>();

		for (CounterInfo ci : selectList) {

			ContEventType contEventType = contEventTypeService.findOne(ci.id);
			checkNotNull(contEventType);

			MonitorContEventTypeStatus item = MonitorContEventTypeStatus.newInstance(contEventType);
			item.setTotalCount(ci.count);
			List<ContEventLevelColor> levelColors = contEventLevelColorRepository
					.selectByContEventLevel(contEventType.getContEventLevel());

			checkState(levelColors.size() == 1,
					"Can't calculate eventLevelColor for contEventType with keyname:" + contEventType.getKeyname());

			item.setContEventLevelColorKey(levelColors.get(0).getColorKey());
			resultList.add(item);

		}

		return resultList;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventTypeStatus> selectMonitorContEventTypeStatusCollapse(final Long subscriberId,
			final Long contObjectId, final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCountCollapse(
				subscriberId, contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

		List<CounterInfo> selectList = selectResult.stream()
				.map((objects) -> CounterInfo.newInstance(objects[0], objects[1])).collect(Collectors.toList());

		List<MonitorContEventTypeStatus> resultList = new ArrayList<>();

		for (CounterInfo ci : selectList) {

			ContEventType contEventType = contEventTypeService.findOne(ci.id);
			checkNotNull(contEventType);

			MonitorContEventTypeStatus item = MonitorContEventTypeStatus.newInstance(contEventType);
			item.setTotalCount(ci.count);

			List<ContEventLevelColor> levelColors = contEventLevelColorRepository
					.selectByContEventLevel(contEventType.getContEventLevel());

			checkState(levelColors.size() == 1,
					"Can't calculate eventLevelColor for contEventType with keyname:" + contEventType.getKeyname());

			item.setContEventLevelColorKey(levelColors.get(0).getColorKey());
			resultList.add(item);
		}

		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventNotificationStatus> selectMonitorContEventNotificationStatus(final Long subscriberId,
			final LocalDatePeriod datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(subscriberId);

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		ContObjectCounterMap allMap = new ContObjectCounterMap(
				selectContEventNotificationInfoList(subscriberId, contObjectIds, datePeriod));

		ContObjectCounterMap allNewMap = new ContObjectCounterMap(
				selectContEventNotificationInfoList(subscriberId, contObjectIds, datePeriod, Boolean.TRUE));

		ContObjectCounterMap contallEventTypesMap = new ContObjectCounterMap(
				selectContObjectEventTypeCountGroupInfoList(subscriberId, contObjectIds, datePeriod));

		Map<Long, List<ContEventMonitor>> monitorContObjectsMap = contEventMonitorService
				.getContObjectsContEventMonitorMap(contObjectIds);

		List<MonitorContEventNotificationStatus> result = new ArrayList<>();
		for (ContObject co : contObjects) {

			List<ContEventMonitor> availableMonitors = monitorContObjectsMap.get(co.getId());

			ContEventLevelColorKey monitorColorKey = null;

			if (availableMonitors != null) {
				ContEventLevelColor monitorColor = contEventMonitorService.sortWorseColor(availableMonitors);
				monitorColorKey = contEventMonitorService.getColorKey(monitorColor);
			}

			// ContEventLevelColorKey checkMonitorColorKey =
			// contEventMonitorService
			// .getColorKeyByContObject(co.getId());
			//
			// checkState(checkMonitorColorKey == monitorColorKey);

			long allCnt = allMap.getCountValue(co.getId());

			long newCnt = 0;
			long typesCnt = 0;

			ContEventLevelColorKey resultColorKey = monitorColorKey;

			if (allCnt > 0) {

				newCnt = allNewMap.getCountValue(co.getId());

				// typesCnt = selectContEventTypeCountGroup(subscriberId,
				// co.getId(), datePeriod);
				typesCnt = contallEventTypesMap.getCountValue(co.getId());

				if (resultColorKey == null) {
					resultColorKey = ContEventLevelColorKey.YELLOW;
				}
			}

			if (resultColorKey == null) {
				resultColorKey = ContEventLevelColorKey.GREEN;
			}

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(co);

			item.setEventsCount(allCnt);
			item.setNewEventsCount(newCnt);
			item.setEventsTypesCount(typesCnt);
			item.setContEventLevelColorKey(resultColorKey);

			result.add(item);
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventNotificationStatus> selectMonitorContEventNotificationStatusCollapse(
			final Long subscriberId, final LocalDatePeriod datePeriod, Boolean noGreenColor) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(subscriberId);

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		ContObjectCounterMap allMap = new ContObjectCounterMap(
				selectContEventNotificationInfoList(subscriberId, contObjectIds, datePeriod));

		ContObjectCounterMap allNewMap = new ContObjectCounterMap(
				selectContEventNotificationInfoList(subscriberId, contObjectIds, datePeriod, Boolean.TRUE));

		ContObjectCounterMap contallEventTypesMap = new ContObjectCounterMap(
				selectContObjectEventTypeCountGroupInfoListCollapse(subscriberId, contObjectIds, datePeriod));

		Map<Long, List<ContEventMonitor>> monitorContObjectsMap = contEventMonitorService
				.getContObjectsContEventMonitorMap(contObjectIds);

		List<MonitorContEventNotificationStatus> monitorStatusList = new ArrayList<>();
		for (ContObject co : contObjects) {

			List<ContEventMonitor> availableMonitors = monitorContObjectsMap.get(co.getId());

			ContEventLevelColorKey monitorColorKey = null;

			if (availableMonitors != null) {
				ContEventLevelColor monitorColor = contEventMonitorService.sortWorseColor(availableMonitors);
				monitorColorKey = contEventMonitorService.getColorKey(monitorColor);
			}

			long allCnt = allMap.getCountValue(co.getId());

			long newCnt = 0;
			long typesCnt = 0;

			ContEventLevelColorKey resultColorKey = monitorColorKey;

			if (allCnt > 0) {

				newCnt = allNewMap.getCountValue(co.getId());

				typesCnt = contallEventTypesMap.getCountValue(co.getId());

				if (resultColorKey == null) {
					resultColorKey = ContEventLevelColorKey.YELLOW;
				}
			}

			if (resultColorKey == null) {
				resultColorKey = ContEventLevelColorKey.GREEN;
			}

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(co);

			item.setEventsCount(allCnt);
			item.setNewEventsCount(newCnt);
			item.setEventsTypesCount(typesCnt);
			item.setContEventLevelColorKey(resultColorKey);

			monitorStatusList.add(item);
		}

		List<MonitorContEventNotificationStatus> resultList = null;

		if (Boolean.TRUE.equals(noGreenColor)) {
			resultList = monitorStatusList.stream()
					.filter((n) -> n.getContEventLevelColorKey() != ContEventLevelColorKey.GREEN)
					.collect(Collectors.toList());
		} else {
			resultList = monitorStatusList;
		}

		return resultList;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @return
	 */
	private List<CounterInfo> selectContEventNotificationInfoList(final Long subscriberId,
			final List<Long> contObjectIds, final LocalDatePeriod datePeriod) {
		return selectContEventNotificationInfoList(subscriberId, contObjectIds, datePeriod, null);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @param datePeriod
	 * @param isNew
	 * @return
	 */
	private List<CounterInfo> selectContEventNotificationInfoList(final Long subscriberId,
			final List<Long> contObjectIds, final LocalDatePeriod datePeriod, Boolean isNew) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		List<Object[]> selectResult = null;
		if (isNew == null) {
			selectResult = subscrContEventNotificationRepository.selectNotificatoinsCountList(subscriberId,
					contObjectIds, datePeriod.getDateFrom(), datePeriod.getDateTo());
		} else {
			selectResult = subscrContEventNotificationRepository.selectNotificatoinsCountList(subscriberId,
					contObjectIds, datePeriod.getDateFrom(), datePeriod.getDateTo(), isNew);
		}

		checkNotNull(selectResult);

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
	@Deprecated
	private List<CounterInfo> selectContObjectEventTypeCountGroupInfoList(final Long subscriberId,
			final List<Long> contObjectIds, final LocalDatePeriod datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCountGroup(
				subscriberId, contObjectIds, datePeriod.getDateFrom(), datePeriod.getDateTo());

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
	private List<CounterInfo> selectContObjectEventTypeCountGroupInfoListCollapse(final Long subscriberId,
			final List<Long> contObjectIds, final LocalDatePeriod datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkArgument(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository
				.selectNotificationEventTypeCountGroupCollapse(subscriberId, contObjectIds, datePeriod.getDateFrom(),
						datePeriod.getDateTo());

		List<CounterInfo> resultList = selectResult.stream()
				.map((objects) -> CounterInfo.newInstance(objects[0], objects[1])).collect(Collectors.toList());

		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CityMonitorContEventsStatus> selectMonitoryContObjectCityStatus(final Long subscriberId,
			final LocalDatePeriod datePeriod, Boolean noGreenColor) {

		List<MonitorContEventNotificationStatus> resultObjects = selectMonitorContEventNotificationStatusCollapse(
				subscriberId, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatus> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatus.FACTORY_INSTANCE);

		Map<UUID, Long> cityEventCount = contEventMonitorService.selectCityContObjectMonitorEventCount(subscriberId);

		result.forEach((i) -> {
			Long cnt = cityEventCount.get(i.getCityFiasUUID());
			i.setMonitorEventCount(cnt != null ? cnt : 0);
		});

		return result;
	}

}
