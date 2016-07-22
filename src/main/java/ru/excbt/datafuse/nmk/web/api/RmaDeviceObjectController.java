package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
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
import ru.excbt.datafuse.nmk.data.model.support.DataSourceInfo;
import ru.excbt.datafuse.nmk.data.model.vo.DeviceObjectVO;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

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

	private static final Logger logger = LoggerFactory.getLogger(RmaDeviceObjectController.class);

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects(@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsByContObjectId(contObjectId);
		for (DeviceObject deviceObject : deviceObjects) {
			deviceObject.shareDeviceLoginInfo();
		}

		return responseOK(ObjectFilters.deletedFilter(deviceObjects));
	}

	/**
	 * 
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deviceObjectByContObjectGet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);

		if (deviceObject == null) {
			return responseNoContent();
		}

		if (deviceObject.getContObject() == null || !contObjectId.equals(deviceObject.getContObject().getId())) {
			return responseBadRequest();
		}

		deviceObject.shareDeviceLoginInfo();

		return ResponseEntity.ok(deviceObject);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param subscrDataSourceId
	 * @param subscrDataSourceAddr
	 * @param deviceObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectByContObject(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId, @RequestBody DeviceObject deviceObject) {

		checkNotNull(deviceObject);
		checkArgument(!deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());
		checkArgument(deviceObject.getId().equals(deviceObjectId));

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject contObject = contObjectService.findContObject(contObjectId);
		deviceObject.setContObject(contObject);
		DeviceModel deviceModel = deviceModelService.findOne(deviceObject.getDeviceModelId());
		deviceObject.setDeviceModel(deviceModel);

		DataSourceInfo dsi = deviceObject.getEditDataSourceInfo();

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

		deviceObject.saveDeviceObjectInfo();

		ApiAction action = new AbstractEntityApiAction<DeviceObject>(deviceObject) {
			@Override
			public void process() {
				DeviceObject result = deviceObjectService.saveDeviceObject(entity, deviceObjectDataSource);
				result.shareDeviceLoginInfo();
				setResultEntity(result);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param subscrDataSourceId
	 * @param subscrDataSourceAddr
	 * @param deviceObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObjectByContObject(@PathVariable("contObjectId") Long contObjectId,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

		return createDeviceObjectInternal(contObjectId, deviceObject, request);

	}

	/**
	 * 
	 * @param contObjectId
	 * @param subscrDataSourceId
	 * @param subscrDataSourceAddr
	 * @param deviceObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObject(
			@RequestParam(value = "contObjectId", required = true) Long contObjectId,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

		return createDeviceObjectInternal(contObjectId, deviceObject, request);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param subscrDataSourceId
	 * @param subscrDataSourceAddr
	 * @param deviceObject
	 * @param request
	 * @return
	 */
	private ResponseEntity<?> createDeviceObjectInternal(Long contObjectId, DeviceObject deviceObject,
			HttpServletRequest request) {
		checkNotNull(deviceObject);
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject contObject = contObjectService.findContObject(contObjectId);
		deviceObject.setContObject(contObject);
		DeviceModel deviceModel = deviceModelService.findOne(deviceObject.getDeviceModelId());
		deviceObject.setDeviceModel(deviceModel);

		DataSourceInfo dsi = deviceObject.getEditDataSourceInfo();

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

		deviceObject.saveDeviceObjectInfo();

		ApiActionLocation action = new ApiActionEntityLocationAdapter<DeviceObject, Long>(deviceObject, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public DeviceObject processAndReturnResult() {
				return deviceObjectService.saveDeviceObject(entity, deviceObjectDataSource);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDeviceObject(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				if (isPermanent) {
					deviceObjectService.deleteDeviceObjectPermanent(deviceObjectId);
				} else {
					deviceObjectService.deleteDeviceObject(deviceObjectId);
				}

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects() {
		List<DeviceObject> deviceObjects = deviceObjectService
				.selectDeviceObjectsBySubscriber(getCurrentSubscriberId());

		for (DeviceObject deviceObject : deviceObjects) {
			deviceObject.shareDeviceLoginInfo();
		}

		List<DeviceObjectVO> deviceObjectVOs = deviceObjects.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.map(i -> new DeviceObjectVO(i)).collect(Collectors.toList());

		deviceObjectVOs.forEach(i -> {
			V_DeviceObjectTimeOffset timeOffset = deviceObjectService
					.selectDeviceObjsetTimeOffset(i.getObject().getId());
			i.setDeviceObjectTimeOffset(timeOffset);
		});

		return responseOK(deviceObjectVOs);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/loadingSettings",
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putDeviceObjectLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectLoadingSettings requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(deviceObjectId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		if (!requestEntity.isNew() && !deviceObjectId.equals(requestEntity.getDeviceObjectId())) {
			return responseBadRequest(
					ApiResult.badRequest("Wrong deviceObjectId (%d) in deviceObjectLoadingSettings ", deviceObjectId));
		}

		requestEntity.setDeviceObject(deviceObject);

		ApiAction action = new ApiActionEntityAdapter<DeviceObjectLoadingSettings>(requestEntity) {

			@Override
			public DeviceObjectLoadingSettings processAndReturnResult() {
				return deviceObjectLoadingSettingsService.saveOne(requestEntity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
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
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putDeviceObjectDataSourceLoadingSettings(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody SubscrDataSourceLoadingSettings requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(deviceObjectId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		SubscrDataSource subscrDataSource = deviceObject.getActiveDataSource().getSubscrDataSource();

		if (!requestEntity.isNew() && !subscrDataSource.getId().equals(requestEntity.getSubscrDataSourceId())) {
			return responseBadRequest(ApiResult
					.badRequest("Wrong subscrDataSourceId (%d) in subscrDataSourceLoadingSettings ", deviceObjectId));
		}

		requestEntity.setSubscrDataSource(subscrDataSource);

		ApiAction action = new ApiActionEntityAdapter<SubscrDataSourceLoadingSettings>(requestEntity) {

			@Override
			public SubscrDataSourceLoadingSettings processAndReturnResult() {
				return subscrDataSourceLoadingSettingsService.saveSubscrDataSourceLoadingSettings(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
