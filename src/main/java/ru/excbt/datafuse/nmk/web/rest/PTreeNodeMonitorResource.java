package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PTreeNodeMonitorService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.service.utils.ObjectAccessUtil;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/p-tree-node-monitor")
public class PTreeNodeMonitorResource {

    private final CurrentSubscriberService currentSubscriberServicel;

    private final PTreeNodeMonitorService pTreeNodeMonitorService;

    private final ObjectAccessService objectAccessService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    @Autowired
    public PTreeNodeMonitorResource(CurrentSubscriberService currentSubscriberServicel, PTreeNodeMonitorService pTreeNodeMonitorService, ObjectAccessService objectAccessService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService) {
        this.currentSubscriberServicel = currentSubscriberServicel;
        this.pTreeNodeMonitorService = pTreeNodeMonitorService;
        this.objectAccessService = objectAccessService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
    }


    @GetMapping("/all-linked-objects")
    @ApiOperation("Get all monitor status of cont objects")
    public ResponseEntity<?> getLinkedObjectsMonitor(@ApiParam("nodeId of requested objectMonitor") @RequestParam(value = "nodeId", required = false) Long nodeId) {

        if (nodeId == null) {
            return ApiResponse.responseNoContent();
        }

        PortalUserIds portalUserIds = currentSubscriberServicel.getSubscriberParam().asPortalUserIds();

        ObjectAccessUtil objectAccessUtil = objectAccessService.objectAccessUtil();
        List<Long> nodeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId);

        List<ContZPointIdPair> nodeZPoint = objectAccessService.findAllContZPointPairIds(portalUserIds).stream()
            .filter(i -> nodeContObjectIds.contains(i.getContObjectId())).collect(Collectors.toList());

        List<Long> monitorContObjectIds = nodeContObjectIds.stream()
            .filter(objectAccessUtil.checkContObjectId(portalUserIds)).collect(Collectors.toList());

        List<PTreeNodeMonitorDTO> resultList = new ArrayList<>();

        List<PTreeNodeMonitorDTO> coMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorCO(portalUserIds, monitorContObjectIds, null, false);
        List<PTreeNodeMonitorDTO> zpMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorZP(portalUserIds, nodeZPoint, coMonitorDTOList);
        resultList.addAll(coMonitorDTOList);
        resultList.addAll(zpMonitorDTOList);

        if (nodeId != null) {
            List<PTreeNodeMonitorDTO> elMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorElements(portalUserIds, nodeId, coMonitorDTOList);
            resultList.addAll(elMonitorDTOList);
        }

        return ApiResponse.responseOK(() -> resultList);
    }


}
