package ru.excbt.datafuse.nmk.data.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV3;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorX;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorV3Repository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class ContEventMonitorV3Service {
    private static final Logger log = LoggerFactory.getLogger(ContEventMonitorV3Service.class);

    private final ContEventMonitorV3Repository contEventMonitorV3Repository;

    private final ContEventService contEventService;

    public ContEventMonitorV3Service(ContEventMonitorV3Repository contEventMonitorV3Repository, ContEventService contEventService) {
        this.contEventMonitorV3Repository = contEventMonitorV3Repository;
        this.contEventService = contEventService;
    }

    public final static Comparator<ContEventMonitorX> CMP_BY_COLOR_RANK = Comparator.comparingInt(e -> e.getContEventLevelColorKeyname() == null ? -1 :
        ContEventLevelColorKeyV2.findByKeyname(e.getContEventLevelColorKeyname()).getColorRank());


    /**
     *
     * @param contObjectIds
     * @return
     */
    public List<ContEventMonitorX> selectByContObjectIds(List<Long> contObjectIds) {
        checkNotNull(contObjectIds);

        if (contObjectIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ContEventMonitorX> result = contEventMonitorV3Repository.selectByContObjectIds(contObjectIds).stream().collect(Collectors.toList());

        return contEventService.loadContEventTypeModel(result);
    }

    /**
     *
     * @param contEventMonitor
     * @return
     */

    public <T extends ContEventMonitorX> ContEventLevelColorKeyV2 sortWorseColor(List<T> contEventMonitor) {

        checkNotNull(contEventMonitor);

        if (contEventMonitor.isEmpty()) {
            return null;
        }

        Optional<T> sorted = contEventMonitor.stream().filter(i -> i.getContEventLevel() != null)
            .sorted(CMP_BY_COLOR_RANK).findFirst();

        if (!sorted.isPresent()) {
            return null;
        }

        return ContEventLevelColorKeyV2.findByKeyname(sorted.get().getContEventLevelColorKeyname());
    }



    /**
     *
     * @param contObjectIds
     * @return
     */
    public Map<Long, List<ContEventMonitorV3>> getContObjectsContEventMonitorMap(List<Long> contObjectIds) {

        checkNotNull(contObjectIds);

        if (contObjectIds.isEmpty()) {
            return Collections.emptyMap();
        }

        final List<ContEventMonitorV3> rawMonitorList = contEventMonitorV3Repository.selectByContObjectIds(contObjectIds);

        List<ContEventMonitorV3> monitorList = contEventService.loadContEventTypeModel(rawMonitorList);

        Map<Long, List<ContEventMonitorV3>> resultMap = GroupUtil.makeIdMap(monitorList, (m) -> m.getContObjectId());

        return resultMap;
    }


    /**
     *
     * @param portalUserIds
     * @return
     */
    public Map<UUID, Long> selectCityContObjectMonitorEventCount(PortalUserIds portalUserIds) {

        List<Object[]> cityContEventCount = contEventMonitorV3Repository
            .selectCityContObjectMonitorEventCount(portalUserIds.getSubscriberId());

        Map<UUID, Long> resultMap = new HashMap<>();
        cityContEventCount.forEach((i) -> {
            String strUUID = DBRowUtil.asString(i[0]);
            UUID cityUUID = strUUID != null ? UUID.fromString(strUUID) : null;
            BigInteger count = DBRowUtil.asBigInteger(i[1]);
            resultMap.put(cityUUID, count.longValue());
        });

        return resultMap;
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    public List<ContEventMonitorX> selectByContObject(Long contObjectId) {
        checkNotNull(contObjectId);

        List<ContEventMonitorX> result = contEventMonitorV3Repository.selectByContObjectId(contObjectId).stream().collect(Collectors.toList());

        return contEventService.loadContEventTypeModel(result);
    }


    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
    public List<ContEventMonitorX> selectByContZPoint(Long contObjectId, Long contZPointId) {
        return contEventMonitorV3Repository.selectByZPointId(contObjectId, contZPointId).stream().collect(Collectors.toList());
    }

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
    public ContEventLevelColorKeyV2 findMonitorColor(Long contObjectId, Long contZPointId) {
        List<ContEventMonitorV3> mon = contEventMonitorV3Repository.selectByZPointId(contObjectId, contZPointId);
        return sortWorseColor(mon);
    }

}
