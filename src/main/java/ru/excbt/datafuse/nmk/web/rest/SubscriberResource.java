package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.web.ApiConst;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberResource {

    private final PortalUserIdsService portalUserIdsService;
    private final SubscriberService subscriberService;

    public SubscriberResource(PortalUserIdsService portalUserIdsService, SubscriberService subscriberService) {
        this.portalUserIdsService = portalUserIdsService;
        this.subscriberService = subscriberService;
    }

    @GetMapping
    public ResponseEntity<?> getSubscribers() {
        List<SubscriberDTO> result = subscriberService.selectSubscribers(portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok(result);
    }


    @GetMapping(value = "/page", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<Page<SubscriberDTO>> subscribersGetPage(@RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                                    @RequestParam(name = "subscriberMode", required = false) Optional<String> subscriberMode,
                                                                    Pageable pageable) {

        Page<SubscriberDTO> page = subscriberService
            .selectSubscribers(
                portalUserIdsService.getCurrentIds(),
                KeyEnumTool.searchName(SubscriberService.SubscriberMode.class,
                        subscriberMode.map(String::toUpperCase).orElse(null)
                ).orElse(SubscriberService.SubscriberMode.NORMAL),
                searchString,
                pageable);
        return new ResponseEntity(page, HttpStatus.OK);
    }

}
