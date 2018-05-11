package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
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
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.types.ContObjectCurrentSettingTypeKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.utils.ObjectAccessUtil;
import ru.excbt.datafuse.nmk.service.vm.ContObjectShortInfoVM;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
public class SubscrContObjectResource {

	// private final static int TEST_SUBSCRIBER_ID = 728;

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectResource.class);

	protected final ContObjectService contObjectService;

	protected final ContGroupService contGroupService;

    protected final OrganizationService organizationService;

	protected final ContObjectFiasService contObjectFiasService;

	protected final MeterPeriodSettingService meterPeriodSettingService;

	protected final ObjectAccessService objectAccessService;

	protected final PortalUserIdsService portalUserIdsService;

	@Autowired
    public SubscrContObjectResource(ContObjectService contObjectService,
                                    ContGroupService contGroupService,
                                    OrganizationService organizationService,
                                    ContObjectFiasService contObjectFiasService,
                                    MeterPeriodSettingService meterPeriodSettingService,
                                    ObjectAccessService objectAccessService,
                                    PortalUserIdsService portalUserIdsService) {
        this.contObjectService = contObjectService;
        this.contGroupService = contGroupService;
        this.organizationService = organizationService;
        this.contObjectFiasService = contObjectFiasService;
        this.meterPeriodSettingService = meterPeriodSettingService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    protected abstract class ContObjectDTOResponse extends ApiActionEntityAdapter<List<? extends ContObjectDTO>> {

    }


    protected abstract class ContObjectMonitorDTOResponse extends ApiActionEntityAdapter<List<ContObjectMonitorDTO>> {

    }


    /**
     *
     * @param contGroupId
     * @return
     */
    protected List<ContObject> findContObjectsByAccess(Long contGroupId, boolean isRma, boolean isHaveSubscrFiltered, List<Long> meterPeriodSettingIds) {
        List<ContObject> contObjectList;

        contObjectList = objectAccessService.findContObjects(
            portalUserIdsService.getCurrentIds().getSubscriberId(),
            contGroupId);
        contObjectList = meterPeriodSettingService.filterMeterPeriodSettingIds(contObjectList, meterPeriodSettingIds);

        if (isRma) {
            objectAccessService.setupRmaHaveSubscr(portalUserIdsService.getCurrentIds(), contObjectList);

            if (isHaveSubscrFiltered) {

                contObjectList = ObjectFilters.filterToList(contObjectList, i -> Boolean.TRUE.equals(i.get_haveSubscr()));

            }
        }


        return contObjectList;
    }

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId,
                                            @RequestParam(value = "meterPeriodSettingIds", required = false) List<Long> meterPeriodSettingIds) {

//        ApiAction action = new ApiActionEntityAdapter<List<ContObjectMonitorDTO>> () {
//            @Override
//            public List<ContObjectMonitorDTO> processAndReturnResult() {
//                boolean isRma = currentSubscriberService.isRma();
//                List<ContObject> resultList = findContObjectsByAccess(contGroupId, isRma, true, meterPeriodSettingIds);
//                List<ContObjectMonitorDTO> result = contObjectService.wrapContObjectsMonitorDTO(getSubscriberParam(), resultList);
//                return result;
//            }
//        };

		return ApiActionTool.processResponceApiActionOk(() -> {
            boolean isRma = portalUserIdsService.getCurrentIds().isRma();
            List<ContObject> resultList = findContObjectsByAccess(contGroupId, isRma, true, meterPeriodSettingIds);
            List<ContObjectMonitorDTO> result = contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(), resultList);
            return result;
        });

	}

	protected boolean canAccessContObject(Long contObjectId) {
        ObjectAccessUtil objectAccessUtil = objectAccessService.objectAccessUtil();
        return objectAccessUtil.checkContObjectId(portalUserIdsService.getCurrentIds()).test(contObjectId);
    }

	protected boolean canAccessContObject(List<Long> contObjectIds) {
        ObjectAccessUtil objectAccessUtil = objectAccessService.objectAccessUtil();
        java.util.function.Predicate<Long> checker = objectAccessUtil.checkContObjectId(portalUserIdsService.getCurrentIds());
        return contObjectIds.stream().filter(i -> !checker.test(i)).findAny().map(x -> false).orElse(true);
    }

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getContObject(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		return ApiResponse.responseOK(() -> contObjectService.findContObjectMonitorDTO(portalUserIdsService.getCurrentIds(), contObjectId));
	}


	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/fias", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getContObjectFias(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ContObjectFias result = contObjectFiasService.findContObjectFias(contObjectId);

		if (result == null) {
			return ApiResponse.responseNoContent();
		}

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contObjectDTO
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> updateContObject(@PathVariable("contObjectId") Long contObjectId,
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			final @RequestBody ContObjectDTO contObjectDTO) {

        Objects.requireNonNull(contObjectDTO);
        Objects.requireNonNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (contObjectDTO.getId() == null) {
			return ApiResponse.responseBadRequest();
		}

		ApiAction action = new ApiActionEntityAdapter<ContObjectMonitorDTO>() {

			@Override
			public ContObjectMonitorDTO processAndReturnResult() {

				//ContObject result = contObjectService.updateContObject(contObject, cmOrganizationId);
				ContObject result = contObjectService.automationUpdate(contObjectDTO, cmOrganizationId);

				objectAccessService.setupRmaHaveSubscr(portalUserIdsService.getCurrentIds(), Arrays.asList(result));

				return contObjectService.wrapContObjectMonitorDTO(portalUserIdsService.getCurrentIds(), result,false);
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
    @Timed
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
    @Timed
    public ResponseEntity<?> updateContObjectSettingModeType(
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestParam(value = "currentSettingMode") final String currentSettingMode,
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
						currentSettingMode, portalUserIdsService.getCurrentIds().getSubscriberId());

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
    @Timed
    public ResponseEntity<?> getCmOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<OrganizationDTO> organizations = organizationService.selectCmOrganizations(portalUserIdsService.getCurrentIds());
		organizationService.checkAndEnhanceOrganizations(organizations, organizationId);
		return ApiResponse.responseOK(organizations);
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/organizations", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<OrganizationDTO> organizations = organizationService.selectOrganizations(portalUserIdsService.getCurrentIds());
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
    @Timed
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
    @Timed
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
			return contObjectService.findContObjectChecked(settings.getContObjectId());
		};

		return ApiResponse.responseUpdate(process);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/meterPeriodSettings", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getContObjectMeterPeriodSetting() {
		List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());
		List<ContObjectMeterPeriodSettingsDTO> result = contObjectService.findMeterPeriodSettings(ids);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/meterPeriodSettings", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
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


	@GetMapping("/cont-objects/short-info")
	public ResponseEntity<?> getContObjectShortInfo() {
	    List<ContObjectShortInfoVM> shortInfoVMList = contObjectService.findShortInfo(portalUserIdsService.getCurrentIds());
	    return ApiResponse.responseOK(shortInfoVMList);
    }

}
