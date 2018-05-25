package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.DeviceModelService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.dto.DeviceModelDTO;

import java.util.List;

@RestController()
@RequestMapping("/api")
public class DeviceModelResource {

    private final DeviceModelService deviceModelService;

    private final PortalUserIdsService portalUserIdsService;

    public DeviceModelResource(DeviceModelService deviceModelService, PortalUserIdsService portalUserIdsService) {
        this.deviceModelService = deviceModelService;
        this.portalUserIdsService = portalUserIdsService;
    }

    @GetMapping("/device-models")
    @Timed
    public ResponseEntity<?> getDeviceModels() {
        List<DeviceModelDTO> resultPage = deviceModelService.findDeviceModels();
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/device-models/page")
    @Timed
    public ResponseEntity<?> getDeviceModelsPage(@RequestParam(name = "searchString", required = false) String searchString,
                                             Pageable pageable) {
        Page<DeviceModelDTO> resultPage = deviceModelService.findDeviceModels(searchString, pageable);
        return ResponseEntity.ok(resultPage);
    }
}
