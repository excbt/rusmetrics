package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.types.ContObjectCurrentSettingTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContObjectController extends SubscrApiController {

	// private final static int TEST_SUBSCRIBER_ID = 728;

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectController.class);

	@Autowired
	protected ContObjectService contObjectService;

	@Autowired
	private OrganizationService organizationService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getRmaContObjects() {
		List<ContObject> resultList = subscrContObjectService
				.selectSubscriberContObjects(currentSubscriberService.getSubscriberId());

		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObject(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject result = contObjectService.findOne(contObjectId);
		return responseOK(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/fias", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectFias(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObjectFias result = contObjectService.findContObjectFias(contObjectId);

		if (result == null) {
			return responseNoContent();
		}

		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObject(@PathVariable("contObjectId") Long contObjectId,
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			@RequestBody ContObject contObject) {

		checkNotNull(contObjectId);
		checkNotNull(contObject);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		if (contObject.isNew()) {
			return responseBadRequest();
		}

		ApiAction action = new AbstractEntityApiAction<ContObject>(contObject) {
			@Override
			public void process() {
				setResultEntity(contObjectService.updateOne(entity, cmOrganizationId));

			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectSettingModeType() {

		List<ContObjectSettingModeType> resultList = contObjectService.selectContObjectSettingModeType();
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObjectSettingModeType(
			@RequestParam(value = "contObjectIds", required = true) final Long[] contObjectIds,
			@RequestParam(value = "currentSettingMode", required = true) final String currentSettingMode) {

		checkArgument(contObjectIds.length > 0);
		checkNotNull(currentSettingMode);
		checkArgument(ContObjectCurrentSettingTypeKey.isSupported(currentSettingMode));

		List<Long> contObjectIdList = Arrays.asList(contObjectIds);

		Optional<Long> checkAccess = contObjectIdList.stream().filter((i) -> !canAccessContObject(i)).findAny();

		if (checkAccess.isPresent()) {
			return responseForbidden();
		}

		ApiAction action = new AbstractEntityApiAction<List<Long>>() {

			@Override
			public void process() {

				List<Long> result = contObjectService.updateContObjectCurrentSettingModeType(contObjectIds,
						currentSettingMode, currentSubscriberService.getSubscriberId());

				setResultEntity(result);
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/cmOrganizations", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCmOrganizations() {
		List<Organization> organizations = organizationService.selectCmOrganizations();
		List<Organization> resultList = ObjectFilters.deletedFilter(organizations);
		return responseOK(resultList);
	}

}
