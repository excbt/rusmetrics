package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        SubscrObjectTreeVM result = subscrObjectTreeService.findSubscrObjectTreeVM(rootSubscrObjectTreeId);
        return ApiResponse.responseOK(result);
    }

    /**
     * Same as RmaSubscrObjectTreeController
     *
     *
     * @param objectTreeType
     * @return
     */
    @RequestMapping(value = "/{objectTreeType}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getSubscrObjectTreeList(@PathVariable("objectTreeType") String objectTreeType) {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

        if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
            return ApiResponse.responseBadRequest();
        }

        List<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVM(portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(result);
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

        Page<SubscrObjectTreeVM> result = subscrObjectTreeService.selectSubscrObjectTreeShortVMPage(portalUserIdsService.getCurrentIds(), subscriberId, pageable);
        return ApiResponse.responseOK(result);
    }


}
