package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectPTreeNodeService;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
@RequestMapping(value = "/api/p-tree-node")
public class SubscrObjectPTreeNodeResource extends AbstractSubscrApiResource {

    private final SubscrObjectPTreeNodeService subscrObjectPTreeNodeService;


    public SubscrObjectPTreeNodeResource(SubscrObjectPTreeNodeService subscrObjectPTreeNodeService) {
        this.subscrObjectPTreeNodeService = subscrObjectPTreeNodeService;
    }


    @GetMapping("/{subscrObjectTreeId}")
    @ApiOperation("Get PTreeNode view of subscrObjectTree")
    public ResponseEntity<?> getPTreeNode(@ApiParam("id of subscrObjectTree") @PathVariable("subscrObjectTreeId") Long subscrObjectTreeId,
                                          @ApiParam(name = "Level of request tree. It affects only for ELEMENT nodes.")
                                          @RequestParam(name = "childLevel", required = false) Integer childLevel) {
        return ApiResponse.responseOK(() ->
            subscrObjectPTreeNodeService.readSubscrObjectTree(subscrObjectTreeId, childLevel, getSubscriberParam().asPortalUserIds()));
    }


}
