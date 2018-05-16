package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrStPlanService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/subscr-st-plans")
public class SubscrStPlanResource {

    private final SubscrStPlanService subscrStPlanService;

    private final PortalUserIdsService portalUserIdsService;

    @Autowired
    public SubscrStPlanResource(SubscrStPlanService subscrStPlanService, PortalUserIdsService portalUserIdsService) {
        this.subscrStPlanService = subscrStPlanService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
     *
     * @return
     */
    @GetMapping("")
    @ApiOperation("Get all StPlans of subscriber")
    @Timed
    public ResponseEntity<?> getStPlans() {
        return ApiResponse.responseOK(() -> subscrStPlanService.findStPlanDTOs(portalUserIdsService.getCurrentIds()));
    }

    @PostMapping("/new")
    @ApiOperation("Creates new StPlans of subscriber ans save it immediately")
    @Timed
    public ResponseEntity<?> createStPlanAndSave(HttpServletRequest request) {
        ApiActionProcess<SubscrStPlanDTO> action = () -> subscrStPlanService.newStPlanAndSave(portalUserIdsService.getCurrentIds());
        return ApiResponse.responseCreate(action, () -> "/api/subscr/st-plans");
    }

    @GetMapping("/new")
    @ApiOperation("Get Blank StPlan of subscriber")
    @Timed
    public ResponseEntity<?> getBlankStPlan() {
        return ApiResponse.responseOK(() -> subscrStPlanService.newStPlanBlankDTO(portalUserIdsService.getCurrentIds()));
    }

    @PutMapping
    @ApiOperation("Update StPlan of subscriber")
    @Timed
    public ResponseEntity<?> saveStPlan(@RequestBody @Valid SubscrStPlanDTO stPlanDTO) {

        if (stPlanDTO == null) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("SubscrStPlan is empty"));
        }

        if (stPlanDTO.getSubscriberId() != null &&
            !stPlanDTO.getSubscriberId().equals(portalUserIdsService.getCurrentIds().getSubscriberId())) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("SubscrStPlan: subscriberId is invalid"));
        }

        return ApiResponse.responseOK(() -> subscrStPlanService.saveStPlanDTO(stPlanDTO, portalUserIdsService.getCurrentIds()));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete StPlan of subscriber")
    @Timed
    public ResponseEntity<?>  deleteStPlan(@PathVariable("id") @ApiParam("id of SubscrStPlan") Long id) {
        ApiActionVoidProcess action = () -> subscrStPlanService.delete(id, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(action);

    }

}
