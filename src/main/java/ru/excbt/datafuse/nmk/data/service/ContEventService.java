package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventDeviation;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventCategoryRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventDeviationRepository;

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

//	@Autowired
	private final ContEventRepository contEventRepository;

//	@Autowired
	private final ContEventTypeRepository contEventTypeRepository;

//	@Autowired
	private final ContEventCategoryRepository contEventCategoryRepository;

//	@Autowired
	private final ContEventDeviationRepository contEventDeviationRepository;

    @Autowired
	public ContEventService(ContEventRepository contEventRepository,
                            ContEventTypeRepository contEventTypeRepository,
                            ContEventCategoryRepository contEventCategoryRepository,
                            ContEventDeviationRepository contEventDeviationRepository) {
	    this.contEventRepository = contEventRepository;
	    this.contEventTypeRepository = contEventTypeRepository;
	    this.contEventCategoryRepository = contEventCategoryRepository;
	    this.contEventDeviationRepository = contEventDeviationRepository;
    }

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	public Page<ContEvent> selectEventsBySubscriber(long subscriberId) {
		return loadContEventTypeModel(selectEventsBySubscriber(subscriberId, DEFAULT_MAX_EVENTS_PAGE_REQUEST));
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	public Page<ContEvent> selectEventsBySubscriber(long subscriberId, Pageable pageable) {
		Page<ContEvent> result = contEventRepository.selectBySubscriber(subscriberId, pageable);
		return loadContEventTypeModel(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	public List<ContEvent> findEventsByContObjectId(long contObjectId) {
		return loadContEventTypeModel(findEventsByContObjectId(contObjectId, DEFAULT_MAX_EVENTS_PAGE_REQUEST));
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	public List<ContEvent> findEventsByContObjectId(long contObjectId, Pageable pageable) {
		return loadContEventTypeModel(contEventRepository.findByContObjectId(contObjectId, pageable));
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
		return loadContEventTypeModel(contEventRepository.selectBySubscriberAndDate(subscriberId, startDate.toDate(),
				endDate.toDate(), pageable));
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
		return loadContEventTypeModel(selectBySubscriberAndDateAndContObjectIds(subscriberId, startDate, endDate,
				contObjectIds, DEFAULT_MAX_EVENTS_PAGE_REQUEST));
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

		return loadContEventTypeModel(contEventRepository.selectBySubscriberAndDateAndContObjects(subscriberId,
				startDate.toDate(), endDate.toDate(), contObjectIds, pageable));
	}

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @return
	 */
	public Page<ContEvent> selectBySubscriberAndContObjectIds(long subscriberId, List<Long> contObjectIds) {
		return loadContEventTypeModel(
				selectBySubscriberAndContObjectIds(subscriberId, contObjectIds, DEFAULT_MAX_EVENTS_PAGE_REQUEST));
	}

    /**
     *
     * @param subscriberId
     * @param contObjectIds
     * @param pageable
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

		return loadContEventTypeModel(
				contEventRepository.selectBySubscriberAndContObjects(subscriberId, contObjectIds, pageable));

	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContEventCategory> selectContEventCategoryList() {
		return contEventCategoryRepository.selectCategoryList();
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContEventDeviation> findContEventDeviation() {
		return Lists.newArrayList(contEventDeviationRepository.selectContEventDeviation());
	}

	/**
	 *
	 * @param contEventsIds
	 * @return
	 */
	public List<ContEvent> selectContEventsByIds(Collection<Long> contEventsIds) {
		if (contEventsIds == null || contEventsIds.isEmpty()) {
			return new ArrayList<>();
		}

		return loadContEventTypeModel(contEventRepository.selectContEventsByIds(contEventsIds));
	}

	/**
	 * Gets Ids of ContEventType, load from database and setup model
	 * @param contEvents
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public <T extends ContEventTypeModel> List<T> loadContEventTypeModel(List<T> contEvents) {

		List<Long> contEventTypeIds = contEvents.stream().map(i -> i.getContEventTypeId()).distinct()
				.collect(Collectors.toList());

		if (contEventTypeIds.isEmpty()) {
			return contEvents;
		}

		List<ContEventType> contEventTypeList = contEventTypeRepository.selectContEventTypes(contEventTypeIds);

		final Map<Long, ContEventType> contEventTypes = contEventTypeList.stream()
				.collect(Collectors.toMap(ContEventType::getId, Function.identity()));

		contEvents.forEach(i -> i.setContEventType(contEventTypes.get(i.getContEventTypeId())));

		return contEvents;
	}

	/**
	 * Gets Ids of ContEventType, load from database and setup model
	 * @param contEventsPage
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public <T extends ContEventTypeModel> Page<T> loadContEventTypeModel(Page<T> contEventsPage) {

		loadContEventTypeModel(contEventsPage.getContent());
		return contEventsPage;

	}

}
