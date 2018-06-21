package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventNotificationRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventLevelColorRepository;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfo;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.service.ContEventMonitorService;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class SubscrContEventNotificationStatusService {


	private final SubscrContEventNotificationService subscrContEventNotificationService;

	private final ContEventMonitorService contEventMonitorService;

	private final ContEventTypeService contEventTypeService;

	private final SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	private final ContEventLevelColorRepository contEventLevelColorRepository;

	private final ContObjectService contObjectService;

	private final ContObjectFiasService contObjectFiasService;

	private final ObjectAccessService objectAccessService;

	private final ContObjectMapper contObjectMapper;

    public SubscrContEventNotificationStatusService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorService contEventMonitorService, ContEventTypeService contEventTypeService, SubscrContEventNotificationRepository subscrContEventNotificationRepository, ContEventLevelColorRepository contEventLevelColorRepository, ContObjectService contObjectService, ContObjectFiasService contObjectFiasService, ObjectAccessService objectAccessService, ContObjectMapper contObjectMapper) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorService = contEventMonitorService;
        this.contEventTypeService = contEventTypeService;
        this.subscrContEventNotificationRepository = subscrContEventNotificationRepository;
        this.contEventLevelColorRepository = contEventLevelColorRepository;
        this.contObjectService = contObjectService;
        this.contObjectFiasService = contObjectFiasService;
        this.objectAccessService = objectAccessService;
        this.contObjectMapper = contObjectMapper;
    }

    /**
	 *
	 * @return
	 */
	@Deprecated
	@Transactional( readOnly = true)
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
	 * @param portalUserIds
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<CityMonitorContEventsStatus> selectCityMonitoryContEventsStatus(final PortalUserIds portalUserIds,
			final List<ContObject> contObjects, final LocalDatePeriod datePeriod, Boolean noGreenColor) {

		List<MonitorContEventNotificationStatus> resultObjects = selectMonitorContEventNotificationStatusCollapse(
				portalUserIds, contObjects, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatus> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatus.FACTORY_INSTANCE);

		Map<UUID, Long> cityEventCount = contEventMonitorService
				.selectCityContObjectMonitorEventCount(portalUserIds.getSubscriberId());

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
	@Transactional( readOnly = true)
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

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(contObjectMapper.toDto(co),
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
	@Transactional( readOnly = true)
	public List<MonitorContEventNotificationStatus> selectMonitorContEventNotificationStatusCollapse(
        final PortalUserIds portalUserIds, final List<ContObject> contObjects, final LocalDatePeriod datePeriod,
        Boolean noGreenColor) {
		checkNotNull(portalUserIds);
		checkNotNull(contObjects);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		if (contObjectIds.isEmpty()) {
			contObjectIds = RepositoryUtil.NO_DATA_IDS;
		}

		CounterInfoMap allMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContEventNotificationCounterInfo(portalUserIds.getSubscriberId(), contObjectIds, datePeriod));

		CounterInfoMap allNewMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContEventNotificationCounterInfo(
						portalUserIds.getSubscriberId(), contObjectIds, datePeriod, Boolean.TRUE));

		CounterInfoMap contallEventTypesMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContObjectEventTypeGroupCollapseCounterInfo(
						portalUserIds.getSubscriberId(), contObjectIds, datePeriod));

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

			MonitorContEventNotificationStatus item = MonitorContEventNotificationStatus.newInstance(contObjectMapper.toDto(co),
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
     * @param portalUserIds
     * @param contObjectId
     * @param datePeriod
     * @return
     */
	@Transactional( readOnly = true)
	public List<MonitorContEventTypeStatus> selectMonitorContEventTypeStatusCollapse(
			final PortalUserIds portalUserIds, final Long contObjectId, final LocalDatePeriod datePeriod) {

		checkNotNull(contObjectId);
		checkNotNull(portalUserIds);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		List<Object[]> selectResult = subscrContEventNotificationRepository.selectNotificationEventTypeCountCollapse(
				portalUserIds.getSubscriberId(), contObjectId, datePeriod.getDateFrom(), datePeriod.getDateTo());

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
