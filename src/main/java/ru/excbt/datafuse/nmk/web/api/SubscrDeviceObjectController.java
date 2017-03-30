package ru.excbt.datafuse.nmk.web.api;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
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
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dmo.DeviceObjectDMO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.repository.VzletSystemRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.DeviceMetadataService;
import ru.excbt.datafuse.nmk.data.service.DeviceModelService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectLoadingLogService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectLoadingSettingsService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceLoadingSettingsService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.web.api.support.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с приборами для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrDeviceObjectController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectController.class);

	@Autowired
	protected DeviceObjectService deviceObjectService;

	@Autowired
	protected DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	@Autowired
	protected DeviceObjectLoadingLogService deviceObjectLoadingLogService;

	@Autowired
	protected VzletSystemRepository vzletSystemRepository;

	@Autowired
	protected DeviceModelService deviceModelService;

	@Autowired
	protected ContObjectService contObjectService;

	@Autowired
	protected SubscrDataSourceService subscrDataSourceService;

	@Autowired
	protected DeviceMetadataService deviceMetadataService;

	@Autowired
	protected SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService;

    @Autowired
    private ModelMapper modelMapper;

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsByContObjectId(contObjectId);

		return responseOK(ObjectFilters.deletedFilter(deviceObjects));
	}

    /**
     * TODO
     * @param contObjectId
     * @param deviceObjectId
     * @return
     */
    @RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.GET,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getDeviceObject(@PathVariable("contObjectId") Long contObjectId,
                                                         @PathVariable("deviceObjectId") Long deviceObjectId) {


        if (!canAccessContObject(contObjectId)) {
            return responseForbidden();
        }

        ApiActionProcess<DeviceObject> actionProcess = () -> {

            DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);

            if (SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
                deviceObject.shareDeviceLoginInfo();
            }
            return deviceObject;
        };

        Function<DeviceObject, ResponseEntity<?>> extraCheck = (x) -> {
            if (x == null) {
                return responseNoContent();
            }
            if (x.getContObject() == null || !x.getContObject().getId().equals(contObjectId)) {
                return responseBadRequest();
            }
            return null;
        };

        return responseOK(actionProcess, extraCheck);

    }

    @RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.PUT,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> saveDeviceObject(@PathVariable("contObjectId") Long contObjectId,
                                              @PathVariable("deviceObjectId") Long deviceObjectId, @RequestBody DeviceObject deviceObject) {

        checkNotNull(deviceObject);
        checkArgument(!deviceObject.isNew());
        checkNotNull(deviceObject.getDeviceModelId());
        checkArgument(deviceObject.getId().equals(deviceObjectId));

        if (!canAccessContObject(contObjectId)) {
            return responseForbidden();
        }

        /////////////////////////////////////////////
        ApiActionObjectProcess actionProcess = () -> {
            final DeviceObjectDTO deviceObjectDTO = modelMapper.map(deviceObject, DeviceObjectDTO.class);
            final DeviceObjectDMO deviceObjectDMO = deviceObjectService.convert(deviceObjectDTO);
            DeviceObject result =  deviceObjectService.saveDeviceObjectDMO(deviceObjectDMO);

            return result;
        };
        return responseUpdate(actionProcess);

    }

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
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
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetaVzletId(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId, @PathVariable("entityId") Long entityId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
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
			method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet, HttpServletRequest request) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		if (!deviceObjectMetaVzlet.isNew()) {
			return responseBadRequest();
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

		return WebApiHelper.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param contObjectId
     * @param deviceObjectId
     * @param deviceObjectMetaVzlet
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		if (deviceObjectMetaVzlet.isNew()) {
			return responseBadRequest();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		deviceObjectMetaVzlet.setDeviceObject(deviceObject);

		ApiAction action = new ApiActionEntityAdapter<DeviceObjectMetaVzlet>(deviceObjectMetaVzlet) {

			@Override
			public DeviceObjectMetaVzlet processAndReturnResult() {
				return deviceObjectService.updateDeviceObjectMetaVzlet(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet",
			method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDeviceObjectMetaVzlet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ApiAction action = (ApiActionAdapter) () -> deviceObjectService.deleteDeviceObjectMetaVzlet(deviceObjectId);

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/metaVzlet/system", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetaVzletSystem() {
		List<VzletSystem> preList = vzletSystemRepository.findAll();
		List<VzletSystem> result = preList.stream().sorted(Comparator.comparingLong(VzletSystem::getId)).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceModels() {
		ApiActionObjectProcess actionProcess = () -> {
			List<DeviceModel> deviceModels = deviceModelService.findDeviceModelAll();
			deviceModels.sort(DeviceModelService.COMPARE_BY_NAME);
			if (!currentSubscriberService.isSystemUser()) {
				deviceModels = ObjectFilters.devModeFilter(deviceModels);
			}
			return deviceModels;
		};
		return responseOK(actionProcess);
	}

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{id}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceModel(@PathVariable("id") Long deviceModelId) {
		ApiActionObjectProcess actionProcess = () -> {
			DeviceModel deviceModel = deviceModelService.findDeviceModel(deviceModelId);
			return deviceModel;
		};
		return responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModelTypes", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceModelTypes() {
		return responseOK(() -> deviceModelService.findDeviceModelTypes());
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/impulseCounterTypes", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceImpulseCounterTypes() {
		return responseOK(() -> deviceModelService.findImpulseCounterTypes());
	}


	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingSettings",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		DeviceObjectLoadingSettings result = deviceObjectLoadingSettingsService
				.getDeviceObjectLoadingSettings(deviceObject);

		return responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(
			value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource/loadingSettings",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectDataSourceLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		if (deviceObject.getActiveDataSource() == null) {
			return responseBadRequest(ApiResult.badRequest("SubscrDataSource is not set"));
		}

		SubscrDataSource subscrDataSource = deviceObject.getActiveDataSource().getSubscrDataSource();

		SubscrDataSourceLoadingSettings result = subscrDataSourceLoadingSettingsService
				.getSubscrDataSourceLoadingSettings(subscrDataSource);

		return responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingLog",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectLoadingLog(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		DeviceObjectLoadingLog result = deviceObjectLoadingLogService.getDeviceObjectLoadingLog(deviceObject);

		return responseOK(result);
	}

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{deviceModelId}/metadata", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
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
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectSubscrDataSource(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest();
		}

		List<SubscrDataSource> result = subscrDataSourceService.selectDataSourceBySubscriber(getCurrentSubscriberId());
		if (deviceObject.getActiveDataSource() != null && !result.stream()
				.anyMatch(i -> i.getId().equals(deviceObject.getActiveDataSource().getSubscrDataSourceId()))) {
			Long subscrDataSourceId = deviceObject.getActiveDataSource().getSubscrDataSourceId();
			SubscrDataSource extraDataSurce = subscrDataSourceService.findOne(subscrDataSourceId);
			if (extraDataSurce != null) {
				extraDataSurce.set_isAnotherSubscriber(true);
				result.add(0, extraDataSurce);
			}
		}

		return responseOK(ObjectFilters.deletedFilter(result));
	}

}
