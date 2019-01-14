package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjects;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventNotificationStatusV2;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;

@Service
public class SubscrContEventNotificationStatusV2Service {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscrContEventNotificationStatusV2Service.class);

	private final SubscrContEventNotificationService subscrContEventNotificationService;

	private final ContEventMonitorV3Service contEventMonitorV3Service;

	private final ContObjectService contObjectService;

	private final ContObjectFiasService contObjectFiasService;

	@Autowired
    public SubscrContEventNotificationStatusV2Service(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorV3Service contEventMonitorV3Service, ContObjectService contObjectService, ContObjectFiasService contObjectFiasService) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorV3Service = contEventMonitorV3Service;
        this.contObjectService = contObjectService;
        this.contObjectFiasService = contObjectFiasService;
    }

    /**
	 *
	 * @param contObjects
	 * @param portalUserIds
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CityMonitorContEventsStatusV2> selectCityMonitoryContEventsStatusV2(
        final PortalUserIds portalUserIds, final List<ContObject> contObjects, final LocalDatePeriod datePeriod,
        Boolean noGreenColor) {

	    if (contObjects.isEmpty()) {
	        return new ArrayList<>();
        }

		List<MonitorContEventNotificationStatusV2> resultObjects = selectMonitorContEventNotificationStatusCollapseV2(
				portalUserIds, contObjects, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatusV2> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatusV2.FACTORY_INSTANCE);

		// Calculate all city contEventCount
		final Map<UUID, Long> cityEventCount = contEventMonitorV3Service
				.selectCityContObjectMonitorEventCount(portalUserIds);

		result.forEach((i) -> {
			Long cnt = cityEventCount.get(i.getCityFiasUUID());
			i.setMonitorEventCount(cnt != null ? cnt : 0);
		});

		return result;
	}

	/**
	 *
	 * @param portalUserIds
	 * @param contObjects
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	//@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<MonitorContEventNotificationStatusV2> selectMonitorContEventNotificationStatusCollapseV2(
			final PortalUserIds portalUserIds, final List<ContObject> contObjects, final LocalDatePeriod datePeriod,
			Boolean noGreenColor) {
		checkNotNull(portalUserIds);
		checkNotNull(contObjects);
		checkNotNull(datePeriod);
		checkState(datePeriod.isValidEq());

		if (contObjects.isEmpty()) {
			LOGGER.warn("contObjects is empty");
			return new ArrayList<>();
		}

		List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

		// Second check. For safe only
		if (contObjectIds.isEmpty()) {
			contObjectIds = RepositoryUtil.NO_DATA_IDS;
		}

		CounterInfoMap allNotificationsMap = new CounterInfoMap(subscrContEventNotificationService
				.selectContEventNotificationCounterInfo(portalUserIds.getSubscriberId(), contObjectIds, datePeriod));

		CounterInfoMap allNewNotificationsMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContEventNotificationCounterInfo(
						portalUserIds.getSubscriberId(), contObjectIds, datePeriod, Boolean.TRUE));

		CounterInfoMap contallEventTypesMap = new CounterInfoMap(
				subscrContEventNotificationService.selectContObjectEventTypeGroupCollapseCounterInfo(
						portalUserIds.getSubscriberId(), contObjectIds, datePeriod));

		Map<Long, List<ContEventMonitorX>> monitorContObjectsMap = contEventMonitorV3Service
				.getContObjectsContEventMonitorMap(contObjectIds);

		Map<Long, ContObjectFias> contObjectFiasMap = contObjectFiasService.selectContObjectsFiasMap(contObjectIds);
		Map<Long, ContObjectGeoPos> contObjectGeoPosMap = contObjectService.selectContObjectsGeoPosMap(contObjectIds);

		List<MonitorContEventNotificationStatusV2> monitorStatusList = new ArrayList<>();
		for (ContObject co : contObjects) {

			List<ContEventMonitorX> contObjectMonitors = monitorContObjectsMap.get(co.getId());

			ContEventLevelColorKeyV2 worseMonitorColorKey = null;

			if (contObjectMonitors != null) {
                worseMonitorColorKey = contEventMonitorV3Service.sortWorseColor(contObjectMonitors);
			}

			final long allCnt = allNotificationsMap.getCountValue(co.getId());
			final long newCnt = allCnt > 0 ? allNewNotificationsMap.getCountValue(co.getId()) : 0;
			final long typesCnt = allCnt > 0 ? contallEventTypesMap.getCountValue(co.getId()) : 0;
			final ContEventLevelColorKeyV2 resultColorKey = worseMonitorColorKey != null ? worseMonitorColorKey
					: ContEventLevelColorKeyV2.GREEN;

			MonitorContEventNotificationStatusV2 item = MonitorContEventNotificationStatusV2.newInstance(co,
					contObjectFiasMap.get(co.getId()), contObjectGeoPosMap.get(co.getId()));

			item.setEventsCount(allCnt);
			item.setNewEventsCount(newCnt);
			item.setEventsTypesCount(typesCnt);
			item.setContEventLevelColorKey(resultColorKey);

			monitorStatusList.add(item);
		}

		List<MonitorContEventNotificationStatusV2> resultList = null;

		if (Boolean.TRUE.equals(noGreenColor)) {
			resultList = monitorStatusList.stream()
					.filter((n) -> n.getContEventLevelColorKey() != ContEventLevelColorKeyV2.GREEN)
					.collect(Collectors.toList());
		} else {
			resultList = monitorStatusList;
		}

		return resultList;
	}

}
