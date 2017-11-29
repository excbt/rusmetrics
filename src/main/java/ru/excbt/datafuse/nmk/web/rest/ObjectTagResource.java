package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
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
            objectTagService.findTags("cont-object", portalUserIdsService.getCurrentIds()));
    }


    /**
     *
     * @return
     */
    @PostMapping("/cont-objects")
    public ResponseEntity<?> createContObjectTag(@RequestBody ObjectTagDTO objectTagDTO) {
        objectTagDTO.setObjectTagKeyname("cont-object");
        ObjectTagDTO savedDTO = objectTagService.saveTag(objectTagDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseCreated(savedDTO);
    }

    /**
     *
     * @param objectTagDTO
     * @return
     */
    @PutMapping("/cont-objects")
    public ResponseEntity<?> createOrDeleteContObjectTag(@RequestBody ObjectTagDTO objectTagDTO,
                                                  @RequestParam( name = "delete", required = false, defaultValue = "false") Boolean delete) {
        objectTagDTO.setObjectTagKeyname("cont-object");
        if (Boolean.TRUE.equals(delete)) {
            boolean result = objectTagService.deleteTag(objectTagDTO, portalUserIdsService.getCurrentIds());

            return result ? ApiResponse.responseOK() : ApiResponse.responseNoContent();
        }
        ObjectTagDTO savedDTO = objectTagService.saveTag(objectTagDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(savedDTO);
    }

}
