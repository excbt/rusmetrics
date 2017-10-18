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
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

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
	public final static Comparator<ContEventMonitorV2> CMP_BY_COLOR_RANK =
        Comparator.comparingInt(e -> e.getContEventLevelColor() == null ? -1 : e.getContEventLevelColor().getColorRank());

	/**
	 *
	 */
	public final static Comparator<ContEventMonitorV2> CMP_BY_EVENT_TIME =
        Comparator.comparing(ContEventMonitorV2::getContEventTime);

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

        final List<ContEventMonitorV2> rawMonitorList = contEventMonitorV2Repository.selectByContObjectIds(contObjectIds);

		List<ContEventMonitorV2> monitorList = contEventService.enhanceContEventType(rawMonitorList);

        Map<Long, List<ContEventMonitorV2>> resultMap = GroupUtil.makeIdMap(monitorList, (m) -> m.getContObjectId());

        return resultMap;
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	public Map<UUID, Long> selectCityContObjectMonitorEventCount(Long subscriberId) {


		List<Object[]> cityContEventCount = contEventMonitorV2Repository
				.selectCityContObjectMonitorEventCount(subscriberId);

        Map<UUID, Long> resultMap = new HashMap<>();
		cityContEventCount.forEach((i) -> {
			String strUUID = DBRowUtil.asString(i[0]);
			UUID cityUUID = strUUID != null ? UUID.fromString(strUUID) : null;
			BigInteger count = DBRowUtil.asBigInteger(i[1]);
			resultMap.put(cityUUID, count.longValue());
		});

		return resultMap;
	}


	public List<ContEventMonitorV2> selectByContZPoint(Long contObjectId, Long contZPointId) {
		return contEventMonitorV2Repository.selectByZPointId(contObjectId, contZPointId);
	}

}
