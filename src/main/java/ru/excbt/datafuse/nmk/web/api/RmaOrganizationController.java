package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma/organizations")
public class RmaOrganizationController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(RmaOrganizationController.class);

	@Autowired
	private OrganizationService organizationService;

	/**
	 * 
	 * @param xId
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationsGet() {
		List<Organization> resultList = organizationService.selectOrganizations();
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationGet(@PathVariable("organizationId") Long organizationId) {
		Organization result = organizationService.selectOrganization(organizationId);
		return responseOK(result);
	}

	/**
	 * 
	 * @param organizationId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> OrganizationPut(@PathVariable("organizationId") Long organizationId,
			@RequestBody Organization requestEntity) {

		Organization organization = organizationService.selectOrganization(organizationId);
		if (organization == null) {
			return responseBadRequest();
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
	@RequestMapping(value = "", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> organizationPost(@RequestBody Organization requestEntity, HttpServletRequest request) {

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
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/{organizationId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> OrganizationDelete(@PathVariable("organizationId") Long organizationId) {

		Organization organization = organizationService.selectOrganization(organizationId);
		if (organization == null) {
			return responseBadRequest();
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
