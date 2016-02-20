package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventCategoryRepository;

/**
 * Сервис для работы с событиями ContEvent у ContObject
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.04.2015
 *
 */

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class ContEventService {

	private final static int DEFAULT_MAX_EVENTS = 1000;
	private final static PageRequest DEFAULT_MAX_EVENTS_PAGE_REQUEST = new PageRequest(0, DEFAULT_MAX_EVENTS);

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private ContEventTypeRepository contEventTypeRepository;

	@Autowired
	private ContEventCategoryRepository contEventCategoryRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Page<ContEvent> selectEventsBySubscriber(long subscriberId) {
		return selectEventsBySubscriber(subscriberId, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Page<ContEvent> selectEventsBySubscriber(long subscriberId, Pageable pageable) {
		Page<ContEvent> result = contEventRepository.selectBySubscriber(subscriberId, pageable);
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEvent> findEventsByContObjectId(long contObjectId) {
		return findEventsByContObjectId(contObjectId, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEvent> findEventsByContObjectId(long contObjectId, Pageable pageable) {
		return contEventRepository.findByContObjectId(contObjectId, pageable);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndDate(long subscriberId, DateTime startDate, DateTime endDate) {
		return selectBySubscriberAndDate(subscriberId, startDate, endDate, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndDate(long subscriberId, DateTime startDate, DateTime endDate,
			Pageable pageable) {
		checkNotNull(startDate);
		checkNotNull(endDate);
		checkArgument(subscriberId > 0);
		checkNotNull(pageable);
		return contEventRepository.selectBySubscriberAndDate(subscriberId, startDate.toDate(), endDate.toDate(),
				pageable);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param startDate
	 * @param endDate
	 * @param contObjectIds
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndDateAndContObjectIds(long subscriberId, DateTime startDate,
			DateTime endDate, List<Long> contObjectIds) {
		return selectBySubscriberAndDateAndContObjectIds(subscriberId, startDate, endDate, contObjectIds,
				DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param startDate
	 * @param endDate
	 * @param contObjectIds
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndDateAndContObjectIds(long subscriberId, DateTime startDate,
			DateTime endDate, List<Long> contObjectIds, Pageable pageable) {
		checkNotNull(startDate);
		checkNotNull(endDate);
		checkArgument(subscriberId > 0);
		checkNotNull(pageable);

		if (contObjectIds == null || contObjectIds.size() == 0) {
			return contEventRepository.selectBySubscriberAndDate(subscriberId, startDate.toDate(), endDate.toDate(),
					pageable);
		}

		return contEventRepository.selectBySubscriberAndDateAndContObjects(subscriberId, startDate.toDate(),
				endDate.toDate(), contObjectIds, pageable);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndContObjectIds(long subscriberId, List<Long> contObjectIds) {
		return selectBySubscriberAndContObjectIds(subscriberId, contObjectIds, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @param pageRequest
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndContObjectIds(long subscriberId, List<Long> contObjectIds,
			Pageable pageable) {

		checkArgument(subscriberId > 0);
		checkNotNull(contObjectIds);
		checkNotNull(pageable);

		if (contObjectIds.size() == 0) {
			contEventRepository.selectBySubscriber(subscriberId, pageable);
		}

		return contEventRepository.selectBySubscriberAndContObjects(subscriberId, contObjectIds, pageable);

	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContEventCategory> selectContEventCategoryList() {
		return contEventCategoryRepository.selectCategoryList();
	}

}
