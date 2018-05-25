package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
@RequestMapping("/api/subscr-object-tree")
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
}
