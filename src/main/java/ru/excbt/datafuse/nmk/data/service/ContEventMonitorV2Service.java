package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorV2Repository;

/**
 * Сервис для работы с монитором событий
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 30.06.2015
 *
 */

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class ContEventMonitorV2Service {

	private static final Logger logger = LoggerFactory.getLogger(ContEventMonitorV2Service.class);

	/**
	 * 
	 */
	public final static Comparator<ContEventMonitorV2> CMP_BY_COLOR_RANK = (e1, e2) -> Integer.compare(
			e1.getContEventLevelColor() == null ? -1 : e1.getContEventLevelColor().getColorRank(),
			e2.getContEventLevelColor() == null ? -1 : e2.getContEventLevelColor().getColorRank());

	/**
	 * 
	 */
	public final static Comparator<ContEventMonitorV2> CMP_BY_EVENT_TIME = (e1, e2) -> e1.getContEventTime()
			.compareTo(e2.getContEventTime());

	/**
	 * 		
	 */
	@Autowired
	private ContEventMonitorV2Repository contEventMonitorV2Repository;

	@Autowired
	private ContEventService contEventService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	protected List<ContEventMonitorV2> findByContObject(Long contObjectId) {
		checkNotNull(contObjectId);

		List<ContEventMonitorV2> contEventMonitor = contEventMonitorV2Repository.findByContObjectId(contObjectId);

		List<ContEventMonitorV2> result = contEventMonitor.stream().sorted(CMP_BY_EVENT_TIME)
				.collect(Collectors.toList());

		return contEventService.enhanceContEventType(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEventMonitorV2> selectByContObject(Long contObjectId) {
		checkNotNull(contObjectId);

		List<ContEventMonitorV2> result = contEventMonitorV2Repository.selectByContObjectId(contObjectId);

		return contEventService.enhanceContEventType(result);
	}

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	public List<ContEventMonitorV2> selectByContObjectIds(List<Long> contObjectIds) {
		checkNotNull(contObjectIds);

		if (contObjectIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<ContEventMonitorV2> result = contEventMonitorV2Repository.selectByContObjectIds(contObjectIds);

		return contEventService.enhanceContEventType(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public ContEventLevelColorV2 getWorseColorByContObject(Long contObjectId) {
		checkNotNull(contObjectId);
		List<ContEventMonitorV2> contEventMonitor = contEventMonitorV2Repository.findByContObjectId(contObjectId);
		return sortWorseColor(contEventMonitor);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public List<ContEventMonitorV2> selectBySubscriberId(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContEventMonitorV2> result = contEventMonitorV2Repository.selectBySubscriberId(subscriberId);
		return contEventService.enhanceContEventType(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public ContEventLevelColorV2 getWorseColorBySubscriberId(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContEventMonitorV2> contEventMonitor = contEventMonitorV2Repository.selectBySubscriberId(subscriberId);

		return sortWorseColor(contEventMonitor);
	}

	/**
	 * 
	 * @param contEventMonitor
	 * @return
	 */
	public ContEventLevelColorV2 sortWorseColor(List<ContEventMonitorV2> contEventMonitor) {

		checkNotNull(contEventMonitor);

		if (contEventMonitor.isEmpty()) {
			return null;
		}

		Optional<ContEventMonitorV2> sorted = contEventMonitor.stream().filter(i -> i.getContEventLevel() != null)
				.sorted(CMP_BY_COLOR_RANK).findFirst();

		if (!sorted.isPresent()) {
			return null;
		}

		return sorted.get().getContEventLevelColor();
	}

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	public Map<Long, List<ContEventMonitorV2>> getContObjectsContEventMonitorMap(List<Long> contObjectIds) {
		List<ContEventMonitorV2> monitorList = contEventService
				.enhanceContEventType(contEventMonitorV2Repository.selectByContObjectIds(contObjectIds));

		Map<Long, List<ContEventMonitorV2>> resultMap = new HashMap<>();
		for (ContEventMonitorV2 m : monitorList) {
			if (!resultMap.containsKey(m.getContObjectId())) {
				resultMap.put(m.getContObjectId(), new ArrayList<>());
			}
			resultMap.get(m.getContObjectId()).add(m);
		}

		return resultMap;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Map<UUID, Long> selectCityContObjectMonitorEventCount(Long subscriberId) {

		Map<UUID, Long> resultMap = new HashMap<>();

		List<Object[]> cityContEventCount = contEventMonitorV2Repository
				.selectCityContObjectMonitorEventCount(subscriberId);

		cityContEventCount.forEach((i) -> {
			String strUUID = (String) i[0];
			UUID cityUUID = strUUID != null ? UUID.fromString(strUUID) : null;
			BigInteger count = (BigInteger) i[1];
			resultMap.put(cityUUID, count.longValue());
		});

		return resultMap;
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZpointId
	 * @return
	 */
	public List<ContEventMonitorV2> selectByContZpoint(Long contObjectId, Long contZpointId) {
		return contEventMonitorV2Repository.selectByZpointId(contObjectId, contZpointId);
	}

}
