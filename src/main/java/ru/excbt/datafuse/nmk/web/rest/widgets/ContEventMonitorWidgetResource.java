package ru.excbt.datafuse.nmk.web.rest.widgets;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.data.service.widget.ContEventMonitorWidgetService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/widgets/cont-event-monitor")
public class ContEventMonitorWidgetResource {

    private final static List<Long> EMPTY_LIST = Collections.unmodifiableList(Collections.EMPTY_LIST);

    private final CurrentSubscriberService currentSubscriberService;

    private final ObjectAccessService objectAccessService;

    private final ContEventMonitorWidgetService monitorWidgetService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    private final ContEventTypeService contEventTypeService;

    public ContEventMonitorWidgetResource(CurrentSubscriberService currentSubscriberService, ObjectAccessService objectAccessService, ContEventMonitorWidgetService monitorWidgetService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, ContEventTypeService contEventTypeService) {
        this.currentSubscriberService = currentSubscriberService;
        this.objectAccessService = objectAccessService;
        this.monitorWidgetService = monitorWidgetService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
        this.contEventTypeService = contEventTypeService;
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

        PortalUserIds portalUserIds = currentSubscriberService.getSubscriberParam();
        List<Long> contObjectIds = nodeId != null ? subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(portalUserIds, nodeId) : EMPTY_LIST;
        return ApiActionTool.processResponceApiActionOk(() -> monitorWidgetService.loadMonitorData(i -> contObjectIds.isEmpty() || contObjectIds.contains(i), Boolean.TRUE.equals(nestedTypes)));
    }

    /**
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "Get cont event monitor stats for PTreeNode")
    @GetMapping("/cont-event-types")
    public ResponseEntity<?> getContEventTypes() {
        return ApiActionTool.processResponceApiActionOk(() -> monitorWidgetService.findMonitorContEventTypes());
    }


}
