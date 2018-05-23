package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.ContObjectAccessService;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;
import ru.excbt.datafuse.nmk.service.dto.ContZPointAccessDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.vm.ContObjectAccessVM;
import ru.excbt.datafuse.nmk.service.vm.ContZPointAccessVM;
import ru.excbt.datafuse.nmk.service.vm.SubscriberAccessStatsVM;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/subscr-access")
public class SubscrAccessResource {

    private final ObjectAccessService objectAccessService;
    private final ContObjectMapper contObjectMapper;
    private final ContObjectAccessRepository contObjectAccessRepository;
    private final ContObjectAccessService contObjectAccessService;
    private final ContObjectAccessMapper contObjectAccessMapper;
    private final PortalUserIdsService portalUserIdsService;

    public SubscrAccessResource(ObjectAccessService objectAccessService, ContObjectMapper contObjectMapper, ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessService contObjectAccessService, ContObjectAccessMapper contObjectAccessMapper, PortalUserIdsService portalUserIdsService) {
        this.objectAccessService = objectAccessService;
        this.contObjectMapper = contObjectMapper;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessService = contObjectAccessService;
        this.contObjectAccessMapper = contObjectAccessMapper;
        this.portalUserIdsService = portalUserIdsService;
    }

    @GetMapping("/cont-objects")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getContObjects() {
        List<ContObjectAccessDTO> contObjectAccessDTOS = contObjectAccessRepository.findBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId())
            .stream().map(contObjectAccessMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(contObjectAccessDTOS);
    }

    @GetMapping("/cont-objects/page")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getContObjectsPage(@RequestParam(name = "subscriberId", required = false) Long subscriberId,
                                                 @RequestParam(name = "searchString", required = false) String searchString,
                                                 @RequestParam(name = "addMode", required = false) Boolean addMode,
                                                 Pageable pageable) {

        Page<ContObjectAccessVM> contObjectAccessVMPage;
        if (Boolean.TRUE.equals(addMode) && subscriberId != null) {
            contObjectAccessVMPage = contObjectAccessService
                .findAvailableContObjectAccess(portalUserIdsService.getCurrentIds(),subscriberId, searchString, pageable);
        } else {
            contObjectAccessVMPage = contObjectAccessService.getContObjectAccessVMPage(portalUserIdsService.getCurrentIds(), subscriberId, searchString, pageable);
        }
        return ResponseEntity.ok(contObjectAccessVMPage);
    }

    @GetMapping("/cont-zpoints")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getContZPoints(@RequestParam(name = "subscriberId", required = false) Long subscriberId,
                                            @RequestParam(name = "contObjectId") Long contObjectId) {
        List<ContZPointAccessVM> contZPointAccessDTOS = contObjectAccessService.getContZPointAccessVM(portalUserIdsService.getCurrentIds(), subscriberId, contObjectId);
        return ResponseEntity.ok(contZPointAccessDTOS);
    }

    @GetMapping(value = "/subscriber-manage-list")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getSubscriberManageList() {

        List<SubscriberVM> resultList = contObjectAccessService.findSubscribersManageList(portalUserIdsService.getCurrentIds());

        return ResponseEntity.ok(resultList);
    }

    @GetMapping(value = "/available-cont-objects/page")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getAvailableContObjects(@RequestParam(value = "subscriberId") Long subscriberId,
                                                     @RequestParam(name = "searchString", required = false) String searchString,
                                                     Pageable pageable) {

        Page<ContObjectAccessVM> resultList = contObjectAccessService
            .findAvailableContObjectAccess(portalUserIdsService.getCurrentIds(),subscriberId, searchString, pageable);

        return ResponseEntity.ok(resultList);
    }


    @PutMapping("/cont-objects")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> grantRevokeContObject(@RequestParam(value = "subscriberId") Long subscriberId,
                                                   @RequestParam(value = "contObjectId") Long contObjectId,
                                                   @RequestParam(value = "action", required = false, defaultValue = "grant") String action) {

        if (action == null) {
            return ResponseEntity.badRequest().build();
        }

        String actionLower = action.toLowerCase();

        if (!"grant".equals(actionLower) && !"revoke".equals(actionLower)) {
            return ResponseEntity.badRequest().build();
        }

        if ("grant".equals(actionLower)) {
            boolean grantResult = contObjectAccessService.grantContObjectAccess(portalUserIdsService.getCurrentIds(), subscriberId, contObjectId);
            if (!grantResult) {
                return ResponseEntity.badRequest().build();
            }
        }

        if ("revoke".equals(actionLower)) {
            boolean revokeResult = contObjectAccessService.revokeContObjectAccess(portalUserIdsService.getCurrentIds(), subscriberId, contObjectId);
            if (!revokeResult) {
                return ResponseEntity.badRequest().build();
            }
        }

        return  ResponseEntity.ok().build();
    }

    @PutMapping("/cont-zpoints")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> grantRevokeContZPoint(@RequestParam(value = "subscriberId") Long subscriberId,
                                                   @RequestParam(value = "contZPointId") Long contZPointId,
                                                   @RequestParam(value = "action", required = false, defaultValue = "grant") String action) {

        if (action == null) {
            return ResponseEntity.badRequest().build();
        }

        String actionLower = action.toLowerCase();

        if (!"grant".equals(actionLower) && !"revoke".equals(actionLower)) {
            return ResponseEntity.badRequest().build();
        }

        if ("grant".equals(actionLower)) {
            boolean grantResult = contObjectAccessService.grantContZPointAccess(portalUserIdsService.getCurrentIds(), subscriberId, contZPointId);
            if (!grantResult) {
                return ResponseEntity.badRequest().build();
            }
        }

        if ("revoke".equals(actionLower)) {
            boolean revokeResult = contObjectAccessService.revokeContZPointAccess(portalUserIdsService.getCurrentIds(), subscriberId, contZPointId);
            if (!revokeResult) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/subscriber-access-stats")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getSubscriberAccessStats(@RequestParam(value = "subscriberId", required = false) Long subscriberId) {
        SubscriberAccessStatsVM result = contObjectAccessService.findSubscriberAccessStats(portalUserIdsService.getCurrentIds(), subscriberId);
        return ResponseEntity.ok(result);
    }


}
