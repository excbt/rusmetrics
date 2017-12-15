package ru.excbt.datafuse.nmk.web.rest.widgets;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventCategoryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventTypeDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.dto.ContObjectMonitorStateDTO;
import ru.excbt.datafuse.nmk.service.widget.ContEventMonitorWidgetService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/widgets/cont-event-monitor")
public class ContEventMonitorWidgetResource {

    private final static List<Long> EMPTY_LIST = Collections.unmodifiableList(Collections.EMPTY_LIST);

    private final PortalUserIdsService portalUserIdsService;

    private final ContEventMonitorWidgetService monitorWidgetService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    @Autowired
    public ContEventMonitorWidgetResource(PortalUserIdsService portalUserIdsService, ContEventMonitorWidgetService monitorWidgetService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService) {
        this.portalUserIdsService = portalUserIdsService;
        this.monitorWidgetService = monitorWidgetService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
    }

    /**
     *
     * @param nodeId
     * @param nestedTypes
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event monitor stats for PTreeNode")
    @GetMapping("/p-tree-node/stats")
    public ResponseEntity<?> getStats(@ApiParam("nodeId of requested PTreeNode") @RequestParam(value = "nodeId", required = false) Long nodeId,
                                      @ApiParam("option for including ContEventType in widget") @RequestParam(value = "nestedTypes", required = false) Boolean nestedTypes) {
        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();
        List<Long> contObjectIds = nodeId != null ? subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId) : EMPTY_LIST;
        List<ContEventMonitorWidgetService.ContObjectEventInfo> contObjectStatsList = monitorWidgetService.loadMonitorData(i -> contObjectIds.isEmpty() || contObjectIds.contains(i), Boolean.TRUE.equals(nestedTypes));
        return ResponseEntity.ok(contObjectStatsList);
    }

    /**
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event types")
    @GetMapping("/cont-event-types")
    public ResponseEntity<?> getContEventTypes() {
        List<ContEventTypeDTO> contEventTypeDTOS = monitorWidgetService.findMonitorContEventTypes();
        return ResponseEntity.ok(contEventTypeDTOS);
    }

    /**
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event categories")
    @GetMapping("/cont-event-categories")
    public ResponseEntity<?> getContEventCategories() {
        List<ContEventCategoryDTO> contEventCategoryDTOS = monitorWidgetService.findMonitorContEventCategories();
        return ResponseEntity.ok(contEventCategoryDTOS);
    }


    @Timed
    @ApiOperation(value = "Get cont object monitor state")
    @GetMapping("/cont-objects/{contObjectId}/monitor-state")
    public ResponseEntity<?> getContObjectMonitorState(@PathVariable("contObjectId") @ApiParam("contObject id") Long contObjectId) {
        ContObjectMonitorStateDTO monitorStateDTO = monitorWidgetService.findContObjectMonitorState(contObjectId, portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok(monitorStateDTO);
    }


}
