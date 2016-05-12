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

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.types.ContObjectCurrentSettingTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с объектом учета для абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.02.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContObjectController extends SubscrApiController {

	// private final static int TEST_SUBSCRIBER_ID = 728;

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectController.class);

	@Autowired
	protected ContObjectService contObjectService;

	@Autowired
	protected ContGroupService contGroupService;

	@Autowired
	private OrganizationService organizationService;

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	protected List<ContObject> selectRmaContObjects(Long contGroupId, boolean isHaveSubscrFiltered) {
		List<ContObject> contObjectList = null;

		contObjectList = subscrContObjectService.selectSubscriberContObjects(getSubscriberParam(), contGroupId);

		subscrContObjectService.rmaInitHaveSubscr(getSubscriberParam(), contObjectList);

		if (isHaveSubscrFiltered) {

			return ObjectFilters.filterToList(contObjectList, i -> Boolean.TRUE.equals(i.get_haveSubscr()));

		}

		return contObjectList;
	}

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	protected List<ContObject> selectSubscrContObjects(Long contGroupId) {
		return subscrContObjectService.selectSubscriberContObjects(getSubscriberParam(), contGroupId);

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId) {

		List<ContObject> resultList = currentSubscriberService.isRma() ? selectRmaContObjects(contGroupId, true)
				: selectSubscrContObjects(contGroupId);

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

		ContObject result = contObjectService.findContObject(contObjectId);
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

		ApiAction action = new ApiActionEntityAdapter<ContObject>(contObject) {

			@Override
			public ContObject processAndReturnResult() {
				return contObjectService.updateContObject(entity, cmOrganizationId);
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
		return responseOK(organizations);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/organizations", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOrganizations() {
		List<Organization> organizations = organizationService.selectOrganizations();
		return responseOK(organizations);
	}

}
