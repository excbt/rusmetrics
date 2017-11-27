package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrStPlanService;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/subscr/st-plans")
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
    public ResponseEntity<?> getStPlans() {
        return ApiResponse.responseOK(() -> subscrStPlanService.findStPlanDTOs(portalUserIdsService.getCurrentIds()));
    }


    @PostMapping("/new")
    @ApiOperation("Creates new StPlans of subscriber ans save it immediately")
    public ResponseEntity<?> createStPlanAndSave(HttpServletRequest request) {
        return ApiResponse.responseCreate(() ->
            subscrStPlanService.newPlanAndSave(portalUserIdsService.getCurrentIds()),
            () -> "/api/subscr/st-plans");
    }

    @GetMapping("/new")
    @ApiOperation("Get all StPlans of subscriber")
    public ResponseEntity<?> getBlankStPlan() {
        return ApiResponse.responseOK(() -> subscrStPlanService.newPlanBlank(portalUserIdsService.getCurrentIds()));
    }


    }
