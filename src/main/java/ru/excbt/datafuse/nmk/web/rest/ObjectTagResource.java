package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagInfoDTO;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.*;
import java.util.stream.Collectors;

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
    @GetMapping("/{objectTagKeynameUrl}")
    @ApiOperation("Get all tags for cont-object")
    @Timed
    public ResponseEntity<?> getContObjectsTags(@PathVariable("objectTagKeynameUrl")
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
    @GetMapping("/{objectTagKeynameUrl}/{objectId}")
    @ApiOperation("Get all tags for objectTagKeyname")
    @Timed
    public ResponseEntity<?> getContObjectTagsByObject(@PathVariable("objectTagKeynameUrl")
                                                 @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                         String objectTagKeynameUrl,
                                                        @PathVariable("objectId") Long objectId) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        List<ObjectTagDTO> resultTags = objectTagService.findObjectTags(
            supportedObjectTagMap.get(objectTagKeynameUrl),
            objectId,
            portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK(resultTags);

    }


    @GetMapping("/{objectTagKeynameUrl}/tag-names")
    @ApiOperation("Get all tags for cont-object")
    @Timed
    public ResponseEntity<?> getContObjectsTagNames(@PathVariable("objectTagKeynameUrl")
                                                @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                    String objectTagKeynameUrl) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        List<String> resultTagNames = objectTagService.findAllObjectsTagNames(
            supportedObjectTagMap.get(objectTagKeynameUrl),
            portalUserIdsService.getCurrentIds());

        Map<String,Object> result = new HashMap<>();
        result.put("tagNames", resultTagNames);
        result.put("objectTagKeyname", supportedObjectTagMap.get(objectTagKeynameUrl));

        return ApiResponse.responseOK(result);

    }



    /**
     *
     * @return
     */
    @PostMapping("/{objectTagKeynameUrl}")
    @ApiOperation("Creates tag for objectTagKeyname")
    @Timed
    public ResponseEntity<?> postContObjectTag(@PathVariable("objectTagKeynameUrl")
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
    @Timed
    public ResponseEntity<?> putContObjectTag(
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


    @GetMapping("/{objectTagKeynameUrl}/tag-info")
    @ApiOperation("Get all tags info for objectTagKeyname")
    @Timed
    public ResponseEntity<?> getAllObjectTagInfo(@PathVariable("objectTagKeynameUrl")
                                                    @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                        String objectTagKeynameUrl) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        List<ObjectTagInfoDTO> tagInfoDTOS = objectTagService.findAllObjectTagInfo(portalUserIdsService.getCurrentIds());

        String objectTagKeyname = supportedObjectTagMap.get(objectTagKeynameUrl);

        List<ObjectTagInfoDTO> filteredTagInfoDTOS = tagInfoDTOS.stream().filter(i ->  objectTagKeyname.equalsIgnoreCase(i.getObjectTagKeyname())).collect(Collectors.toList());

        return ApiResponse.responseOK(filteredTagInfoDTOS);
    }


    @GetMapping("/{objectTagKeynameUrl}/tag-info/{tagName}")
    @ApiOperation("Get all tags info for objectTagKeyname")
    @Timed
    public ResponseEntity<?> getOneObjectTagInfo(@PathVariable("objectTagKeynameUrl")
                                                @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                  String objectTagKeynameUrl,
                                                 @PathVariable("tagName") String tagName) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        ObjectTagInfoDTO tagInfoDTO = new ObjectTagInfoDTO();
        String objectTagKeyname = supportedObjectTagMap.get(objectTagKeynameUrl);
        tagInfoDTO.setObjectTagKeyname(objectTagKeyname);
        tagInfoDTO.setTagName(tagName);

        ObjectTagInfoDTO resultTagInfoDTO = objectTagService.findOneObjectTagInfo(tagInfoDTO, portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK(resultTagInfoDTO);
    }


    @PutMapping("/{objectTagKeynameUrl}/tag-info")
    @ApiOperation("Get all tags info for objectTagKeyname")
    @Timed
    public ResponseEntity<?> putOneObjectTagInfo(@PathVariable("objectTagKeynameUrl")
                                                 @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                     String objectTagKeynameUrl,
                                                 @RequestBody ObjectTagInfoDTO objectTagInfoDTO) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        ObjectTagInfoDTO resultTagInfoDTO = objectTagService.saveOneObjectTagInfo(objectTagInfoDTO, portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK(resultTagInfoDTO);
    }


    @PutMapping("/{objectTagKeynameUrl}/tag-info-list")
    @ApiOperation("Get all tags info for objectTagKeyname")
    @Timed
    public ResponseEntity<?> putObjectTagInfoList(@PathVariable("objectTagKeynameUrl")
                                                   @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                       String objectTagKeynameUrl,
                                                   @RequestBody List<ObjectTagInfoDTO> objectTagInfoDTOs) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        List<ObjectTagInfoDTO> tagInfoDTOS = objectTagService.saveObjectTagInfo(objectTagInfoDTOs, portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK(tagInfoDTOS);
    }

    /**
     *
     * @param objectTagKeynameUrl
     * @return
     */
    @DeleteMapping("/{objectTagKeynameUrl}/tag-info/{tagName}")
    @ApiOperation("Get all tags info for objectTagKeyname")
    @Timed
    public ResponseEntity<?> deleteObjectTagInfo(@PathVariable("objectTagKeynameUrl")
                                                   @ApiParam("supported urls: cont-objects, cont-zpoints, device-objects")
                                                       String objectTagKeynameUrl,
                                                 @PathVariable("tagName") String tagName) {

        if (! supportedObjectTagMap.containsKey(objectTagKeynameUrl)) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Unsupported tag"));
        }

        ObjectTagInfoDTO tagInfoDTO = new ObjectTagInfoDTO();
        String objectTagKeyname = supportedObjectTagMap.get(objectTagKeynameUrl);
        tagInfoDTO.setObjectTagKeyname(objectTagKeyname);
        tagInfoDTO.setTagName(tagName);

        objectTagService.deleteObjectTagInfo(tagInfoDTO, portalUserIdsService.getCurrentIds());

        return ApiResponse.responseOK();
    }



}
