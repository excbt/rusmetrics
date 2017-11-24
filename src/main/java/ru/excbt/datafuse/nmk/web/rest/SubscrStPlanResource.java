package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrStPlanService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
@RequestMapping(value = "/api/subscr/st-plans")
public class SubscrStPlanResource {

    private final SubscrStPlanService subscrStPlanService;

    private final CurrentSubscriberService currentSubscriberServicel;

    @Autowired
    public SubscrStPlanResource(SubscrStPlanService subscrStPlanService, CurrentSubscriberService currentSubscriberServicel) {
        this.subscrStPlanService = subscrStPlanService;
        this.currentSubscriberServicel = currentSubscriberServicel;
    }

    /**
     *
     * @return
     */
    @GetMapping("")
    @ApiOperation("Get all monitor status of cont objects")
    public ResponseEntity<?> getStPlans() {
        return ApiResponse.responseOK(() -> subscrStPlanService.findStPlanDTOs(currentSubscriberServicel.getSubscriberParam()));
    }

}
