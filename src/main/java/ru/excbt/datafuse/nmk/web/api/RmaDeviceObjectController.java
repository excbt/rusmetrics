package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSourceLoadingSettings;
import ru.excbt.datafuse.nmk.data.model.V_DeviceObjectTimeOffset;
import ru.excbt.datafuse.nmk.data.model.dto.ActiveDataSourceInfoDTO;
import ru.excbt.datafuse.nmk.data.model.vo.DeviceObjectVO;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 * Контроллер для работы с приборами для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaDeviceObjectController extends SubscrDeviceObjectController {

	private static final Logger log = LoggerFactory.getLogger(RmaDeviceObjectController.class);

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionObjectProcess actionProcess = () -> {
			List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsByContObjectId(contObjectId);
			for (DeviceObject deviceObject : deviceObjects) {
				deviceObject.shareDeviceLoginInfo();
			}
			return ObjectFilters.deletedFilter(deviceObjects);
		};
		return ApiResponse.responseOK(actionProcess);

	}

	/**
	 *
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObject(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionProcess<DeviceObject> actionProcess = () -> {

			DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);

			deviceObject.shareDeviceLoginInfo();
			return deviceObject;
		};

		Function<DeviceObject, ResponseEntity<?>> extraCheck = (x) -> {
			if (x == null) {
				return ApiResponse.responseNoContent();
			}
			if (x.getContObject() == null || !contObjectId.equals(x.getContObject().getId())) {
				return ApiResponse.responseBadRequest();
			}
			return null;
		};

		return ApiResponse.responseOK(actionProcess, extraCheck);

	}

    /**
     *
     * @param contObjectId
     * @param deviceObjectId
     * @param deviceObject
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObject(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId, @RequestBody DeviceObject deviceObject) {

		checkNotNull(deviceObject);
		checkArgument(!deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());
		checkArgument(deviceObject.getId().equals(deviceObjectId));

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		/////////////////////////////////////////////
		ApiActionObjectProcess actionProcess = () -> {

			deviceObject.setContObject(new ContObject().id(contObjectId));
			deviceObject.setDeviceModel(new DeviceModel().id(deviceObject.getDeviceModelId()));

			ActiveDataSourceInfoDTO dsi = deviceObject.getEditDataSourceInfo();

			DeviceObjectDataSource deviceObjectDataSource = (dsi == null || dsi.getSubscrDataSourceId() == null) ? null
					: new DeviceObjectDataSource();

			if (deviceObjectDataSource != null && dsi != null) {
				SubscrDataSource subscrDataSource = subscrDataSourceService.findOne(dsi.getSubscrDataSourceId());
				deviceObjectDataSource.setSubscrDataSource(subscrDataSource);
				deviceObjectDataSource.setSubscrDataSourceAddr(dsi.getSubscrDataSourceAddr());
				deviceObjectDataSource.setDataSourceTable(dsi.getDataSourceTable());
				deviceObjectDataSource.setDataSourceTable1h(dsi.getDataSourceTable1h());
				deviceObjectDataSource.setDataSourceTable24h(dsi.getDataSourceTable24h());
				deviceObjectDataSource.setIsActive(true);
			}

			deviceObject.saveDeviceObjectCredentials();

			DeviceObject result = deviceObjectService.saveDeviceObject(deviceObject, deviceObjectDataSource);
			result.shareDeviceLoginInfo();
			return result;
		};
		return ApiResponse.responseUpdate(actionProcess);

	}

    /**
     *
     * @param contObjectId
     * @param deviceObject
     * @param request
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObjectByContObject(@PathVariable("contObjectId") Long contObjectId,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

        return createDeviceObject(contObjectId,deviceObject, request);

	}

    /**
     *
     * @param contObjectId
     * @param deviceObject
     * @param request
     * @return
     */
	@RequestMapping(value = "/contObjects/deviceObjects", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObject(
			@RequestParam(value = "contObjectId", required = true) Long contObjectId,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

        checkNotNull(deviceObject);
        checkArgument(deviceObject.isNew());
        checkNotNull(deviceObject.getDeviceModelId());

        if (!canAccessContObject(contObjectId)) {
            return ApiResponse.responseForbidden();
        }

        ApiActionProcess<DeviceObject> actionProcess = () ->
            deviceObjectService.automationCreate(contObjectId, deviceObject);

        return ApiResponse.responseCreate(actionProcess, () -> request.getRequestURI());
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDeviceObject(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionVoidProcess actionProcess = () -> {
			if (isPermanent) {
				deviceObjectService.deleteDeviceObjectPermanent(deviceObjectId);
			} else {
				deviceObjectService.deleteDeviceObject(deviceObjectId);
			}
		};
		return ApiResponse.responseDelete(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects() {

		ApiActionObjectProcess actionProcess = () -> {
			List<DeviceObject> deviceObjects = deviceObjectService
					.selectDeviceObjectsBySubscriber(getCurrentSubscriberId());

			for (DeviceObject deviceObject : deviceObjects) {
				deviceObject.shareDeviceLoginInfo();
			}

			List<DeviceObjectVO> deviceObjectVOs = deviceObjects.stream()
					.filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> new DeviceObjectVO(i))
					.collect(Collectors.toList());

			List<Long> deviceObjectIds = deviceObjects.stream().map(DeviceObject::getId).distinct()
					.collect(Collectors.toList());

			List<V_DeviceObjectTimeOffset> offsetList = deviceObjectService
					.selectDeviceObjsetTimeOffset(deviceObjectIds);

			Map<Long, V_DeviceObjectTimeOffset> offsetMap = offsetList.stream()
					.collect(Collectors.toMap(V_DeviceObjectTimeOffset::getDeviceObjectId, Function.identity()));

			deviceObjectVOs.forEach(i -> {

				V_DeviceObjectTimeOffset timeOffset = offsetMap.get(i.getModel().getId());
				i.setDeviceObjectTimeOffset(timeOffset);
			});

			return deviceObjectVOs;
		};

		return ApiResponse.responseOK(actionProcess);

	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingSettings",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectLoadingSettings requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(deviceObjectId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		if (!requestEntity.isNew() && !deviceObjectId.equals(requestEntity.getDeviceObjectId())) {
			return ApiResponse.responseBadRequest(
					ApiResult.badRequest("Wrong deviceObjectId (%d) in deviceObjectLoadingSettings ", deviceObjectId));
		}

		requestEntity.setDeviceObject(deviceObject);

		ApiActionObjectProcess actionProcess = () -> deviceObjectLoadingSettingsService.saveOne(requestEntity);
		return ApiResponse.responseUpdate(actionProcess);

		//		ApiAction action = new ApiActionEntityAdapter<DeviceObjectLoadingSettings>(requestEntity) {
		//
		//			@Override
		//			public DeviceObjectLoadingSettings processAndReturnResult() {
		//				return deviceObjectLoadingSettingsService.saveOne(requestEntity);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(
			value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource/loadingSettings",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectDataSourceLoadingSettings(
			@PathVariable("contObjectId") Long contObjectId, @PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody SubscrDataSourceLoadingSettings requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(deviceObjectId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		SubscrDataSource subscrDataSource = deviceObject.getActiveDataSource().getSubscrDataSource();

		if (!requestEntity.isNew() && !subscrDataSource.getId().equals(requestEntity.getSubscrDataSourceId())) {
			return ApiResponse.responseBadRequest(ApiResult
					.badRequest("Wrong subscrDataSourceId (%d) in subscrDataSourceLoadingSettings ", deviceObjectId));
		}

		requestEntity.setSubscrDataSource(subscrDataSource);

		ApiActionObjectProcess actionProcess = () -> subscrDataSourceLoadingSettingsService
				.saveSubscrDataSourceLoadingSettings(requestEntity);
		return ApiResponse.responseUpdate(actionProcess);

		//		ApiAction action = new ApiActionEntityAdapter<SubscrDataSourceLoadingSettings>(requestEntity) {
		//
		//			@Override
		//			public SubscrDataSourceLoadingSettings processAndReturnResult() {
		//				return subscrDataSourceLoadingSettingsService.saveSubscrDataSourceLoadingSettings(entity);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param deviceModelId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{id}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceModel(@PathVariable("id") Long deviceModelId,
			@RequestBody DeviceModel requestEntity) {

		if (!isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		if (deviceModelId == null || !deviceModelId.equals(requestEntity.getId())) {
			return ApiResponse.responseBadRequest();
		}

		ApiActionObjectProcess actionProcess = () -> deviceModelService.save(requestEntity);

		return ApiResponse.responseUpdate(actionProcess);

	}

    /**
     *
     * @param requestEntity
     * @param request
     * @return
     */
	@RequestMapping(value = "/deviceObjects/deviceModels", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceModel(@RequestBody DeviceModel requestEntity, HttpServletRequest request) {

		if (!isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		ApiActionProcess<DeviceModel> actionProcess = () -> {
			return deviceModelService.save(requestEntity);
		};

		return ApiResponse.responseCreate(actionProcess, () -> request.getRequestURI());
	}

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels/{id}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDeviceModel(@PathVariable("id") Long deviceModelId) {

		if (!isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		ApiActionVoidProcess actionProcess = () -> {
			DeviceModel deviceModel = deviceModelService.findDeviceModel(deviceModelId);
			deviceModel.setDeleted(1);
			deviceModelService.save(deviceModel);
		};

		return ApiResponse.responseDelete(actionProcess);

	}

}
