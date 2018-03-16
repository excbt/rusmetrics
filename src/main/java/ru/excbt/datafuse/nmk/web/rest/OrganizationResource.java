package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.mapper.OrganizationMapper;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
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
    @RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<OrganizationDTO> organizationsGet(Sort sort) {
        List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(portalUserIdsService.getCurrentIds(), sort);
        return new ResponseEntity(organizations, HttpStatus.OK);
    }

    /**
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<OrganizationDTO> organizationGet(@PathVariable("organizationId") Long organizationId) {
        OrganizationDTO organizationDTO = organizationService.findOneOrganization(organizationId)
            .map(organizationMapper::toDTO)
            .orElseThrow(() -> new EntityNotFoundException(Organization.class, organizationId));
        return new ResponseEntity<>(organizationDTO, HttpStatus.OK);
    }


}
