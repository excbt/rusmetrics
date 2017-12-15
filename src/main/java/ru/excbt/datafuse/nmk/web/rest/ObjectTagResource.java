package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation("Get all tags for cont-object")
    @GetMapping("/cont-objects")
    public ResponseEntity<?> findContObjectsTags() {
        return ApiResponse.responseOK(() ->
            objectTagService.findAllObjectsTags("cont-object", portalUserIdsService.getCurrentIds()));
    }


    /**
     *
     * @return
     */
    @PostMapping("/cont-objects")
    @ApiOperation("Creates tag for cont-object")
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
    @ApiOperation("Creates or deletes tag for cont-object")
    public ResponseEntity<?> createOrDeleteContObjectTag(@RequestBody ObjectTagDTO objectTagDTO,
                                                  @RequestParam( name = "delete", required = false, defaultValue = "false")
                                                  @ApiParam ("delete flag") Boolean delete) {
        objectTagDTO.setObjectTagKeyname("cont-object");
        if (Boolean.TRUE.equals(delete)) {
            boolean result = objectTagService.deleteTag(objectTagDTO, portalUserIdsService.getCurrentIds());

            return result ? ApiResponse.responseOK() : ApiResponse.responseNoContent();
        }
        ObjectTagDTO savedDTO = objectTagService.saveTag(objectTagDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(savedDTO);
    }

}
