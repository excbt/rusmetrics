package ru.excbt.datafuse.nmk.web.rest;

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
    public ResponseEntity<?> getPTreeNode(@PathVariable("subscrObjectTreeId") Long subscrObjectTreeId,
                                          @RequestParam(name = "childLevel", required = false) Integer childLevel) {
        return ApiResponse.responseOK(() -> subscrObjectPTreeNodeService.readSubscrObjectTree(subscrObjectTreeId, childLevel));
    }


}
