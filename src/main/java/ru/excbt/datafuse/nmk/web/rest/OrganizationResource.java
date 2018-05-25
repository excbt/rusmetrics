package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.mapper.OrganizationMapper;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;
import ru.excbt.datafuse.nmk.web.util.PaginationUtil;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrganizationResource {

    private final PortalUserIdsService portalUserIdsService;

    private final OrganizationService organizationService;

    private final OrganizationMapper organizationMapper;

    @Autowired
    public OrganizationResource(PortalUserIdsService portalUserIdsService, OrganizationService organizationService, OrganizationMapper organizationMapper) {
        this.portalUserIdsService = portalUserIdsService;
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    /**
     *
     * @return
     */
    @GetMapping(value = "/organizations", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<OrganizationDTO> organizationsGet(Pageable pageable) {
        Page<OrganizationDTO> page = organizationService.findOrganizationsOfRmaPaged(portalUserIdsService.getCurrentIds(), Optional.empty(), Optional.empty(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organizations");
        return new ResponseEntity(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/organizations/page", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<Page<OrganizationDTO>> organizationsGetPage(@RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                                      @RequestParam(name = "subscriberMode", required = false) Optional<Boolean> subscriberMode,
                                                                      Pageable pageable) {
        Page<OrganizationDTO> page = organizationService.findOrganizationsOfRmaPaged(portalUserIdsService.getCurrentIds(), searchString, subscriberMode, pageable);
        return new ResponseEntity(page, HttpStatus.OK);
    }

    /**
     *
     * @param organizationId
     * @return
     */
    @GetMapping(value = "/organizations/{organizationId}", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<OrganizationDTO> organizationGet(@PathVariable("organizationId") Long organizationId) {
        OrganizationDTO organizationDTO = organizationService.findOneOrganization(organizationId)
            .map(organizationMapper::toDTO)
            .orElseThrow(() -> new EntityNotFoundException(Organization.class, organizationId));
        return new ResponseEntity<>(organizationDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/organizations")
    @ApiOperation("")
    @Timed
    public ResponseEntity<?> putOrganization(@RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO savedDTO = organizationService.saveOrganization(organizationDTO, portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok(savedDTO);
    }

    /**
     *
     * @param organizationId
     * @return
     */
    @DeleteMapping(value = "/organizations/{organizationId}", produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity organizationDelete(@PathVariable("organizationId") Long organizationId) {
        organizationService.deleteOrganization(organizationId, portalUserIdsService.getCurrentIds());
        return ResponseEntity.ok().build();
    }

}