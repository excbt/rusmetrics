package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNodeType;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
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

    private final ObjectAccessService objectAccessService;

    public PTreeNodeMonitorService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorV2Service contEventMonitorV2Service, ObjectAccessService objectAccessService) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorV2Service = contEventMonitorV2Service;
        this.objectAccessService = objectAccessService;
    }


    /**
     * Read Monitor status for ContObjects
     * @param portalUserIds
     * @param contObjectIds
     * @param dateInterval
     * @param noGreenColor
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorCO(final PortalUserIds portalUserIds,
                                                            final List<Long> contObjectIds,
                                                            final DateInterval dateInterval,
                                                            final Boolean noGreenColor) {

        checkNotNull(portalUserIds);
        checkNotNull(contObjectIds);

        if (contObjectIds.isEmpty()) {
            log.warn("contObjects is empty");
            return Collections.emptyList();
        }

        List<Long> qryContObjectIds = contObjectIds;

        // Second check. For safe only
        if (qryContObjectIds.isEmpty()) {
            qryContObjectIds = RepositoryUtil.NO_DATA_IDS;
        }

        if (CALC_CNT_MODE && dateInterval != null) {
            CounterInfoMap allNotificationsMap = new CounterInfoMap(subscrContEventNotificationService
                .selectContEventNotificationCounterInfo(portalUserIds.getSubscriberId(), qryContObjectIds, dateInterval));

            CounterInfoMap allNewNotificationsMap = new CounterInfoMap(
                subscrContEventNotificationService.selectContEventNotificationCounterInfo(
                    portalUserIds.getSubscriberId(), qryContObjectIds, dateInterval, Boolean.TRUE));

            CounterInfoMap contallEventTypesMap = new CounterInfoMap(
                subscrContEventNotificationService.selectContObjectEventTypeGroupCollapseCounterInfo(
                    portalUserIds.getSubscriberId(), qryContObjectIds, dateInterval));
        }

        // Includes colors of notifications
        Map<Long, List<ContEventMonitorV2>> monitorContObjectsMap = contEventMonitorV2Service
            .getContObjectsContEventMonitorMap(qryContObjectIds);


        List<PTreeNodeMonitorDTO> monitorStatusList = new ArrayList<>();
        for (Long coId : qryContObjectIds) {

            List<ContEventMonitorV2> contObjectMonitors = monitorContObjectsMap.get(coId);

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

            PTreeNodeMonitorDTO item = new PTreeNodeMonitorDTO(PTreeNodeType.CONT_OBJECT, coId);

//            MonitorContEventNotificationStatusV2 item = MonitorContEventNotificationStatusV2.newInstance(co,
//                contObjectFiasMap.get(co.getId()), contObjectGeoPosMap.get(co.getId()));

//            item.setEventsCount(allCnt);
//            item.setNewEventsCount(newCnt);
//            item.setEventsTypesCount(typesCnt);
            item.setColorKey(resultColorKey);

            monitorStatusList.add(item);
        }

        return Boolean.TRUE.equals(noGreenColor)
            ? monitorStatusList.stream().filter((n) ->  n.getColorKey() != ContEventLevelColorKeyV2.GREEN).collect(Collectors.toList())
            : monitorStatusList;
    }

    /**
     *
     * @param portalUserIds
     * @param contZPointIds
     * @param dateInterval
     * @param noGreenColor
     * @param nodeMonitorDTOList
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorZP(final PortalUserIds portalUserIds,
                                                            final List<Long> contZPointIds,
                                                            final DateInterval dateInterval,
                                                            final Boolean noGreenColor,
                                                            final List<PTreeNodeMonitorDTO> nodeMonitorDTOList) {

        checkNotNull(portalUserIds);
        checkNotNull(contZPointIds);

        List<ContZPointIdPair> idPairList = objectAccessService.findAllContZPointPairIds(portalUserIds);

        List<PTreeNodeMonitorDTO> filteredList = nodeMonitorDTOList.stream().filter(i -> PTreeNodeType.CONT_OBJECT.equals(i.getNodeType())).collect(Collectors.toList());

        Map<Long, List<PTreeNodeMonitorDTO>> coMap = GroupUtil.makeIdMap(filteredList, (i) -> i.getMonitorObjectId());

        return idPairList.stream().map( z -> {
            PTreeNodeMonitorDTO item = new PTreeNodeMonitorDTO (PTreeNodeType.CONT_ZPOINT, z.getContZPointId());
            List<PTreeNodeMonitorDTO> cMon = coMap.get(z.getContObjectId());
            ContEventLevelColorKeyV2 colorKey = cMon == null || cMon.isEmpty() ? ContEventLevelColorKeyV2.GREEN :
                cMon.get(0).getColorKey();
            item.setColorKey(colorKey);
            return item;
        }).collect(Collectors.toList());
    }

    /**
     *
     * @param portalUserIds
     * @param nodeMonitorDTOList - monitor list of ContObjectStatus
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorZP(final PortalUserIds portalUserIds,
                                                            final List<PTreeNodeMonitorDTO> nodeMonitorDTOList) {

        checkNotNull(portalUserIds);
        checkNotNull(nodeMonitorDTOList);

        List<ContZPointIdPair> idPairList = objectAccessService.findAllContZPointPairIds(portalUserIds);

        List<PTreeNodeMonitorDTO> filteredList = nodeMonitorDTOList.stream().filter(i -> PTreeNodeType.CONT_OBJECT.equals(i.getNodeType())).collect(Collectors.toList());

        Map<Long, List<PTreeNodeMonitorDTO>> coMap = GroupUtil.makeIdMap(filteredList, (i) -> i.getMonitorObjectId());

        return idPairList.stream().map( z -> {
            PTreeNodeMonitorDTO item = new PTreeNodeMonitorDTO (PTreeNodeType.CONT_ZPOINT, z.getContZPointId());
            List<PTreeNodeMonitorDTO> cMon = coMap.get(z.getContObjectId());
            ContEventLevelColorKeyV2 colorKey = cMon == null || cMon.isEmpty() ? ContEventLevelColorKeyV2.GREEN :
                cMon.get(0).getColorKey();
            item.setColorKey(colorKey);
            return item;
        }).collect(Collectors.toList());
    }




}
