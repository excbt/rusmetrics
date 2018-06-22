package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.QSubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfo;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;
import ru.excbt.datafuse.nmk.service.ContObjectQueryDSLUtil;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

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

	private final SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	private final ContEventService contEventService;

	private final ObjectAccessService objectAccessService;

	private final QueryDSLService queryDSLService;

	@Autowired
    public SubscrContEventNotificationService(SubscrContEventNotificationRepository subscrContEventNotificationRepository, ContEventService contEventService, ObjectAccessService objectAccessService, QueryDSLService queryDSLService) {
        this.subscrContEventNotificationRepository = subscrContEventNotificationRepository;
        this.contEventService = contEventService;
        this.objectAccessService = objectAccessService;
        this.queryDSLService = queryDSLService;
    }


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
		private final List<String> contServiceTypes = new ArrayList<>();

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

		public void initContServiceTypes(String[] contServiceTypes) {
			this.contServiceTypes.clear();
			if (contServiceTypes != null) {
				this.contServiceTypes.addAll(Arrays.asList(contServiceTypes));
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

		boolean checkValidInterval() {
		    return dateInterval != null && dateInterval.isValidEq();
        }

        boolean checkContObjectIds() {
		    return contObjectIdList.size() > 0;
        }

        boolean checkContEventTypes() {
		    return contEventTypeList.size() > 0;
        }

        boolean checkContEventCategory() {
		    return contEventCategoryList.size() > 0;
        }

        boolean checkContEventDeviation() {
		    return contEventDeviationList.size() > 0;
        }

        boolean checkContServiceTypes() {
		    return contServiceTypes.size() > 0;
        }
	}

	/**
	 *
	 * @param subscriberId
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SubscrContEventNotification> selectAll(final long subscriberId, final Boolean isNew,
			final Pageable pageable) {

		Pageable pageRequest = setupPageRequest(pageable);

        QSubscrContEventNotification qSubscrContEventNotification = QSubscrContEventNotification.subscrContEventNotification;

		BooleanExpression expr = qSubscrContEventNotification.subscriberId.eq(subscriberId);
		if (isNew != null) {
		    expr.and(qSubscrContEventNotification.isNew.eq(isNew));
        }
        Page<SubscrContEventNotification> resultPage = subscrContEventNotificationRepository
				.findAll(expr, pageRequest);

		initContEvent(resultPage.getContent());
		contEventService.loadContEventTypeModel(resultPage.getContent());

		return resultPage;

	}

    /**
     *
     * @param searchConditions
     * @param pageable
     * @return
     */
	@Transactional(readOnly = true)
	public Page<SubscrContEventNotification> selectNotificationByConditionsDSL(SearchConditions searchConditions,
			final Pageable pageable) {

        Pageable pageRequest = setupPageRequest(pageable);

        QSubscrContEventNotification qSubscrContEventNotification = QSubscrContEventNotification.subscrContEventNotification;

        List<BooleanExpression> booleanExpressions = new ArrayList<>();

        booleanExpressions.addAll(
            Arrays.asList(
            qSubscrContEventNotification.subscriberId.eq(searchConditions.subscriberId),
            searchConditions.checkValidInterval() ? qSubscrContEventNotification.contEventTime
                .between(searchConditions.dateInterval.getFromDate(), searchConditions.dateInterval.getToDate()) : null,
            searchConditions.isNew != null ? qSubscrContEventNotification.isNew.eq(searchConditions.isNew) : null,
            searchConditions.checkContObjectIds() ? qSubscrContEventNotification.contObjectId.in(searchConditions.contObjectIdList) : null,
            searchConditions.checkContEventTypes() ? qSubscrContEventNotification.contEventTypeId.in(searchConditions.contEventTypeList) : null,
            searchConditions.checkContEventCategory() ? qSubscrContEventNotification.contEventCategoryKeyname.in(searchConditions.contEventCategoryList) : null,
            searchConditions.checkContEventDeviation() ? qSubscrContEventNotification.contEventDeviationKeyname.in(searchConditions.contEventDeviationList) : null
        ));


        if (searchConditions.checkContServiceTypes()) {
            List<Long> contZPointIds = objectAccessService.findAllContZPointIds(searchConditions.subscriberId);
            List<Long> filteredContZPointIds = ContObjectQueryDSLUtil.filterContZPointIdByContServiceType(queryDSLService.queryFactory(), contZPointIds, searchConditions.contServiceTypes);
            if (!filteredContZPointIds.isEmpty()) {
                booleanExpressions.add(qSubscrContEventNotification.contZPointId.in(filteredContZPointIds));
            } else {
                return new PageImpl<>(Collections.EMPTY_LIST);
            }
        }


        BooleanBuilder builder = new BooleanBuilder();
        booleanExpressions.stream().forEach(i -> {
            if (i != null) {
                builder.and(i);
            }
        });

		Page<SubscrContEventNotification> result = subscrContEventNotificationRepository.findAll(builder, pageRequest);

		initContEvent(result.getContent());
		contEventService.loadContEventTypeModel(result.getContent());

		return result;

	}

    /**
     *
     * @param portalUserIds
     * @param dateInterval
     * @param contObjectList
     * @param contEventTypeList
     * @param isNew
     * @param revisionIsNew
     * @param revisionSubscrUserId
     */
	@Transactional
	public void updateRevisionByConditions(final PortalUserIds portalUserIds, final DateInterval dateInterval,
			final List<Long> contObjectList, final List<Long> contEventTypeList, final Boolean isNew,
			final Boolean revisionIsNew, Long revisionSubscrUserId) {

		checkNotNull(portalUserIds);

		QSubscrContEventNotification qSubscrContEventNotification = QSubscrContEventNotification.subscrContEventNotification;

        BooleanExpression expr = qSubscrContEventNotification.subscriberId.eq(portalUserIds.getSubscriberId());
        expr.and(qSubscrContEventNotification.contEventTime.between(dateInterval.getFromDate(), dateInterval.getToDate()));
        if (isNew != null) {
            expr.and(qSubscrContEventNotification.isNew.eq(isNew));
        }
        if (!contEventTypeList.isEmpty()) {
            expr.and(qSubscrContEventNotification.contEventTypeId.in(contEventTypeList));
        }

		Iterable<SubscrContEventNotification> updateCandidates = subscrContEventNotificationRepository.findAll(expr);
		for (SubscrContEventNotification n : updateCandidates) {
			updateNotificationRevision(portalUserIds, n, revisionIsNew);
		}
	}

    /**
     *
     * @param portalUserIds
     * @param datePeriod
     * @param contObjectIds
     * @param contEventTypeIds
     * @param revisionIsNew
     */
	@Transactional
	public void updateRevisionByConditionsFast(PortalUserIds portalUserIds, final DateInterval datePeriod,
			final List<Long> contObjectIds, final List<Long> contEventTypeIds, final Boolean revisionIsNew) {

		checkNotNull(portalUserIds);

		if ((contObjectIds == null || contObjectIds.isEmpty())
				&& (contEventTypeIds == null || contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(portalUserIds.getSubscriberId(),
					portalUserIds.getUserId());
		} else // another case
		if ((contObjectIds != null && !contObjectIds.isEmpty())
				&& (contEventTypeIds == null || contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(portalUserIds.getSubscriberId(),
					portalUserIds.getUserId(), contObjectIds);
		} else // another case
		if ((contObjectIds != null && !contObjectIds.isEmpty())
				&& (contEventTypeIds != null && !contEventTypeIds.isEmpty())) {
			subscrContEventNotificationRepository.updateAllSubscriberRevisions(portalUserIds.getSubscriberId(),
					portalUserIds.getUserId(), contObjectIds, contEventTypeIds);
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
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrContEventNotification findNotification(Long id) {
		SubscrContEventNotification result = subscrContEventNotificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrContEventNotification.class, id));
		initContEvent(result);
		return result;
	}

	/**
	 *
	 * @param subscrContEventNotificationId
	 * @return
	 */
	@Deprecated
	@Transactional
	public SubscrContEventNotification updateNotificationRevision(final PortalUserIds portalUserIds,
			final Boolean isNew, final Long subscrContEventNotificationId) {

		checkNotNull(isNew);

		SubscrContEventNotification updateCandidate = subscrContEventNotificationRepository
				.findById(subscrContEventNotificationId)
            .orElseThrow(() -> new EntityNotFoundException(SubscrContEventNotification.class, subscrContEventNotificationId));

		return updateNotificationRevision(portalUserIds, updateCandidate, isNew);

	}

    /**
     *
     * @param portalUserIds
     * @param subscrContEventNotification
     * @param isNew
     * @return
     */
	@Deprecated
	//@Transactional
	private SubscrContEventNotification updateNotificationRevision(PortalUserIds portalUserIds,
			SubscrContEventNotification subscrContEventNotification, Boolean isNew) {

		checkNotNull(subscrContEventNotification);
		subscrContEventNotification.setIsNew(isNew);
        //ZonedDateTime revisionDate = ZonedDateTime.now();
        Date revisionDate = new Date();


		subscrContEventNotification.setRevisionTime(revisionDate);
		subscrContEventNotification.setRevisionTimeTZ(revisionDate);
		subscrContEventNotification.setRevisionSubscrUserId(portalUserIds.getUserId());
		SubscrContEventNotification result = subscrContEventNotificationRepository.save(subscrContEventNotification);
		initContEvent(result);
		return result;
	}

    /**
     *
     * @param portalUserIds
     * @param notificationIds
     * @param isNew
     * @return
     */
	@Transactional
	public List<Long> updateNotificationsRevisions(PortalUserIds portalUserIds, List<Long> notificationIds,
                                                   Boolean isNew) {

		if (notificationIds != null && !notificationIds.isEmpty()) {
			subscrContEventNotificationRepository.updateSubscriberRevisions(portalUserIds.getSubscriberId(),
                portalUserIds.getUserId(), notificationIds, isNew);

		}

		return notificationIds;
	}

	/**
	 *
	 * @param notificationIds
	 */
	@Deprecated
	@Transactional
	public void updateNotificationRevision(PortalUserIds portalUserIds, Boolean isNew, List<Long> notificationIds) {
		checkNotNull(isNew);
		checkNotNull(notificationIds);
		checkNotNull(portalUserIds);
		for (Long id : notificationIds) {
			updateNotificationRevision(portalUserIds, isNew, id);
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
