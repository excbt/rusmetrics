package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/rma/organizations")
public class RmaOrganizationController {

	private static final Logger log = LoggerFactory.getLogger(RmaOrganizationController.class);

	private final OrganizationService organizationService;

	private final PortalUserIdsService portalUserIdsService;

    public RmaOrganizationController(OrganizationService organizationService, PortalUserIdsService portalUserIdsService) {
        this.organizationService = organizationService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
     *
     * @return
     */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationsGet() {
        List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(organizations);
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationGet(@PathVariable("organizationId") Long organizationId) {
		return ApiResponse.responseContent(organizationService.findOneOrganization(organizationId));
	}

	/**
	 *
	 * @param organizationId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> OrganizationPut(@PathVariable("organizationId") Long organizationId,
			@RequestBody Organization requestEntity) {

        Optional<Organization> organizationOptional = organizationService.findOneOrganization(organizationId);

		//Organization checkOrganization = organizationService.selectOrganization(organizationId);
		if (!organizationOptional.isPresent()) {
			return ApiResponse.responseBadRequest();
		}

		Organization organization = organizationOptional.get();

		if (Boolean.TRUE.equals(organization.getIsCommon())) {
			return ApiResponse.responseForbidden();
		}

        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();

		requestEntity.setRmaSubscriberId(
            portalUserIds.isRma() ? portalUserIds.getSubscriberId() : portalUserIds.getRmaId());

		if (organization.getRmaSubscriberId() == null
				|| !organization.getRmaSubscriberId().equals(requestEntity.getRmaSubscriberId())) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<Organization>(requestEntity) {

			@Override
			public Organization processAndReturnResult() {
				return organizationService.saveOrganization(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param requestEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationPost(@RequestBody Organization requestEntity, HttpServletRequest request) {

		if (!requestEntity.isNew()) {
			return ApiResponse.responseBadRequest();
		}

		if (Boolean.TRUE.equals(requestEntity.getIsCommon())) {
			return ApiResponse.responseForbidden();
		}

        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();

		requestEntity.setRmaSubscriberId(
            portalUserIds.isRma() ? portalUserIds.getSubscriberId() : portalUserIds.getRmaId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<Organization, Long>(requestEntity, request) {

			@Override
			public Organization processAndReturnResult() {
				return organizationService.saveOrganization(entity);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param organizationId
     * @return
     */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> OrganizationDelete(@PathVariable("organizationId") Long organizationId) {

		Optional<Organization> organizationOptional = organizationService.findOneOrganization(organizationId);
            //selectOrganization(organizationId);
		if (!organizationOptional.isPresent()) {
			return ApiResponse.responseBadRequest();
		}

		if (Boolean.TRUE.equals(organizationOptional.get().getIsCommon())) {
			return ApiResponse.responseForbidden();
		}

		if (organizationOptional.get().getRmaSubscriberId() == null
				|| !organizationOptional.get().getRmaSubscriberId().equals(portalUserIdsService.getCurrentIds().getRmaId())) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = (ApiActionAdapter) () -> organizationService.deleteOrganization(organizationOptional.get());

		return ApiActionTool.processResponceApiActionDelete(action);
	}

}
