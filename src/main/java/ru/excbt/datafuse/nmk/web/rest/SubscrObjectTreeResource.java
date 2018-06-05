package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.vm.ContObjectShortInfoVM;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeDataVM;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;
import ru.excbt.datafuse.nmk.web.ApiConst;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/subscr-object-trees")
public class SubscrObjectTreeResource {

    private final SubscrObjectTreeService subscrObjectTreeService;

    private final PortalUserIdsService portalUserIdsService;

    private final ContObjectService contObjectService;

    public SubscrObjectTreeResource(SubscrObjectTreeService subscrObjectTreeService, PortalUserIdsService portalUserIdsService, ContObjectService contObjectService) {
        this.subscrObjectTreeService = subscrObjectTreeService;
        this.portalUserIdsService = portalUserIdsService;
        this.contObjectService = contObjectService;
    }

    /**
     * Same as RmaSubscrObjectTreeController
     *
     * @param objectTreeType
     * @param rootSubscrObjectTreeId
     * @return
     */
    @RequestMapping(value = "/{objectTreeType}/{rootSubscrObjectTreeId}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
                                                 @PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        SubscrObjectTreeDTO result = subscrObjectTreeService.findSubscrObjectTreeDTO(rootSubscrObjectTreeId);

        if (!checkTreeType.get().getKeyname().equals(result.getObjectTreeType())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Same as RmaSubscrObjectTreeController
     *
     *
     * @param objectTreeType
     * @return
     */
    @GetMapping(value = "/{objectTreeType}")
    public ResponseEntity<?> getSubscrObjectTreeList(@PathVariable("objectTreeType") String objectTreeType) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        List<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVM(checkTreeType.get(), portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{objectTreeType}/{nodeId}")
    public ResponseEntity<?> deleteSubscrObjectTreeNode(@PathVariable("objectTreeType") String objectTreeType,
                                                        @PathVariable("nodeId") Long nodeId) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        boolean result = subscrObjectTreeService.deleteSubscrObjectTreeNode(nodeId, checkTreeType.get(), portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }


    @RequestMapping(value = "/{objectTreeType}/page", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getSubscrObjectTreePage(@PathVariable("objectTreeType") String objectTreeType,
                                                     @RequestParam(name = "searchString", required = false) String searchString,
                                                     Pageable pageable) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Page<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVMPage(portalUserIdsService.getCurrentIds(), subscriberId, checkTreeType.get(), searchString, pageable);
        return ResponseEntity.ok(result);
    }


    /**
     * Same as RmaSubscrObjectTreeController
     *
     *
     * @param objectTreeType
     * @return
     */
    @RequestMapping(value = "/{objectTreeType}/new", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> putSubscrObjectTreeList(@PathVariable("objectTreeType") String objectTreeType,
                                                     @RequestParam("newTreeName") String newTreeName) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }


        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService.addSubscrObjectTree(newTreeName, null, checkTreeType.get(), portalUserIdsService.getCurrentIds(), subscriberId);

        return resultTree.map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)) ;
    }


    @RequestMapping(value = "/{objectTreeType}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> putSubscrObjectTreeNode(@PathVariable("objectTreeType") String objectTreeType,
                                                     @RequestParam(value = "addMode", required = false) String addMode,
                                                     @RequestBody SubscrObjectTreeVM subscrObjectTreeVM) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }


        if (addMode == null) {
            addMode = SubscrObjectTreeService.ADD_MODE_CHILD;
        }

        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Optional<SubscrObjectTreeVM> resultTree = subscrObjectTreeVM.getId() == null
            ? subscrObjectTreeService.addSubscrObjectTreeNode(subscrObjectTreeVM, portalUserIdsService.getCurrentIds(), subscriberId, addMode)
            : subscrObjectTreeService.updateSubscrObjectTreeNode(subscrObjectTreeVM, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());

        return resultTree.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build()) ;
    }

    @GetMapping("/{objectTreeType}/cont-objects")
    public ResponseEntity<?> getContObjectShortInfo(@PathVariable("objectTreeType") String objectTreeType,
                                                    @RequestParam(name = "nodeId", required = false) Long nodeId) {

        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        List<ContObjectShortInfoVM> shortInfoVMList = contObjectService.findShortInfo(portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok(shortInfoVMList);
    }


    private Optional<ObjectTreeTypeKeyname> checkTreeType(String objectTreeType) {
        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1 ) {
            return Optional.empty();
        }

        return Optional.ofNullable(treeType);
    }

    @PutMapping(value = "/{objectTreeType}/add-cont-objects")
    public ResponseEntity<?> putObjectTreeAddContObjects(@PathVariable("objectTreeType") String objectTreeType,
                                                         @RequestParam(value = "rootNodeId") Long subscrObjectTreeId,
                                                         @RequestParam(value = "nodeId") Long nodeId,
                                                         @RequestBody SubscrObjectTreeDataVM dataVM) {
        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        boolean result = subscrObjectTreeService.addContObjectsToNode(subscrObjectTreeId, nodeId, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(), dataVM);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/{objectTreeType}/remove-cont-objects")
    public ResponseEntity<?> putObjectTreeRemoveContObjects(@PathVariable("objectTreeType") String objectTreeType,
                                                         @RequestParam(value = "rootNodeId") Long subscrObjectTreeId,
                                                         @RequestParam(value = "nodeId") Long nodeId,
                                                         @RequestBody SubscrObjectTreeDataVM dataVM) {
        Optional<ObjectTreeTypeKeyname> checkTreeType = checkTreeType((objectTreeType));
        if (!checkTreeType.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        boolean result = subscrObjectTreeService.removeContObjectsFromNode(subscrObjectTreeId, nodeId, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(), dataVM);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }



}
