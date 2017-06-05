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
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/rma/organizations")
public class RmaOrganizationController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(RmaOrganizationController.class);

	@Autowired
	private OrganizationService organizationService;

    /**
     *
     * @return
     */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationsGet() {
		List<Organization> resultList = organizationService.selectOrganizations(getSubscriberParam());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationGet(@PathVariable("organizationId") Long organizationId) {
		Organization result = organizationService.selectOrganization(organizationId);
		return ApiResponse.responseOK(result);
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

		Organization checkOrganization = organizationService.selectOrganization(organizationId);
		if (checkOrganization == null || organizationId == null || !organizationId.equals(checkOrganization.getId())) {
			return ApiResponse.responseBadRequest();
		}

		if (Boolean.TRUE.equals(checkOrganization.getIsCommon())) {
			return ApiResponse.responseForbidden();
		}

		SubscriberParam subscriberParam = getSubscriberParam();

		requestEntity.setRmaSubscriberId(
				subscriberParam.isRma() ? subscriberParam.getSubscriberId() : subscriberParam.getRmaSubscriberId());

		if (checkOrganization.getRmaSubscriberId() == null
				|| !checkOrganization.getRmaSubscriberId().equals(requestEntity.getRmaSubscriberId())) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<Organization>(requestEntity) {

			@Override
			public Organization processAndReturnResult() {
				return organizationService.saveOrganization(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
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

		return WebApiHelper.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param organizationId
     * @return
     */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> OrganizationDelete(@PathVariable("organizationId") Long organizationId) {

		Organization organization = organizationService.selectOrganization(organizationId);
		if (organization == null) {
			return ApiResponse.responseBadRequest();
		}

		if (Boolean.TRUE.equals(organization.getIsCommon())) {
			return ApiResponse.responseForbidden();
		}

		if (organization.getRmaSubscriberId() == null
				|| !organization.getRmaSubscriberId().equals(getSubscriberParam().getRmaSubscriberId())) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				organizationService.deleteOrganization(organization);
			}

		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
