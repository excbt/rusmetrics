package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

import ru.excbt.datafuse.nmk.data.model.ContEvent_;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification_;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;

@Service
@Transactional
public class SubscrContEventNotifiicationService {

	private final static int DEFAULT_PAGE_SIZE = 100;
	private final static Pageable DEFAULT_NOTIFICATION_PAGE_REQUEST = new PageRequest(
			0, DEFAULT_PAGE_SIZE, makeDefaultSort());

	public final static String[] AVAILABLE_SORT_FIELDS = { "contEventTime" };
	public final static List<String> AVAILABLE_SORT_FIELD_LIST = Collections
			.unmodifiableList(Arrays.asList(AVAILABLE_SORT_FIELDS));

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

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
	public Page<SubscrContEventNotification> selectByConditions(
			long subscriberId, final Date fromDate, final Date toDate,
			final List<Long> contObjectList,
			final List<Long> contEventTypeList, final Boolean isNew,
			final Pageable pageable) {

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
					pageable.getPageSize(), makeSort(Direction.DESC));
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
}
