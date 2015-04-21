package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;

@Service
@Transactional
public class ContEventService {

	private final static int DEFAULT_MAX_EVENTS = 1000;
	private final static PageRequest DEFAULT_MAX_EVENTS_PAGE_REQUEST = new PageRequest(
			0, DEFAULT_MAX_EVENTS);

	@Autowired
	private ContEventRepository contEventRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ContEvent> selectEventsBySubscriber(long subscriberId) {
		Page<ContEvent> result = contEventRepository.selectBySubscriber(
				subscriberId, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContEvent> findEventsByContObjectId(long contObjectId) {
		return contEventRepository.findByContObjectId(contObjectId,
				DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ContEvent> selectBySubscriberAndDate(long subscriberId,
			DateTime startDate, DateTime endDate) {
		checkNotNull(startDate);
		checkNotNull(endDate);
		checkArgument(subscriberId > 0);
		return contEventRepository.selectBySubscriberAndDate(subscriberId,
				startDate.toDate(), endDate.toDate(),
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
	@Transactional(readOnly = true)
	public Page<ContEvent> selectBySubscriberAndDateAndContObjectIds(
			long subscriberId, DateTime startDate, DateTime endDate,
			List<Long> contObjectIds) {
		checkNotNull(startDate);
		checkNotNull(endDate);
		checkArgument(subscriberId > 0);

		if (contObjectIds == null || contObjectIds.size() == 0) {
			return contEventRepository.selectBySubscriberAndDate(subscriberId,
					startDate.toDate(), endDate.toDate(),
					DEFAULT_MAX_EVENTS_PAGE_REQUEST);
		}

		return contEventRepository.selectBySubscriberAndDateAndContObjects(
				subscriberId, startDate.toDate(), endDate.toDate(),
				contObjectIds, DEFAULT_MAX_EVENTS_PAGE_REQUEST);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ContEvent> selectBySubscriberAndContObjectIds(long subscriberId,
			List<Long> contObjectIds) {

		checkArgument(subscriberId > 0);
		checkNotNull(contObjectIds);

		if (contObjectIds.size() == 0) {
			contEventRepository.selectBySubscriber(subscriberId,
					DEFAULT_MAX_EVENTS_PAGE_REQUEST);
		}

		return contEventRepository.selectBySubscriberAndContObjects(
				subscriberId, contObjectIds, DEFAULT_MAX_EVENTS_PAGE_REQUEST);

	}

}
