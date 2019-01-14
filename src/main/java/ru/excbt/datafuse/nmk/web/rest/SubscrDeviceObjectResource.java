package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.*;
import ru.excbt.datafuse.nmk.data.repository.VzletSystemRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с приборами для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
@RestController
@RequestMapping(value = "/api/subscr")
public class SubscrDeviceObjectResource //extends AbstractSubscrApiResource
{

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectResource.class);

	protected final DeviceObjectService deviceObjectService;

	protected final DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	protected final DeviceObjectLoadingLogService deviceObjectLoadingLogService;

	protected final VzletSystemRepository vzletSystemRepository;

	protected final DeviceModelService deviceModelService;

	protected final ContObjectService contObjectService;

	protected final SubscrDataSourceService subscrDataSourceService;

	protected final DeviceMetadataService deviceMetadataService;

	protected final SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService;

	protected final HeatRadiatorTypeService heatRadiatorTypeService;

    protected final DeviceDataTypeService deviceDataTypeService;

    protected final DeviceObjectMapper deviceObjectMapper;

    protected final ObjectAccessService objectAccessService;

    protected final PortalUserIdsService portalUserIdsService;

    protected final ContZPointDeviceHistoryService contZPointDeviceHistoryService;



    @Autowired
    public SubscrDeviceObjectResource(DeviceObjectService deviceObjectService, DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService, DeviceObjectLoadingLogService deviceObjectLoadingLogService, VzletSystemRepository vzletSystemRepository, DeviceModelService deviceModelService, ContObjectService contObjectService, SubscrDataSourceService subscrDataSourceService, DeviceMetadataService deviceMetadataService, SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService, HeatRadiatorTypeService heatRadiatorTypeService, DeviceDataTypeService deviceDataTypeService, DeviceObjectMapper deviceObjectMapper, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService, ContZPointDeviceHistoryService contZPointDeviceHistoryService) {
        this.deviceObjectService = deviceObjectService;
        this.deviceObjectLoadingSettingsService = deviceObjectLoadingSettingsService;
        this.deviceObjectLoadingLogService = deviceObjectLoadingLogService;
        this.vzletSystemRepository = vzletSystemRepository;
        this.deviceModelService = deviceModelService;
        this.contObjectService = contObjectService;
        this.subscrDataSourceService = subscrDataSourceService;
        this.deviceMetadataService = deviceMetadataService;
        this.subscrDataSourceLoadingSettingsService = subscrDataSourceLoadingSettingsService;
        this.heatRadiatorTypeService = heatRadiatorTypeService;
        this.deviceDataTypeService = deviceDataTypeService;
        this.deviceObjectMapper = deviceObjectMapper;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
        this.contZPointDeviceHistoryService = contZPointDeviceHistoryService;
    }

    /**
     *
     * @param contObjectId
     * @return
     */
    protected boolean canAccessContObject(Long contObjectId) {
        return objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds());
    }


    /**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsByContObjectId(contObjectId);

        List<DeviceObjectFullVM> deviceObjectFullVMList = deviceObjects.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> deviceObjectMapper.toFullVM(i)).collect(Collectors.toList());
        //List<DeviceObjectShortInfoDTO> deviceObjectDTOS = deviceObjects.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> deviceObjectMapper.toShortInfoDTO(i)).collect(Collectors.toList());

		return ApiResponse.responseOK(deviceObjectFullVMList);
	}

    /**
     * TODO
     * @param contObjectId
     * @param deviceObjectId
     * @return
     */
    @RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getDeviceObject(@PathVariable("contObjectId") Long contObjectId,
                                                         @PathVariable("deviceObjectId") Long deviceObjectId) {


        if (!canAccessContObject(contObjectId)) {
            return ApiResponse.responseForbidden();
        }

        ApiActionProcess<DeviceObjectFullVM> actionProcess = () -> {

            DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);

            DeviceObjectFullVM fullVM = deviceObjectMapper.toFullVM(deviceObject);
            if (SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
                fullVM.shareDeviceLoginInfo(deviceObject);
            }

            return fullVM;
        };

        Function<DeviceObjectFullVM, ResponseEntity<?>> extraCheck = (x) -> {
            if (x == null) {
                return ApiResponse.responseNoContent();
            }
            if (x.getContObjectId() == null || !x.getContObjectId().equals(contObjectId)) {
                return ApiResponse.responseBadRequest();
            }
            return null;
        };

        return ApiResponse.responseOK(actionProcess, extraCheck);

    }

    @RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> updateDeviceObject(@PathVariable("contObjectId") Long contObjectId,
                                              @PathVariable("deviceObjectId") Long deviceObjectId, @RequestBody DeviceObjectDTO deviceObjectDTO) {

        Objects.requireNonNull(deviceObjectDTO);
        Objects.requireNonNull(deviceObjectDTO.getId());
        Objects.requireNonNull(deviceObjectDTO.getDeviceModelId());
        //checkArgument(deviceObject.getId().equals(deviceObjectId));

        if (!canAccessContObject(contObjectId)) {
            return ApiResponse.responseForbidden();
        }

        /////////////////////////////////////////////
        ApiActionObjectProcess actionProcess = () -> {
            //final DeviceObjectDTO deviceObjectDTO = modelMapper.map(deviceObject, DeviceObjectDTO.class);
            DeviceObject deviceObject =  deviceObjectService.saveDeviceObjectDTO_lvlS1(deviceObjectDTO);
            DeviceObjectFullVM fullVM = deviceObjectMapper.toFullVM(deviceObject);
            fullVM.shareDeviceLoginInfo(deviceObject);
            return fullVM;
        };
        return ApiResponse.responseUpdate(actionProcess);

    }

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObjectMetaVzlet result = deviceObjectService.selectDeviceObjectMetaVzlet(deviceObjectId);

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet/{entityId}",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectMetaVzletId(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId, @PathVariable("entityId") Long entityId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObjectMetaVzlet result = deviceObjectService.selectDeviceObjectMetaVzlet(deviceObjectId);

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param deviceObjectMetaVzlet
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> createDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet, HttpServletRequest request) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (!deviceObjectMetaVzlet.isNew()) {
			return ApiResponse.responseBadRequest();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);

		deviceObjectMetaVzlet.setDeviceObject(deviceObject);

		ApiActionLocation action = new ApiActionEntityLocationAdapter<DeviceObjectMetaVzlet, Long>(
				deviceObjectMetaVzlet, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public DeviceObjectMetaVzlet processAndReturnResult() {
				return deviceObjectService.updateDeviceObjectMetaVzlet(entity);
			}

		};

		deviceObjectMetaVzlet.setDeviceObjectId(deviceObjectId);

		return ApiActionTool.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param contObjectId
     * @param deviceObjectId
     * @param deviceObjectMetaVzlet
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> updateDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (deviceObjectMetaVzlet.isNew()) {
			return ApiResponse.responseBadRequest();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		deviceObjectMetaVzlet.setDeviceObject(deviceObject);

		ApiAction action = new ApiActionEntityAdapter<DeviceObjectMetaVzlet>(deviceObjectMetaVzlet) {

			@Override
			public DeviceObjectMetaVzlet processAndReturnResult() {
				return deviceObjectService.updateDeviceObjectMetaVzlet(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> deleteDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = (ApiActionAdapter) () -> deviceObjectService.deleteDeviceObjectMetaVzlet(deviceObjectId);

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/metaVzlet/system", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectMetaVzletSystem() {
		List<VzletSystem> preList = vzletSystemRepository.findAll();
		List<VzletSystem> result = preList.stream().sorted(Comparator.comparingLong(VzletSystem::getId)).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceModels() {
		ApiActionObjectProcess actionProcess = () -> {
			List<DeviceModelDTO> deviceModels = deviceModelService.findDeviceModelDTOs();
			if (!portalUserIdsService.isSystemUser()) {
				deviceModels = ObjectFilters.devModeFilter(deviceModels);
			}
			return deviceModels;
		};
		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{id}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceModel(@PathVariable("id") Long deviceModelId) {
		ApiActionObjectProcess actionProcess = () -> {
			DeviceModelDTO deviceModel = deviceModelService.findDeviceModelDTO(deviceModelId);
			return deviceModel;
		};
		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModelTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceModelTypes() {
		return ApiResponse.responseOK(() -> deviceDataTypeService.findDeviceDataTypes());
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceDataTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceDataTypes() {
		return ApiResponse.responseOK(() -> deviceDataTypeService.findDeviceDataTypes());
	}


    /**
     *
     * @return
     */
    @RequestMapping(value = "/deviceObjects/heatRadiatorTypes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getHeatRadiatorTypes() {
        return ApiResponse.responseOK(() -> heatRadiatorTypeService.findAllHeatRadiators());
    }


	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/impulseCounterTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceImpulseCounterTypes() {
		return ApiResponse.responseOK(() -> deviceModelService.findImpulseCounterTypes());
	}


	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingSettings",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		DeviceObjectLoadingSettings result = deviceObjectLoadingSettingsService
				.getDeviceObjectLoadingSettings(deviceObject);

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(
			value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource/loadingSettings",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectDataSourceLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		if (deviceObject.getActiveDataSource() == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("SubscrDataSource is not set"));
		}

		SubscrDataSource subscrDataSource = deviceObject.getActiveDataSource().getSubscrDataSource();

		SubscrDataSourceLoadingSettings result = subscrDataSourceLoadingSettingsService
				.getSubscrDataSourceLoadingSettings(subscrDataSource);

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingLog",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectLoadingLog(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		DeviceObjectLoadingLog result = deviceObjectLoadingLogService.getDeviceObjectLoadingLog(deviceObject);

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{deviceModelId}/metadata", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceModelMetadata(@PathVariable("deviceModelId") Long deviceModelId) {

		List<DeviceMetadata> metadata = deviceMetadataService.selectDeviceMetadata(deviceModelId,
				DeviceMetadataService.DEVICE_METADATA_TYPE);

		return ResponseEntity.ok(!metadata.isEmpty());
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDeviceObjectSubscrDataSource(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			ApiResponse.responseForbidden();
		}

        SubscrDataSourceDTO deviceSubscrDataSourceDTO = deviceObjectService.selectDeviceObjectSubscrDataSource(deviceObjectId);
		List<SubscrDataSourceDTO> subscrDataSources = subscrDataSourceService.selectDataSourceDTOBySubscriber(portalUserIdsService.getCurrentIds().getSubscriberId());

        Optional<SubscrDataSourceDTO> findDataSource = subscrDataSources.stream()
            .filter(i -> Objects.nonNull(i.getId()))
            .filter(i -> deviceSubscrDataSourceDTO != null && i.getId().equals(deviceSubscrDataSourceDTO.getId()))
            .findAny();

        if (!findDataSource.isPresent() && deviceSubscrDataSourceDTO != null) {
            subscrDataSources.add(0, deviceSubscrDataSourceDTO);
        }

        return ApiResponse.responseOK(subscrDataSources);

//		if (deviceObject == null) {
//			return ApiResponse.responseBadRequest();
//		}
//
//		if (deviceObject.getActiveDataSource() != null && !result.stream()
//				.anyMatch(i -> (deviceObject.getActiveDataSource().getSubscrDataSource() != null &&
//                    i.getId().equals(deviceObject.getActiveDataSource().getSubscrDataSource().getId())))) {
//			Long subscrDataSourceId = deviceObject.getActiveDataSource().getSubscrDataSource().getId();
//			SubscrDataSource extraDataSurce = subscrDataSourceService.findOne(subscrDataSourceId);
//			if (extraDataSurce != null) {
//				extraDataSurce.set_isAnotherSubscriber(true);
//				result.add(0, extraDataSurce);
//			}
//		}
//
//		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
	}

    @GetMapping ("/device-objects/cont-zpoints/{contZPointId}/history")
    @Timed
	public ResponseEntity<?> deviceObjectsHistory (@PathVariable("contZPointId") Long contZPointId) {
	    List<ContZPointDeviceHistoryDTO> historyList = contZPointDeviceHistoryService.findHistory(new ContZPoint().id(contZPointId));
	    return ApiResponse.responseOK(historyList);
    }
}
