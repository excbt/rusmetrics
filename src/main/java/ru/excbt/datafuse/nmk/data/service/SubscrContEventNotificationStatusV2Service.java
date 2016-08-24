package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjects;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventNotificationStatusV2;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService.ContObjectCounterMap;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

@Service
public class SubscrContEventNotificationStatusV2Service extends AbstractService {

	@Autowired
	private SubscrContEventNotificationService subscrContEventNotificationService;

	@Autowired
	private ContEventMonitorV2Service contEventMonitorV2Service;

	/**
	 * 
	 * @param contObjects
	 * @param subscriberParam
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<CityMonitorContEventsStatusV2> selectCityMonitoryContEventsStatusV2(
			final SubscriberParam subscriberParam,
			final List<ContObject> contObjects, final LocalDatePeriod datePeriod, Boolean noGreenColor) {

		List<MonitorContEventNotificationStatusV2> resultObjects = selectMonitorContEventNotificationStatusCollapseV2(
				subscriberParam, contObjects, datePeriod, noGreenColor);

		List<CityMonitorContEventsStatusV2> result = CityContObjects.makeCityContObjects(resultObjects,
				CityMonitorContEventsStatusV2.FACTORY_INSTANCE);

		Map<UUID, Long> cityEventCount = contEventMonitorV2Service
				.selectCityContObjectMonitorEventCount(subscriberParam.getSubscriberId());

		result.forEach((i) -> {
			Long cnt = cityEventCount.get(i.getCityFiasUUID());
			i.setMonitorEventCount(cnt != null ? cnt : 0);
		});

		return new ArrayList<>();
	}

	/**
	 * 
	 * @param subscriberParam
	 * @param contObjects
	 * @param datePeriod
	 * @param noGreenColor
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MonitorContEventNotificationStatusV2> selectMonitorContEventNotificationStatusCollapseV2(
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

		ContObjectCounterMap allMap = new ContObjectCounterMap(
				subscrContEventNotificationService.selectContEventNotificationInfoList(
						subscriberParam.getSubscriberId(), contObjectIds, datePeriod));

		ContObjectCounterMap allNewMap = new ContObjectCounterMap(
				subscrContEventNotificationService.selectContEventNotificationInfoList(
						subscriberParam.getSubscriberId(), contObjectIds, datePeriod, Boolean.TRUE));

		ContObjectCounterMap contallEventTypesMap = new ContObjectCounterMap(
				subscrContEventNotificationService.selectContObjectEventTypeCountGroupInfoListCollapse(
						subscriberParam.getSubscriberId(), contObjectIds,
						datePeriod));

		Map<Long, List<ContEventMonitorV2>> monitorContObjectsMap = contEventMonitorV2Service
				.getContObjectsContEventMonitorMap(contObjectIds);

		List<MonitorContEventNotificationStatusV2> monitorStatusList = new ArrayList<>();
		for (ContObject co : contObjects) {

			List<ContEventMonitorV2> availableMonitors = monitorContObjectsMap.get(co.getId());

			ContEventLevelColorKeyV2 monitorColorKey = null;

			if (availableMonitors != null) {
				ContEventLevelColorV2 monitorColor = contEventMonitorV2Service.sortWorseColor(availableMonitors);
				monitorColorKey = contEventMonitorV2Service.getColorKey(monitorColor);
			}

			long allCnt = allMap.getCountValue(co.getId());
			long newCnt = 0;
			long typesCnt = 0;

			if (allCnt > 0) {
				newCnt = allNewMap.getCountValue(co.getId());
				typesCnt = contallEventTypesMap.getCountValue(co.getId());
			}

			ContEventLevelColorKeyV2 resultColorKey = monitorColorKey;
			if (resultColorKey == null) {
				resultColorKey = ContEventLevelColorKeyV2.GREEN;
			}

			MonitorContEventNotificationStatusV2 item = MonitorContEventNotificationStatusV2.newInstance(co);

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
