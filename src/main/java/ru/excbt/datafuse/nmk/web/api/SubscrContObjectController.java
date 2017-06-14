package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectWrapper;
import ru.excbt.datafuse.nmk.data.model.types.ContObjectCurrentSettingTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
public class SubscrContObjectController extends AbstractSubscrApiResource {

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
	protected List<ContObject> selectRmaContObjects(Long contGroupId, boolean isHaveSubscrFiltered, List<Long> meterPeriodSettingIds) {
		List<ContObject> contObjectList = null;

		contObjectList = subscrContObjectService.selectSubscriberContObjects(getSubscriberParam(), contGroupId, meterPeriodSettingIds);

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
	protected List<ContObject> selectSubscrContObjects(Long contGroupId, List<Long> meterPeriodSettingIds) {
		return subscrContObjectService.selectSubscriberContObjects(getSubscriberParam(), contGroupId, meterPeriodSettingIds);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId,
                                            @RequestParam(value = "meterPeriodSettingIds", required = false) List<Long> meterPeriodSettingIds) {

		ApiAction action = new ApiActionEntityAdapter<Object>() {
			@Override
			public Object processAndReturnResult() {
                List<ContObject> resultList = currentSubscriberService.isRma() ? selectRmaContObjects(contGroupId, true, meterPeriodSettingIds)
                    : selectSubscrContObjects(contGroupId, meterPeriodSettingIds);

				return contObjectService.wrapContObjectsMonitorVO(resultList);
			}
		};

		return ApiActionTool.processResponceApiActionOk(action);

		//return responseOK(contObjectService.wrapContObjectsStats(resultList));
		//return responseOK(contObjectService.wrapContObjectsMonitorVO(resultList));
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObject(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ContObjectMonitorDTO result = contObjectService.findContObjectMonitorDTO(contObjectId);

		return ApiResponse.responseOK(result);
	}


	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/fias", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectFias(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ContObjectFias result = contObjectService.findContObjectFias(contObjectId);

		if (result == null) {
			return ApiResponse.responseNoContent();
		}

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObject(@PathVariable("contObjectId") Long contObjectId,
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			final @RequestBody ContObject contObject) {

		checkNotNull(contObjectId);
		checkNotNull(contObject);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (contObject.isNew()) {
			return ApiResponse.responseBadRequest();
		}

		ApiAction action = new ApiActionEntityAdapter<ContObjectWrapper>() {

			@Override
			public ContObjectWrapper processAndReturnResult() {

				ContObject result = contObjectService.updateContObject(contObject, cmOrganizationId);

				subscrContObjectService.rmaInitHaveSubscr(getSubscriberParam(), Arrays.asList(result));

				return contObjectService.wrapContObjectsStats(result);
			}

		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectSettingModeType() {

		List<ContObjectSettingModeType> resultList = contObjectService.selectContObjectSettingModeType();
		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObjectSettingModeType(
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestParam(value = "currentSettingMode", required = true) final String currentSettingMode,
			final @RequestBody(required = false) List<Long> ids) {

		checkNotNull(currentSettingMode);
		checkArgument(ContObjectCurrentSettingTypeKey.isSupported(currentSettingMode));

		final List<Long> contObjectIdList = (ids != null ? ids
				: (contObjectIds != null ? Arrays.asList(contObjectIds) : null));

		if (contObjectIdList == null || contObjectIdList.isEmpty()) {
			return ApiResponse.responseBadRequest();
		}

		Optional<Long> checkAccess = contObjectIdList.stream().filter((i) -> !canAccessContObject(i)).findAny();

		if (checkAccess.isPresent()) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = new AbstractEntityApiAction<List<Long>>() {

			@Override
			public void process() {

				List<Long> result = contObjectService.updateContObjectCurrentSettingModeType(
						contObjectIdList.toArray(new Long[] {}),
						currentSettingMode, currentSubscriberService.getSubscriberId());

				setResultEntity(result);
			}

		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/cmOrganizations", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCmOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<Organization> organizations = organizationService.selectCmOrganizations(getSubscriberParam());
		organizationService.checkAndEnhanceOrganizations(organizations, organizationId);
		return ApiResponse.responseOK(organizations);
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/organizations", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<Organization> organizations = organizationService.selectOrganizations(getSubscriberParam());
		organizationService.checkAndEnhanceOrganizations(organizations, organizationId);
		return ApiResponse.responseOK(organizations);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/meterPeriodSettings", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectMeterPeriodSetting(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionProcess<ContObjectMeterPeriodSettingsDTO> process = () -> contObjectService.getContObjectMeterPeriodSettings(contObjectId);

		return ApiResponse.responseOK(process);
	}

	/**
	 *
	 * @param contObjectId
	 * @param settings
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/meterPeriodSettings", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObjectMeterPeriodSetting(@PathVariable("contObjectId") Long contObjectId,
			final @RequestBody ContObjectMeterPeriodSettingsDTO settings) {

		if (settings.isSingle() == false) {
			return ApiResponse.responseBadRequest();
		}

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (!contObjectId.equals(settings.getContObjectId())) {
			return ApiResponse.responseBadRequest();
		}

		ApiActionProcess<ContObject> process = () -> {
			contObjectService.updateMeterPeriodSettings(settings);
			return contObjectService.findContObject(settings.getContObjectId());
		};

		return ApiResponse.responseUpdate(process);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/meterPeriodSettings", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectMeterPeriodSetting() {
		List<Long> ids = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
		List<ContObjectMeterPeriodSettingsDTO> result = contObjectService.findMeterPeriodSettings(ids);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/meterPeriodSettings", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObjectMeterPeriodSetting(
			final @RequestBody ContObjectMeterPeriodSettingsDTO settings) {
		if (settings.isMulti() == false) {
			return ApiResponse.responseBadRequest();
		}

		if (!canAccessContObject(settings.getContObjectIds())) {
			return ApiResponse.responseForbidden();
		}
		ApiActionProcess<List<ContObjectMeterPeriodSettingsDTO>> process = () -> {
			contObjectService.updateMeterPeriodSettings(settings);
			return contObjectService.findMeterPeriodSettings(settings.getContObjectIds());
		};
		return ApiResponse.responseOK(process);
	}

}
