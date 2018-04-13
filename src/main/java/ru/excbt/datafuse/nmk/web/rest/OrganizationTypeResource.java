package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.service.OrganizationTypeService;

@RestController
@RequestMapping("/api/organization-types")
public class OrganizationTypeResource {

    private final OrganizationTypeService organizationTypeService;

    @Autowired
    public OrganizationTypeResource(OrganizationTypeService organizationTypeService) {
        this.organizationTypeService = organizationTypeService;
    }

    @GetMapping
    @ApiOperation("Get all organization types")
    @Timed
    public ResponseEntity getOrganizationTypes() {
        return ResponseEntity.ok(organizationTypeService.findAll());
    }

}
