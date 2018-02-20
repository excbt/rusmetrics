package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationResource {

    private final PortalUserIdsService portalUserIdsService;

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationResource(PortalUserIdsService portalUserIdsService, OrganizationService organizationService) {
        this.portalUserIdsService = portalUserIdsService;
        this.organizationService = organizationService;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity organizationsGet() {
        List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(portalUserIdsService.getCurrentIds());
        return new ResponseEntity(organizations, HttpStatus.OK);
    }


}
