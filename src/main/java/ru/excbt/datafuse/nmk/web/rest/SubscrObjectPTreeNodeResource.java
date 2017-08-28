package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<?> getPTreeNode(@PathVariable("subscrObjectTreeId") Long subscrObjectTreeId) {
        return ApiResponse.responseOK(() -> subscrObjectPTreeNodeService.readSubscrObjectTree(subscrObjectTreeId) );
    }


}
