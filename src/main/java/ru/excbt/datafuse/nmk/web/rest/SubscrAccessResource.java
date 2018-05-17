package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;

@Controller
@RequestMapping("/api/subscr-access")
public class SubscrAccessResource {

    private SubscriberAccessService subscriberAccessService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrAccessResource(PortalUserIdsService portalUserIdsService) {
        this.portalUserIdsService = portalUserIdsService;
    }

    @GetMapping("/cont-objects")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> get() {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
