package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventLevelColorRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfo;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class SubscrContEventNotificationStatusService extends AbstractService {


	private final SubscrContEventNotificationService subscrContEventNotificationService;

	private final ContEventMonitorService contEventMonitorService;

	private final ContEventTypeService contEventTypeService;

	private final SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	private final ContEventLevelColorRepository contEventLevelColorRepository;

	private final ContObjectService contObjectService;

	private final ContObjectFiasService contObjectFiasService;

	private final ObjectAccessService objectAccessService;

    public SubscrContEventNotificationStatusService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorService contEventMonitorService, ContEventTypeService contEventTypeService, SubscrContEventNotificationRepository subscrContEventNotificationRepository, ContEventLevelColorRepository contEventLevelColorRepository, ContObjectService contObjectService, ContObjectFiasService contObjectFiasService, ObjectAccessService objectAccessService) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorService = contEventMonitorService;
        this.contEventTypeService = contEventTypeService;
        this.subscrContEventNotificationRepository = subscrContEventNotificationRepository;
        this.contEventLevelColorRepository = contEventLevelColorRepository;
        this.contObjectService = contObjectService;
        this.contObjectFiasService = contObjectFiasService;
        this.objectAccessService = objectAccessService;
    }

    /**
	 *
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CityMonitorContEventsStatus> selectCityMonitoryContEventsStatus_old(
			final SubscriberParam subscriberParam, final Long contGroupId, final LocalDatePeriod datePeriod,
			Boolean noGreenColor) {

		List<ContObject> contObjects = objectAccessService.findContObjects(subscriberParam.getSubscriberId(), contGroupId);

		List<MonitorContEventNotificationStatus> resultObjects = selectMonitorContEventNotificationStatusCollapse(
				subscriberParam, contObjects, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatus> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatus.FACTORY_INSTANCE);

		Map<UUID, Long> cityEventCount = contEventMonitorService
				.selectCityContObjectMonitorEventCount(subscriberParam.getSubscriberId());

		result.forEach((i) -> {
			Long cnt = cityEventCount.get(i.getCityFiasUUID());
			i.setMonitorEventCount(cnt != null ? cnt : 0);
		});

		return result;
	}

	/**
	 *
	 * @param contObjects
	 * @param subscriberParam
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CityMonitorContEventsStatus> selectCityMonitoryContEventsStatus(final SubscriberParam subscriberParam,
			final List<ContObject> contObjects, final LocalDatePeriod datePeriod, Boolean noGreenColor) {

		List<MonitorContEventNotificationStatus> resultObjects = selectMonitorContEventNotificationStatusCollapse(
				subscriberParam, contObjects, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatus> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatus.FACTORY_INSTANCE);

		Map<UUID, Long> cityEventCount = contEventMonitorService
				.selectCityContObjectMonitorEventCount(subscriberParam.getSubscriberId());

		result.forEach((i) -> {
			Long cnt = cityEventCount.get(i.getCityFiasUUID());
			i.setMonitorEventCount(cnt != null ? cnt : 0);
		});

		return result;
	}

	/**
	 *
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventNotificationStatus> selectMonitorContEventNotificationStatus(final Long subscriberId,
			final LocalDatePeriod datePeriod) {
		checkNotNull(subscriberId);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<ContObject> contObjects = objectAccessService.findContObjects(subscriberId);

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		CounterInfoMap allMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContEventNotificationCounterInfo(subscriberId, contObjectIds, datePeriod));

		CounterInfoMap allNewMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContEventNotificationCounterInfo(subscriberId, contObjectIds, datePeriod, Boolean.TRUE));

		CounterInfoMap contallEventTypesMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContObjectEventTypeGroupCounterInfo(subscriberId, contObjectIds, datePeriod));

		Map<Long, List<ContEventMonitor>> monitorContObjectsMap = contEventMonitorService
				.getContObjectsContEventMonitorMap(contObjectIds);

		Map<Long, ContObjectFias> contObjectFiasMap = contObjectFiasService.selectContObjectsFiasMap(contObjectIds);
		Map<Long, ContObjectGeoPos> contObjectGeoPosMap = contObjectService.selectContObjectsGeoPosMap(contObjectIds);

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

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(co,
					contObjectFiasMap.get(co.getId()), contObjectGeoPosMap.get(co.getId()));

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
			final SubscriberParam subscriberParam, final List<ContObject> contObjects, final LocalDatePeriod datePeriod,
			Boolean noGreenColor) {
		checkNotNull(subscriberParam);
		checkNotNull(contObjects);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		if (contObjectIds.isEmpty()) {
			contObjectIds = NO_DATA_IDS;
		}

		CounterInfoMap allMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContEventNotificationCounterInfo(subscriberParam.getSubscriberId(), contObjectIds, datePeriod));

		CounterInfoMap allNewMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContEventNotificationCounterInfo(
						subscriberParam.getSubscriberId(), contObjectIds, datePeriod, Boolean.TRUE));

		CounterInfoMap contallEventTypesMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContObjectEventTypeGroupCollapseCounterInfo(
						subscriberParam.getSubscriberId(), contObjectIds, datePeriod));

		Map<Long, List<ContEventMonitor>> monitorContObjectsMap = contEventMonitorService
				.getContObjectsContEventMonitorMap(contObjectIds);

		Map<Long, ContObjectFias> contObjectFiasMap = contObjectFiasService.selectContObjectsFiasMap(contObjectIds);
		Map<Long, ContObjectGeoPos> contObjectGeoPosMap = contObjectService.selectContObjectsGeoPosMap(contObjectIds);

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

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(co,
					contObjectFiasMap.get(co.getId()), contObjectGeoPosMap.get(co.getId()));

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
	 * @param contObjectId
	 * @param datePeriod
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<MonitorContEventTypeStatus> selectMonitorContEventTypeStatus(final Long subscriberId,
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

			ContEventType contEventType = contEventTypeService.findOne(ci.getId());
			checkNotNull(contEventType);

			MonitorContEventTypeStatus item = MonitorContEventTypeStatus.newInstance(contEventType);
			item.setTotalCount(ci.getCount());
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
     * @param subscriberParam
     * @param contObjectId
     * @param datePeriod
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventTypeStatus> selectMonitorContEventTypeStatusCollapse(
			final SubscriberParam subscriberParam, final Long contObjectId, final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(subscriberParam);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCountCollapse(
				subscriberParam.getSubscriberId(), contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

		List<CounterInfo> selectList = selectResult.stream()
				.map((objects) -> CounterInfo.newInstance(objects[0], objects[1])).collect(Collectors.toList());

		List<MonitorContEventTypeStatus> resultList = new ArrayList<>();

		for (CounterInfo ci : selectList) {

			ContEventType contEventType = contEventTypeService.findOne(ci.getId());
			checkNotNull(contEventType);

			MonitorContEventTypeStatus item = MonitorContEventTypeStatus.newInstance(contEventType);
			item.setTotalCount(ci.getCount());

			List<ContEventLevelColor> levelColors = contEventLevelColorRepository
					.selectByContEventLevel(contEventType.getContEventLevel());

			checkState(levelColors.size() == 1,
					"Can't calculate eventLevelColor for contEventType with keyname:" + contEventType.getKeyname());

			item.setContEventLevelColorKey(levelColors.get(0).getColorKey());
			resultList.add(item);
		}

		return resultList;
	}

}
