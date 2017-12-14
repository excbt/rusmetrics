package ru.excbt.datafuse.nmk.web.rest.widgets;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.widget.ContEventMonitorWidgetService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

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
        return ApiActionTool.processResponceApiActionOk(() -> monitorWidgetService.loadMonitorData(i -> contObjectIds.isEmpty() || contObjectIds.contains(i), Boolean.TRUE.equals(nestedTypes)));
    }

    /**
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event types")
    @GetMapping("/cont-event-types")
    public ResponseEntity<?> getContEventTypes() {
        return ApiActionTool.processResponceApiActionOk(() -> monitorWidgetService.findMonitorContEventTypes());
    }

    /**
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event categories")
    @GetMapping("/cont-event-categories")
    public ResponseEntity<?> getContEventCategories() {
        return ApiActionTool.processResponceApiActionOk(() -> monitorWidgetService.findMonitorContEventCategories());
    }


}
