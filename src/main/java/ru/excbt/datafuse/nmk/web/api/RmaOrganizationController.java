package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/api/rma/organizations")
public class RmaOrganizationController extends AbstractSubscrApiResource {

	private static final Logger log = LoggerFactory.getLogger(RmaOrganizationController.class);

	@Autowired
	private OrganizationService organizationService;

    /**
     *
     * @return
     */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationsGet() {
        List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(getSubscriberParam().asPortalUserIds());
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

		SubscriberParam subscriberParam = getSubscriberParam();

		requestEntity.setRmaSubscriberId(
				subscriberParam.isRma() ? subscriberParam.getSubscriberId() : subscriberParam.getRmaSubscriberId());

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

		SubscriberParam subscriberParam = getSubscriberParam();

		requestEntity.setRmaSubscriberId(
				subscriberParam.isRma() ? subscriberParam.getSubscriberId() : subscriberParam.getRmaSubscriberId());

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
				|| !organizationOptional.get().getRmaSubscriberId().equals(getSubscriberParam().getRmaSubscriberId())) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = (ApiActionAdapter) () -> organizationService.deleteOrganization(organizationOptional.get());

		return ApiActionTool.processResponceApiActionDelete(action);
	}

}
