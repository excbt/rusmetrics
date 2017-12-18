package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.*;

@RestController
@RequestMapping("/api/object-tags")
public class ObjectTagResource {

    private final ObjectTagService objectTagService;

    private final PortalUserIdsService portalUserIdsService;

    private final static Map<String, String> supportedObjectTagMap;

    static {
        char s = 's';
        Map<String,String> preparationMap = new HashMap<>();
        preparationMap.put(ObjectTag.contObjectTagKeyname + s, ObjectTag.contObjectTagKeyname);
        preparationMap.put(ObjectTag.contZPointTagKeyname + s, ObjectTag.contZPointTagKeyname);
        preparationMap.put(ObjectTag.deviceObjectTagKeyname + s, ObjectTag.deviceObjectTagKeyname);
        supportedObjectTagMap = Collections.unmodifiableMap(preparationMap);

    }


    public ObjectTagResource(ObjectTagService objectTagService, PortalUserIdsService portalUserIdsService) {
        this.objectTagService = objectTagService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
     *
     * @return
     */
    @ApiOperation("Get all tags for cont-object")
    @GetMapping("/{objectTagKeynameUrl}")
    public ResponseEntity<?> findContObjectsTags(@PathVariable("objectTagKeynameUrl")
                                                 @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                         String objectTagKeynameUrl) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        List<ObjectTagDTO> resultTags = objectTagService.findAllObjectsTags(
            supportedObjectTagMap.get(objectTagKeynameUrl),
            portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK(resultTags);

    }


    /**
     *
     * @return
     */
    @ApiOperation("Creates tag for cont-object")
    @PostMapping("/{objectTagKeynameUrl}")
    public ResponseEntity<?> createContObjectTag(@PathVariable("objectTagKeynameUrl")
                                                 @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                         String objectTagKeynameUrl,
                                                 @RequestBody ObjectTagDTO objectTagDTO) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        objectTagDTO.setObjectTagKeyname(supportedObjectTagMap.get(objectTagKeynameUrl));
        ObjectTagDTO savedDTO = objectTagService.saveTag(
            objectTagDTO,
            portalUserIdsService.getCurrentIds());
        return ApiResponse.responseCreated(savedDTO);
    }

    /**
     *
     * @param objectTagDTO
     * @return
     */
    @PutMapping("/{objectTagKeynameUrl}")
    @ApiOperation("Creates or deletes tag for cont-object")
    public ResponseEntity<?> createOrDeleteContObjectTag(
                                                    @PathVariable("objectTagKeynameUrl")
                                                    @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                        String objectTagKeynameUrl,
                                                    @RequestBody ObjectTagDTO objectTagDTO,
                                                    @RequestParam( name = "delete", required = false, defaultValue = "false")
                                                    @ApiParam ("delete flag") Boolean delete) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        objectTagDTO.setObjectTagKeyname(supportedObjectTagMap.get(objectTagKeynameUrl));

        if (Boolean.TRUE.equals(delete)) {
            boolean result = objectTagService.deleteTag(
                objectTagDTO,
                portalUserIdsService.getCurrentIds());

            return result ? ApiResponse.responseOK() : ApiResponse.responseNoContent();
        }
        ObjectTagDTO savedDTO = objectTagService.saveTag(
                objectTagDTO,
                portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(savedDTO);
    }

}
