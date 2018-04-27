package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;
import ru.excbt.datafuse.nmk.web.ApiConst;

import java.util.Optional;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberResource {

    private final PortalUserIdsService portalUserIdsService;
    private final SubscriberService subscriberService;
    private final SubscriberMapper subscriberMapper;

    public SubscriberResource(PortalUserIdsService portalUserIdsService, SubscriberService subscriberService, SubscriberMapper subscriberMapper) {
        this.portalUserIdsService = portalUserIdsService;
        this.subscriberService = subscriberService;
        this.subscriberMapper = subscriberMapper;
    }


    @GetMapping(value = "/{subscriberMode}/page", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<Page<SubscriberVM>> partnersGetPage(@PathVariable(name="subscriberMode") String subscriberMode,
                                                              @RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                              Pageable pageable) {

        Page<SubscriberVM> page = subscriberService
            .selectSubscribers2(
                portalUserIdsService.getCurrentIds(),
                KeyEnumTool.searchName(SubscriberService.SubscriberMode.class,
                    subscriberMode.toUpperCase()
                ).orElse(SubscriberService.SubscriberMode.NORMAL),
                searchString,
                subscriberMapper::toVM,
                pageable);
        return new ResponseEntity(page, HttpStatus.OK);
    }

}
