package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.PTreeNodeMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PTreeNodeMonitorService;
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

    @Autowired
    public PTreeNodeMonitorResource(CurrentSubscriberService currentSubscriberServicel, PTreeNodeMonitorService pTreeNodeMonitorService, ObjectAccessService objectAccessService) {
        this.currentSubscriberServicel = currentSubscriberServicel;
        this.pTreeNodeMonitorService = pTreeNodeMonitorService;
        this.objectAccessService = objectAccessService;
    }


    @GetMapping("/all-linked-objects")
    @ApiOperation("Get all monitor status of cont objects")
    public ResponseEntity<?> getLinkedObjectsMonitor(@RequestParam(value = "nodeId", required = false) Long nodeId) {

        PortalUserIds portalUserIds = currentSubscriberServicel.getSubscriberParam().asPortalUserIds();

        List<Long> availableContObjectIds = objectAccessService.findContObjectIds(portalUserIds);

        List<PTreeNodeMonitorDTO> resultList = new ArrayList<>();

        List<PTreeNodeMonitorDTO> coMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorCO(portalUserIds, availableContObjectIds, null, false);
        List<PTreeNodeMonitorDTO> zpMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorZP(portalUserIds, coMonitorDTOList);
        resultList.addAll(coMonitorDTOList);
        resultList.addAll(zpMonitorDTOList);

        if (nodeId != null) {
            List<PTreeNodeMonitorDTO> elMonitorDTOList = pTreeNodeMonitorService.findPTreeNodeMonitorElements(portalUserIds, nodeId, coMonitorDTOList);
            resultList.addAll(elMonitorDTOList);
        }

        return ApiResponse.responseOK(() -> resultList);
    }


}
