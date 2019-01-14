package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.service.vm.PTreeNodeMonitorColorStatus;
import ru.excbt.datafuse.nmk.service.vm.PTreeNodeMonitorColorStatusDetails;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/p-tree-node-monitor")
public class PTreeNodeMonitorResource {


    private final PortalUserIdsService portalUserIdsService;

    private final PTreeNodeMonitorService pTreeNodeMonitorService;

    private final ObjectAccessService objectAccessService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    @Autowired
    public PTreeNodeMonitorResource(PortalUserIdsService portalUserIdsService, PTreeNodeMonitorService pTreeNodeMonitorService, ObjectAccessService objectAccessService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService) {
        this.portalUserIdsService = portalUserIdsService;
        this.pTreeNodeMonitorService = pTreeNodeMonitorService;
        this.objectAccessService = objectAccessService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
    }


    @GetMapping("/all-linked-objects")
    @ApiOperation("Get all monitor status of cont objects")
    @Timed
    public ResponseEntity<?> getLinkedObjectsMonitor(@ApiParam("nodeId of requested objectMonitor") @RequestParam(value = "nodeId", required = false) Long nodeId) {

        if (nodeId == null) {
            return ApiResponse.responseNoContent();
        }

        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();

//        ObjectAccessUtil objectAccessUtil = objectAccessService.objectAccessUtil();

        List<Long> nodeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId);

        // Find monitorContObjectIds
//        List<Long> monitorContObjectIds = nodeContObjectIds.stream()
//            .filter(objectAccessUtil.checkContObjectId(portalUserIds)).collect(Collectors.toList());

        // Find monitorContZPointIds
        List<ContZPointIdPair> nodeZPointIdPairs = objectAccessService.findAllContZPointPairIds(portalUserIds).stream()
            .filter(i -> nodeContObjectIds.contains(i.getContObjectId())).collect(Collectors.toList());
        List<Long> monitorContZPointIds = nodeZPointIdPairs.stream().map(ContZPointIdPair::getContZPointId)
            // No Need check access because we take findAllContZPointPairIds fromobjectAccessService
            //.filter(objectAccessUtil.checkContZPointId(portalUserIds))
            .collect(Collectors.toList());

        // Make Result List
        List<PTreeNodeMonitorDTO> resultList = new ArrayList<>();

        List<PTreeNodeMonitorDTO> objectsMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitor(portalUserIds, nodeZPointIdPairs, null, false);

        resultList.addAll(objectsMonitorDTOList);

        if (nodeId != null) {
            List<PTreeNodeMonitorDTO> elMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorElements(portalUserIds, nodeId, objectsMonitorDTOList);
            resultList.addAll(elMonitorDTOList);
        }

        return ApiResponse.responseOK(() -> resultList);
    }

    @GetMapping("/node-color-status/{nodeId}")
    @ApiOperation("Get all monitor status of cont objects")
    @Timed
    public ResponseEntity getNodeColorStatus(
        @ApiParam("NodeId") @PathVariable("nodeId") Long nodeId,
        @ApiParam("ContServiceType: CW, HW, HEAT ...") @RequestParam(name = "contServiceType", required = false) String contServiceTypeKeyname) {


        Optional<ContServiceTypeKey> contServiceTypeKey = Optional.empty();
        if (contServiceTypeKeyname != null) {
            contServiceTypeKey = KeyEnumTool.searchName(ContServiceTypeKey.class, contServiceTypeKeyname.toUpperCase());
            if (!contServiceTypeKey.isPresent()) {
                return ApiResponse.responseBadRequest();
            }
        }

        List<PTreeNodeMonitorColorStatus> resultColorStatusList = pTreeNodeMonitorService.findNodeColorStatus(portalUserIdsService.getCurrentIds(), nodeId, contServiceTypeKey);

        return ResponseEntity.ok(resultColorStatusList);
    }

    @GetMapping("/node-color-status/{nodeId}/status-details/{levelColorKeyname}")
    @ApiOperation("Get all monitor status of cont objects")
    @Timed
    public ResponseEntity getNodeColorStatusDetails(
        @ApiParam("nodeId") @PathVariable("nodeId") Long nodeId,
        @ApiParam("levelColorKeyname") @PathVariable("levelColorKeyname") String levelColorKeyname,
        @ApiParam("ContServiceType: CW, HW, HEAT ...") @RequestParam(name = "contServiceType", required = false) String contServiceTypeKeyname) {


        Optional<ContEventLevelColorKeyV2> contEventLevelColorKey = KeyEnumTool.searchKey(ContEventLevelColorKeyV2.class, levelColorKeyname.toUpperCase());

        if (!contEventLevelColorKey.isPresent()) {
            return ApiResponse.responseBadRequest();
        }

        Optional<ContServiceTypeKey> contServiceTypeKey = Optional.empty();
        if (contServiceTypeKeyname != null) {
            contServiceTypeKey = KeyEnumTool.searchName(ContServiceTypeKey.class, contServiceTypeKeyname.toUpperCase());
            if (!contServiceTypeKey.isPresent()) {
                return ApiResponse.responseBadRequest();
            }
        }

        PTreeNodeMonitorColorStatusDetails resultColorStatsDetails = pTreeNodeMonitorService.findNodeStatusDetails(portalUserIdsService.getCurrentIds(), nodeId, contEventLevelColorKey.get(), contServiceTypeKey);

        return ResponseEntity.ok(resultColorStatsDetails);
    }

}
