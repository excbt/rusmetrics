package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNodeType;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import java.util.*;
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

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

    public PTreeNodeMonitorService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorV2Service contEventMonitorV2Service, ObjectAccessService objectAccessService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorV2Service = contEventMonitorV2Service;
        this.objectAccessService = objectAccessService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
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
                                                            final List<ContZPointIdPair> contZPointIdPars,
                                                            final List<PTreeNodeMonitorDTO> nodeMonitorDTOList) {

        checkNotNull(portalUserIds);
        checkNotNull(nodeMonitorDTOList);


        List<PTreeNodeMonitorDTO> filteredList = nodeMonitorDTOList.stream().filter(i -> PTreeNodeType.CONT_OBJECT.equals(i.getNodeType())).collect(Collectors.toList());

        Map<Long, List<PTreeNodeMonitorDTO>> coMap = GroupUtil.makeIdMap(filteredList, (i) -> i.getMonitorObjectId());

        return contZPointIdPars.stream().map( z -> {
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
     * @param node
     * @param contObjectMonitorList
     * @return
     */
    private List<PTreeNodeMonitorDTO> findPTreeNodeMonitorElements(final PortalUserIds portalUserIds,
                                                                   final SubscrObjectTree node,
                                                                   final List<PTreeNodeMonitorDTO> contObjectMonitorList) {

        if (node == null) {
            return Collections.emptyList();
        }

        List<Long> contObjectIds = subscrObjectTreeContObjectRepository.selectContObjectIds(node.getId());

        List<PTreeNodeMonitorDTO> currentContObjectMons = contObjectMonitorList.stream()
            .filter(i -> PTreeNodeType.CONT_OBJECT.equals(i.getNodeType()) && contObjectIds.contains(i.getMonitorObjectId()))
            .collect(Collectors.toList());

        List<PTreeNodeMonitorDTO> resultList = new ArrayList<>();
        List<PTreeNodeMonitorDTO> childContObjectMons = new ArrayList<>();

        for (SubscrObjectTree childNode: node.getChildObjectList()) {
            List<PTreeNodeMonitorDTO> childResult = findPTreeNodeMonitorElements(portalUserIds, childNode, contObjectMonitorList);
            childContObjectMons.addAll(childResult);
        }

        List<PTreeNodeMonitorDTO> decisionList = new ArrayList<>();
        decisionList.addAll(childContObjectMons);
        decisionList.addAll(currentContObjectMons);

        // Sort by color rank
        ContEventLevelColorKeyV2 colorKey = decisionList.stream().filter(i -> Objects.nonNull(i.getColorKey()))
            .sorted(Comparator.comparingInt(e -> e.getColorKey().getColorRank()))
            .findFirst()
            .map(i -> i.getColorKey()).orElse(ContEventLevelColorKeyV2.GREEN);

        PTreeNodeMonitorDTO nodeStatus = new PTreeNodeMonitorDTO(PTreeNodeType.ELEMENT, node.getId());
        nodeStatus.setColorKey(colorKey);

        resultList.add(nodeStatus);
        resultList.addAll(childContObjectMons);

        return resultList;
    }

    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorElements(final PortalUserIds portalUserIds,
                                                                  final Long nodeId,
                                                                  final List<PTreeNodeMonitorDTO> contObjectMonitorList) {

        SubscrObjectTree node = subscrObjectTreeRepository.findOne(nodeId);
        if (node == null) {
            return Collections.emptyList();
        }
        return findPTreeNodeMonitorElements(portalUserIds, node, Collections.unmodifiableList(contObjectMonitorList));
    }


}
