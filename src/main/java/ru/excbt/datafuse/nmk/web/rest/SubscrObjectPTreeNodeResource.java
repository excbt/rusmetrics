package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectPTreeNodeService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
@RequestMapping(value = "/api/p-tree-node")
public class SubscrObjectPTreeNodeResource {

    private final SubscrObjectPTreeNodeService subscrObjectPTreeNodeService;

    private final PortalUserIdsService portalUserIdsService;


    public SubscrObjectPTreeNodeResource(SubscrObjectPTreeNodeService subscrObjectPTreeNodeService, PortalUserIdsService portalUserIdsService) {
        this.subscrObjectPTreeNodeService = subscrObjectPTreeNodeService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
     *
     * @param subscrObjectTreeId
     * @param childLevel
     * @return
     */
    @GetMapping("/{subscrObjectTreeId}")
    @ApiOperation("Get PTreeNode view of subscrObjectTree")
    @Timed
    public ResponseEntity<?> getPTreeNode(@ApiParam("id of subscrObjectTree") @PathVariable("subscrObjectTreeId") Long subscrObjectTreeId,
                                          @ApiParam(name = "Level of request tree. It affects only for ELEMENT nodes.")
                                          @RequestParam(name = "childLevel", required = false) Integer childLevel) {
        return ApiResponse.responseOK(() ->
            subscrObjectPTreeNodeService.readSubscrObjectTree(subscrObjectTreeId, childLevel, portalUserIdsService.getCurrentIds()));
    }

    /**
     *
     * @param subscrObjectTreeId
     * @param childLevel
     * @return
     */
    @GetMapping("/{subscrObjectTreeId}/stub")
    @ApiOperation("Get PTreeNode view of subscrObjectTree")
    @Timed
    public ResponseEntity<?> getPTreeNodeStub(@ApiParam("id of subscrObjectTree") @PathVariable("subscrObjectTreeId") Long subscrObjectTreeId,
                                          @ApiParam(name = "Level of request tree. It affects only for ELEMENT nodes.")
                                          @RequestParam(name = "childLevel", required = false) Integer childLevel) {
        return ApiResponse.responseOK(() ->
            subscrObjectPTreeNodeService.readSubscrObjectTreeStub(subscrObjectTreeId, childLevel, portalUserIdsService.getCurrentIds()));
    }




}
