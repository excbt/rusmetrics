package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContZPointStPlanService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

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
    public ResponseEntity<?> getContZPointStPlans(@PathVariable("contZPointId") Long contZPointId) {
        return ApiResponse.responseOK(() -> subscrContZPointStPlanService
            .findContZPointStPlans(contZPointId, portalUserIdsService.getCurrentIds()));
    }
}
