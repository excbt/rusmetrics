package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContZPointStPlanDTO;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContZPointStPlanService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
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
    public ResponseEntity<?> getContZPointStPlans(@PathVariable("contZPointId") @Valid Long contZPointId) {
        return ApiResponse.responseOK(() -> subscrContZPointStPlanService
            .findContZPointStPlans(contZPointId, portalUserIdsService.getCurrentIds()));
    }


    /**
     *
     * @param planDTO
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> createContZPointStPlan(@RequestBody SubscrContZPointStPlanDTO planDTO) {
        planDTO.setEnabled(false);
        ApiActionProcess<SubscrContZPointStPlanDTO> action = () -> subscrContZPointStPlanService.saveDTO(planDTO, portalUserIdsService.getCurrentIds());
        return ApiResponse.responseCreate(action, () -> "/api/subscr/cont-zpoint-st-plans");
    }

}
