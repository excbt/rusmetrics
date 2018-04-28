package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
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

    @GetMapping(value = "/normal", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<Page<SubscriberVM>> normalGetPage(@RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                            Pageable pageable) {

        Page<SubscriberVM> page = subscriberService
            .selectSubscribers2(
                portalUserIdsService.getCurrentIds(),
                SubscriberService.SubscriberMode.NORMAL,
                searchString,
                subscriberMapper::toVM,
                pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/rma", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<Page<SubscriberVM>> rmaGetPage(@RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                         Pageable pageable) {
        Page<SubscriberVM> page = subscriberService
            .selectSubscribers2(
                portalUserIdsService.getCurrentIds(),
                SubscriberService.SubscriberMode.RMA,
                searchString,
                subscriberMapper::toVM,
                pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/rma/{subscriberId}")
    public ResponseEntity<SubscriberVM> getSubscriberSubscriber(@PathVariable("subscriberId") Long subscriberId) {
        return new ResponseEntity<>(
                        subscriberService.findOneSubscriber(subscriberId)
                            .filter(s -> SubscrTypeKey.RMA.keyName().equals(s.getSubscrType()))
                            .map(subscriberMapper::toVM)
                            .orElse(null),
            HttpStatus.OK);
    }

    @GetMapping(value = "/normal/{subscriberId}")
    public ResponseEntity<SubscriberVM> getSubscriberNormal(@PathVariable("subscriberId") Long subscriberId) {
        return new ResponseEntity<>(
                        subscriberService.findOneSubscriber(subscriberId)
                            .filter(s -> SubscrTypeKey.NORMAL.keyName().equals(s.getSubscrType()))
                            .map(subscriberMapper::toVM)
                            .orElse(null),
            HttpStatus.OK);
    }


    @PutMapping("/normal")
    public ResponseEntity<SubscriberVM> putSubscriberNormal() {
        return new ResponseEntity<>(new SubscriberVM(), HttpStatus.OK);
    }


    @PutMapping("/rma")
    public ResponseEntity<SubscriberVM> putSubscriberRma() {
        return new ResponseEntity<>(new SubscriberVM(), HttpStatus.OK);
    }

}
