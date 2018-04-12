package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.core.Tuple;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.model.support.CounterInfoMap;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNodeType;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.service.ContObjectQueryDSLUtil;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;
import ru.excbt.datafuse.nmk.service.vm.PTreeNodeMonitorColorStatus;
import ru.excbt.datafuse.nmk.service.vm.PTreeNodeMonitorColorStatusDetails;
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

    private final ContEventMonitorV3Service contEventMonitorV3Service;

    private final ObjectAccessService objectAccessService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

    private final QueryDSLService queryDSLService;

    private final QContEventMonitorV3 qContEventMonitorV3 = QContEventMonitorV3.contEventMonitorV3;


    @Getter
    @Setter
    public class PTreeNodeMonitorContObjectData {

        private Long contObjectId;
        private Long contZPointId;
        private ContEventLevelColorKeyV2 contEventLevelColorKey;

        public PTreeNodeMonitorContObjectData(Long contObjectId, ContEventLevelColorKeyV2 contEventLevelColorKey, Long contObjectCount) {
            this.contObjectId = contObjectId;
            this.contEventLevelColorKey = contEventLevelColorKey;
        }

        public PTreeNodeMonitorContObjectData(Long contObjectId, String colorKeyname) {
            this.contObjectId = contObjectId;
            this.contEventLevelColorKey = colorKeyname != null ? ContEventLevelColorKeyV2.findByKeyname(colorKeyname) : null;
        }

        public PTreeNodeMonitorContObjectData(Long contObjectId, Long contZPointId, String colorKeyname) {
            this.contObjectId = contObjectId;
            this.contZPointId = contZPointId;
            this.contEventLevelColorKey = colorKeyname != null ? ContEventLevelColorKeyV2.findByKeyname(colorKeyname) : null;
        }
    }

    public PTreeNodeMonitorService(SubscrContEventNotificationService subscrContEventNotificationService, ContEventMonitorV3Service contEventMonitorV3Service, ObjectAccessService objectAccessService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, QueryDSLService queryDSLService) {
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.contEventMonitorV3Service = contEventMonitorV3Service;
        this.objectAccessService = objectAccessService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.queryDSLService = queryDSLService;
    }


    /**
     * Read Monitor status for ContObjects. Old Version
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
        Map<Long, List<ContEventMonitorX>> monitorContObjectsMap = contEventMonitorV3Service
            .getContObjectsContEventMonitorMap(qryContObjectIds);


        List<PTreeNodeMonitorDTO> monitorStatusList = new ArrayList<>();
        for (Long coId : qryContObjectIds) {

            List<ContEventMonitorX> contObjectMonitors = monitorContObjectsMap.get(coId);

            ContEventLevelColorKeyV2 worseMonitorColorKey = null;

            if (contObjectMonitors != null) {
                worseMonitorColorKey = contEventMonitorV3Service.sortWorseColor(contObjectMonitors);
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
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorZP(final PortalUserIds portalUserIds,
                                                            final List<Long> contZPointIds,
                                                            final DateInterval dateInterval,
                                                            final Boolean noGreenColor) {

        checkNotNull(portalUserIds);
        checkNotNull(contZPointIds);

        if (contZPointIds.isEmpty()) {
            log.warn("contZPoint ids is empty");
            return Collections.emptyList();
        }

        List<Long> qryContZPointIds = contZPointIds;

        // Second check. For safe only
        if (qryContZPointIds.isEmpty()) {
            qryContZPointIds = RepositoryUtil.NO_DATA_IDS;
        }

        // Includes colors of notifications
        Map<Long, List<ContEventMonitorX>> monitorContZPointMap = contEventMonitorV3Service
            .getContZPointContEventMonitorMap(qryContZPointIds);


        final List<PTreeNodeMonitorDTO> monitorStatusList = new ArrayList<>();
        for (Long zpId : qryContZPointIds) {
            // Get All monitors of ContZPoint
            List<ContEventMonitorX> monitors = monitorContZPointMap.get(zpId);
            final ContEventLevelColorKeyV2 resultColorKey = monitors != null ? contEventMonitorV3Service.sortWorseColor(monitors) : ContEventLevelColorKeyV2.GREEN;
            monitorStatusList.add(new PTreeNodeMonitorDTO(PTreeNodeType.CONT_ZPOINT, zpId, resultColorKey));
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

    /**
     *
     * @param portalUserIds
     * @param nodeId
     * @param contObjectMonitorList
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitorElements(final PortalUserIds portalUserIds,
                                                                  final Long nodeId,
                                                                  final List<PTreeNodeMonitorDTO> contObjectMonitorList) {

        SubscrObjectTree node = subscrObjectTreeRepository.findOne(nodeId);
        if (node == null) {
            return Collections.emptyList();
        }
        return findPTreeNodeMonitorElements(portalUserIds, node, Collections.unmodifiableList(contObjectMonitorList));
    }

    /**
     *
     * @param portalUserIds
     * @param contZPointIdPairs
     * @param dateInterval
     * @param noGreenColor
     * @return
     */
    public List<PTreeNodeMonitorDTO> findPTreeNodeMonitor (final PortalUserIds portalUserIds,
                                                           final List<ContZPointIdPair> contZPointIdPairs,
                                                           final DateInterval dateInterval,
                                                           final Boolean noGreenColor) {

        List<Long> contObjectIds = contZPointIdPairs.stream().map(ContZPointIdPair::getContObjectId).distinct().collect(Collectors.toList());
        List<Long> contZPointIds = contZPointIdPairs.stream().map(ContZPointIdPair::getContZPointId).distinct().collect(Collectors.toList());

        List<PTreeNodeMonitorDTO> coMonitorDTOList = findPTreeNodeMonitorCO(portalUserIds, contObjectIds, dateInterval, noGreenColor);
        List<PTreeNodeMonitorDTO> zpMonitorDTOList = findPTreeNodeMonitorZP(portalUserIds, contZPointIds, dateInterval, noGreenColor);
        List<PTreeNodeMonitorDTO> result = new ArrayList<>();
        result.addAll(coMonitorDTOList);
        result.addAll(zpMonitorDTOList);
        return result;
    }


    /**
     *
     * @param inNodeData
     * @param nodeContObjectIds
     * @return
     */
    private List<PTreeNodeMonitorColorStatus> processMonitorData(List<PTreeNodeMonitorContObjectData> inNodeData,
                                                                 List<Long> nodeContObjectIds) {

        Map<Long, List<PTreeNodeMonitorContObjectData>> colorMapList = GroupUtil.makeIdMap(inNodeData, PTreeNodeMonitorContObjectData::getContObjectId);

        Map<Long, ContEventLevelColorKeyV2> contObjectColor = new HashMap<>();
        Map<String, PTreeNodeMonitorColorStatus> colorStatsMap = new HashMap<>();

        colorMapList.forEach((id, l) -> {

            Optional<ContEventLevelColorKeyV2> optionalColorKey = l.stream().filter(i -> Objects.nonNull(i.getContEventLevelColorKey()))
                .sorted(Comparator.comparingInt(e -> e.getContEventLevelColorKey().getColorRank()))
                .findFirst()
                .map(i -> i.getContEventLevelColorKey());

            optionalColorKey.ifPresent(colorKey -> {
                contObjectColor.put(id, colorKey);

                colorStatsMap.putIfAbsent(colorKey.getKeyname(), new PTreeNodeMonitorColorStatus(colorKey.getKeyname()));
                colorStatsMap.get(colorKey.getKeyname()).incCount();
            });

        });

        List<Long> outMonitorIds = nodeContObjectIds.stream().distinct().filter(i -> !contObjectColor.keySet().contains(i)).collect(Collectors.toList());

        List<PTreeNodeMonitorColorStatus> resultColorStats = new ArrayList<>(colorStatsMap.values());
        resultColorStats.add(new PTreeNodeMonitorColorStatus(ContEventLevelColorKeyV2.GREEN.getKeyname(), outMonitorIds.size()));

        return resultColorStats;
    }


    /**
     *
     * @param inData
     * @param contServiceTypeKey
     * @return
     */
    private List<PTreeNodeMonitorContObjectData> filterMonitorContObjectData(final List<PTreeNodeMonitorContObjectData> inData,
                                                                             final ContServiceTypeKey contServiceTypeKey) {
        List<Long> contZPointIds = inData.stream().map(PTreeNodeMonitorContObjectData::getContZPointId).distinct().collect(Collectors.toList());

        QContZPoint qContZPoint = QContZPoint.contZPoint;

        // Take contZPointId of requested contServiceType
        List<Long> filteredContZPointId = queryDSLService.queryFactory()
            .select(qContZPoint.id, qContZPoint.contServiceTypeKeyname).from(qContZPoint).where(qContZPoint.id.in(contZPointIds))
            .fetch().stream()
            .filter(t -> contServiceTypeKey.getKeyname().equals(t.get(qContZPoint.contServiceTypeKeyname)))
            .map(t -> t.get(qContZPoint.id))
            .collect(Collectors.toList());

        return inData.stream()
            .filter(i -> filteredContZPointId.contains(i.contZPointId)).collect(Collectors.toList());
    }

    /**
     *
     * @param portalUserIds
     * @param nodeId
     * @param contServiceTypeKey
     * @return
     */
    @Transactional(readOnly = true)
    public List<PTreeNodeMonitorColorStatus> findNodeColorStatus (final PortalUserIds portalUserIds, final Long nodeId, final Optional<ContServiceTypeKey> contServiceTypeKey) {

        Objects.requireNonNull(portalUserIds);
        Objects.requireNonNull(nodeId);
        Objects.requireNonNull(contServiceTypeKey);

        List<Long> nodeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId);

        List<Tuple> monitorQry = queryDSLService.queryFactory().select(
                                    qContEventMonitorV3.contObjectId,
                                    qContEventMonitorV3.contZPointId,
                                    qContEventMonitorV3.contEventLevelColor().colorKey
                                ).from(qContEventMonitorV3).where(qContEventMonitorV3.contObjectId.in(nodeContObjectIds))
                                .fetch();

        List<PTreeNodeMonitorContObjectData> allMonitorContObjectData = monitorQry.stream()
            .map(i ->
                new PTreeNodeMonitorContObjectData(
                    i.get(qContEventMonitorV3.contObjectId),
                    i.get(qContEventMonitorV3.contZPointId),
                    i.get(qContEventMonitorV3.contEventLevelColor().colorKey)
                )).collect(Collectors.toList());

        List<PTreeNodeMonitorContObjectData> filteredMonitorContObjectData =
            contServiceTypeKey.isPresent() ? filterMonitorContObjectData(allMonitorContObjectData, contServiceTypeKey.get()) : allMonitorContObjectData;

        List<PTreeNodeMonitorColorStatus> colorStatsList = processMonitorData(filteredMonitorContObjectData, nodeContObjectIds);
        return colorStatsList;

    }

    /**
     *
     * @param portalUserIds
     * @param nodeId
     * @param contServiceTypeKey
     * @return
     */
    @Transactional(readOnly = true)
    public PTreeNodeMonitorColorStatusDetails findNodeStatusDetails (final PortalUserIds portalUserIds,
                                                                     final Long nodeId,
                                                                     final ContEventLevelColorKeyV2 levelColorKey,
                                                                     final Optional<ContServiceTypeKey> contServiceTypeKey
                                             ) {

        Objects.requireNonNull(portalUserIds);
        Objects.requireNonNull(nodeId);
        Objects.requireNonNull(contServiceTypeKey);

        List<Long> nodeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId);

        List<Tuple> monitorQry = queryDSLService.queryFactory().select(
                                    qContEventMonitorV3.contObjectId,
                                    qContEventMonitorV3.contZPointId,
                                    qContEventMonitorV3.contEventLevelColor().colorKey
                                ).from(qContEventMonitorV3).where(qContEventMonitorV3.contObjectId.in(nodeContObjectIds))
                                .fetch();

        List<PTreeNodeMonitorContObjectData> allMonitorContObjectData = monitorQry.stream()
            .map(i ->
                new PTreeNodeMonitorContObjectData(
                    i.get(qContEventMonitorV3.contObjectId),
                    i.get(qContEventMonitorV3.contZPointId),
                    i.get(qContEventMonitorV3.contEventLevelColor().colorKey)
                )).collect(Collectors.toList());

        List<Long> contObjectIds;

        if (ContEventLevelColorKeyV2.GREEN.equals(levelColorKey)) {
            // Get No Green Ids
            List<Long> noGreenIds = allMonitorContObjectData.stream().map(PTreeNodeMonitorContObjectData::getContObjectId).distinct().collect(Collectors.toList());

            // Get all ids for specified contServiceType
            List<Long> allNodeContObjectIdsFiltered = contServiceTypeKey
                .map(k -> ContObjectQueryDSLUtil.filterContObjectIdByContServiceType(queryDSLService.queryFactory(), nodeContObjectIds, k))
                .orElse(nodeContObjectIds);

            // Filter allNodeContObjectIdsFiltered by noGreenIds
            contObjectIds = allNodeContObjectIdsFiltered.stream().filter(id -> !noGreenIds.contains(id)).collect(Collectors.toList());
        } else {
            List<PTreeNodeMonitorContObjectData> filteredMonitorContObjectData =
                contServiceTypeKey.isPresent() ? filterMonitorContObjectData(allMonitorContObjectData, contServiceTypeKey.get()) : allMonitorContObjectData;

            contObjectIds = filteredMonitorContObjectData.stream()
                .filter(i -> levelColorKey.equals(i.contEventLevelColorKey))
                .map(PTreeNodeMonitorContObjectData::getContObjectId).distinct().collect(Collectors.toList());

        }

        return new PTreeNodeMonitorColorStatusDetails(contObjectIds);
    }

}
