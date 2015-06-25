package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collections;
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

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification_;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;

@Service
@Transactional
public class SubscrContEventNotifiicationService {

	private final static int DEFAULT_PAGE_SIZE = 100;
	private final static Pageable DEFAULT_NOTIFICATION_PAGE_REQUEST = new PageRequest(
			0, DEFAULT_PAGE_SIZE, makeDefaultSort());

	private final static String[] AVAILABLE_SORT_FIELDS = { "contEventTime" };
	private final static List<String> AVAILABLE_SORT_FIELD_LIST = Collections
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
	public Page<SubscrContEventNotification> selectAllNotifications(
			long subscriberId, Boolean isNew, Pageable pageable) {

		Pageable pageRequest = pageable;

		if (pageRequest != null) {
			checkState(checkSort(pageable.getSort()));
		} else {
			pageRequest = DEFAULT_NOTIFICATION_PAGE_REQUEST;
		}

		Page<SubscrContEventNotification> resultPage = subscrContEventNotificationRepository
				.findAll(Specifications.where(specSubscriberId(subscriberId))
						.and(specIsNew(isNew)), pageRequest);

		return resultPage;

		// if (isNew == null) {
		// return subscrContEventNotificationRepository.selectAll(
		// subscriberId, pageRequest);
		// }
		//
		// return subscrContEventNotificationRepository.selectAll(subscriberId,
		// isNew, pageRequest);
	}

	/**
	 * 
	 * @return
	 */
	private static Sort makeDefaultSort() {
		return new Sort(Direction.DESC, "contEventTime");
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

}
