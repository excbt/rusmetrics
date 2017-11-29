package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
@RequestMapping("/api/object-tags")
public class ObjectTagResource {

    private final ObjectTagService objectTagService;

    private final PortalUserIdsService portalUserIdsService;


    public ObjectTagResource(ObjectTagService objectTagService, PortalUserIdsService portalUserIdsService) {
        this.objectTagService = objectTagService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
     *
     * @return
     */
    @GetMapping("/cont-objects")
    public ResponseEntity<?> findContObjectsTags() {
        return ApiResponse.responseOK(() ->
            objectTagService.findObjectTags("cont-object", portalUserIdsService.getCurrentIds()));
    }
}
