package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscr-object-trees")
public class SubscrObjectTreeResource {

    private final SubscrObjectTreeService subscrObjectTreeService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrObjectTreeResource(SubscrObjectTreeService subscrObjectTreeService, PortalUserIdsService portalUserIdsService) {
        this.subscrObjectTreeService = subscrObjectTreeService;
        this.portalUserIdsService = portalUserIdsService;
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

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
            return ApiResponse.responseBadRequest();
        }

        SubscrObjectTreeDTO result = subscrObjectTreeService.findSubscrObjectTreeDTO(rootSubscrObjectTreeId);
        return ApiResponse.responseOK(result);
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

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
            return ApiResponse.responseBadRequest();
        }

        List<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVM(portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(result);
    }

    @DeleteMapping(value = "/{objectTreeType}/{nodeId}")
    public ResponseEntity<?> deleteSubscrObjectTreeNode(@PathVariable("objectTreeType") String objectTreeType,
                                                        @PathVariable("nodeId") Long nodeId) {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
            return ApiResponse.responseBadRequest();
        }

        boolean result = subscrObjectTreeService.deleteSubscrObjectTreeNode(nodeId, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/{objectTreeType}/page", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getSubscrObjectTreePage(@PathVariable("objectTreeType") String objectTreeType,
                                                     @RequestParam(name = "searchString", required = false) String searchString,
                                                     Pageable pageable) {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
            return ApiResponse.responseBadRequest();
        }

        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Page<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVMPage(portalUserIdsService.getCurrentIds(), subscriberId, searchString, pageable);
        return ApiResponse.responseOK(result);
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

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1 || newTreeName == null || newTreeName.trim().isEmpty()) {
            return ApiResponse.responseBadRequest();
        }

        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService.addSubscrObjectTree(newTreeName, null, treeType, portalUserIdsService.getCurrentIds(), subscriberId);

        return resultTree.map(ApiResponse::responseOK).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)) ;
    }


    @RequestMapping(value = "/{objectTreeType}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> putSubscrObjectTreeNode(@PathVariable("objectTreeType") String objectTreeType,
                                                     @RequestParam(value = "addMode", required = false) String addMode,
                                                     @RequestBody SubscrObjectTreeVM subscrObjectTreeVM) {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1 ) {
            return ApiResponse.responseBadRequest();
        }

        if (addMode == null) {
            addMode = SubscrObjectTreeService.ADD_MODE_CHILD;
        }

        Long subscriberId = portalUserIdsService.getCurrentIds().getSubscriberId();

        Optional<SubscrObjectTreeVM> resultTree = subscrObjectTreeVM.getId() == null
            ? subscrObjectTreeService.addSubscrObjectTreeNode(subscrObjectTreeVM, treeType, portalUserIdsService.getCurrentIds(), subscriberId, addMode)
            : subscrObjectTreeService.updateSubscrObjectTreeNode(subscrObjectTreeVM, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());

        return resultTree.map(ApiResponse::responseOK).orElse(ResponseEntity.badRequest().build()) ;
    }


}
