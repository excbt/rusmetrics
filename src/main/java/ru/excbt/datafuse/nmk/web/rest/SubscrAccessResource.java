package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.ContObjectAccessService;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/subscr-access")
public class SubscrAccessResource {

    private final ObjectAccessService objectAccessService;
    private final ContObjectMapper contObjectMapper;
    private final ContObjectAccessRepository contObjectAccessRepository;
    private final ContObjectAccessService contObjectAccessService;
    private final ContObjectAccessMapper contObjectAccessMapper;
    private final PortalUserIdsService portalUserIdsService;

    public SubscrAccessResource(ObjectAccessService objectAccessService, ContObjectMapper contObjectMapper, ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessService contObjectAccessService, ContObjectAccessMapper contObjectAccessMapper, PortalUserIdsService portalUserIdsService) {
        this.objectAccessService = objectAccessService;
        this.contObjectMapper = contObjectMapper;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessService = contObjectAccessService;
        this.contObjectAccessMapper = contObjectAccessMapper;
        this.portalUserIdsService = portalUserIdsService;
    }

    @GetMapping("/cont-objects")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getContObjects() {
        List<ContObjectAccessDTO> contObjectAccessDTOS = contObjectAccessRepository.findBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId())
            .stream().map(contObjectAccessMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(contObjectAccessDTOS);
    }

    @GetMapping("/cont-objects/page")
    @Timed
    @ApiOperation("")
    public ResponseEntity<?> getContObjectsPaged(@RequestParam(name = "subscriberId", required = false) Optional<Long> subscriberId,
                                                 @RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                 Pageable pageable) {
        Page<ContObjectAccessDTO> contObjectAccessDTOS = contObjectAccessService.getContObjectAccess(portalUserIdsService.getCurrentIds(), searchString, pageable);
        return ResponseEntity.ok(contObjectAccessDTOS);
    }

}
