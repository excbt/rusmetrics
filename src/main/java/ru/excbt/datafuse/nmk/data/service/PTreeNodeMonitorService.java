package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional(readOnly = true)
public class PTreeNodeMonitorService {

    private static final Logger log = LoggerFactory.getLogger(PTreeNodeMonitorService.class);

    private static final boolean CALC_CNT_MODE = false;

    private final SubscrContEventNotificationService subscrContEventNotificationService;

    private final ContEventMonitorV2Service contEventMonitorV2Service;

    public PTreeNodeMonitorService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorV2Service contEventMonitorV2Service) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorV2Service = contEventMonitorV2Service;
    }


    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitor(final PortalUserIds portalUserIds, final List<ContObject> contObjects, final DateInterval dateInterval,
                                     Boolean noGreenColor) {

        checkNotNull(portalUserIds);
        checkNotNull(contObjects);
        checkNotNull(dateInterval);

        if (contObjects.isEmpty()) {
            log.warn("contObjects is empty");
            return Collections.emptyList();
        }

        List<Long> contObjectIds = contObjects.stream().map((i) -> i.getId()).collect(Collectors.toList());

        // Second check. For safe only
        if (contObjectIds.isEmpty()) {
            contObjectIds = RepositoryUtil.NO_DATA_IDS;
        }

        if (CALC_CNT_MODE) {
            CounterInfoMap allNotificationsMap = new CounterInfoMap(subscrContEventNotificationService
                .selectContEventNotificationCounterInfo(portalUserIds.getSubscriberId(), contObjectIds, dateInterval));

            CounterInfoMap allNewNotificationsMap = new CounterInfoMap(
                subscrContEventNotificationService.selectContEventNotificationCounterInfo(
                    portalUserIds.getSubscriberId(), contObjectIds, dateInterval, Boolean.TRUE));

            CounterInfoMap contallEventTypesMap = new CounterInfoMap(
                subscrContEventNotificationService.selectContObjectEventTypeGroupCollapseCounterInfo(
                    portalUserIds.getSubscriberId(), contObjectIds, dateInterval));
        }

        // Includes colors of notifications
        Map<Long, List<ContEventMonitorV2>> monitorContObjectsMap = contEventMonitorV2Service
            .getContObjectsContEventMonitorMap(contObjectIds);


        List<PTreeNodeMonitorDTO> monitorStatusList = new ArrayList<>();
        for (ContObject co : contObjects) {

            List<ContEventMonitorV2> contObjectMonitors = monitorContObjectsMap.get(co.getId());

            ContEventLevelColorKeyV2 worseMonitorColorKey = null;

            if (contObjectMonitors != null) {
                ContEventLevelColorV2 worseMonitorColor = contEventMonitorV2Service.sortWorseColor(contObjectMonitors);
                worseMonitorColorKey = ContEventLevelColorKeyV2.findByKeyname(worseMonitorColor);
            }

            // Comment counting notifications

//            final long allCnt = allNotificationsMap.getCountValue(co.getId());
//            final long newCnt = allCnt > 0 ? allNewNotificationsMap.getCountValue(co.getId()) : 0;
//            final long typesCnt = allCnt > 0 ? contallEventTypesMap.getCountValue(co.getId()) : 0;

            final ContEventLevelColorKeyV2 resultColorKey = worseMonitorColorKey != null ? worseMonitorColorKey
                : ContEventLevelColorKeyV2.GREEN;

            PTreeNodeMonitorDTO item = new PTreeNodeMonitorDTO();

//            MonitorContEventNotificationStatusV2 item = MonitorContEventNotificationStatusV2.newInstance(co,
//                contObjectFiasMap.get(co.getId()), contObjectGeoPosMap.get(co.getId()));

//            item.setEventsCount(allCnt);
//            item.setNewEventsCount(newCnt);
//            item.setEventsTypesCount(typesCnt);
            item.setColorKey(resultColorKey);
            item.setContObjectId(co.getId());

            monitorStatusList.add(item);
        }

        return Boolean.TRUE.equals(noGreenColor)
            ? monitorStatusList.stream().filter((n) ->  n.getColorKey() != ContEventLevelColorKeyV2.GREEN).collect(Collectors.toList())
            : monitorStatusList;
    }


}
