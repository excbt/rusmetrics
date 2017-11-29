package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContZPointStPlanDTO;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContZPointStPlanService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/subscr/cont-zpoint-st-plans")
public class SubscrContZPointStPlanResource {

    private final SubscrContZPointStPlanService subscrContZPointStPlanService;

    private final PortalUserIdsService portalUserIdsService;

    @Autowired
    public SubscrContZPointStPlanResource(SubscrContZPointStPlanService subscrContZPointStPlanService, PortalUserIdsService portalUserIdsService) {
        this.subscrContZPointStPlanService = subscrContZPointStPlanService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
     * @param contZPointId
     * @return
     */
    @GetMapping("/cont-zpoints/{contZPointId}")
    @ApiOperation("Get list of SubscrContZPointStPlan")
    @Timed
    public ResponseEntity<?> getContZPointStPlans(@PathVariable("contZPointId") @Valid @ApiParam("id of contZPoint") Long contZPointId) {
        return ApiResponse.responseOK(() -> subscrContZPointStPlanService
            .findContZPointStPlans(contZPointId, portalUserIdsService.getCurrentIds()));
    }


    /**
     *
     * @param planDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation("Creates new SubscrContZPointStPlan")
    @Timed
    public ResponseEntity<?> createContZPointStPlan(@RequestBody SubscrContZPointStPlanDTO planDTO) {
        if (planDTO.getEnabled() == null) planDTO.setEnabled(false);
        ApiActionProcess<SubscrContZPointStPlanDTO> action = () -> subscrContZPointStPlanService.saveDTO(planDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseCreate(action, () -> "/api/subscr/cont-zpoint-st-plans");
    }

    /**
     *
     * @param planDTO
     * @return
     */
    @PutMapping("")
    @ApiOperation("Updates SubscrContZPointStPlan, creates new if id not set")
    @Timed
    public ResponseEntity<?> updateContZPointStPlan(@RequestBody SubscrContZPointStPlanDTO planDTO) {
        if (planDTO.getId() == null) {
            return createContZPointStPlan(planDTO);
        }

        ApiActionProcess<SubscrContZPointStPlanDTO> action = () -> subscrContZPointStPlanService.saveDTO(planDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(action);
    }


    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("Delete SubscrContZPointStPlan")
    @Timed
    public ResponseEntity<?> deleteContZPointStPlan(@PathVariable("id") @ApiParam("id of SubscrContZPointStPlan") Long id) {
        ApiActionVoidProcess action = () -> subscrContZPointStPlanService.delete(id, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseOK(action);
    }

}
