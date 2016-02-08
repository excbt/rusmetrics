package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

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
import ru.excbt.datafuse.nmk.data.model.support.DataSourceInfo;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

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

		ContObject contObject = contObjectService.findOne(contObjectId);
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

		ApiAction action = new AbstractEntityApiAction<DeviceObject>(deviceObject) {
			@Override
			public void process() {
				DeviceObject result = deviceObjectService.saveOne(entity, deviceObjectDataSource);
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

		ContObject contObject = contObjectService.findOne(contObjectId);
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

		ApiActionLocation action = new EntityApiActionLocationAdapter<DeviceObject, Long>(deviceObject, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public DeviceObject processAndReturnResult() {
				return deviceObjectService.saveOne(entity, deviceObjectDataSource);
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

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				if (isPermanent) {
					deviceObjectService.deleteOnePermanent(deviceObjectId);
				} else {
					deviceObjectService.deleteOne(deviceObjectId);
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
		return responseOK(ObjectFilters.deletedFilter(deviceObjects));
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

		DeviceObject deviceObject = deviceObjectService.findOne(deviceObjectId);
		if (deviceObject == null) {
			return responseBadRequest(ApiResult.badRequest("deviceObject (id=%d) is not found", deviceObjectId));
		}

		if (!requestEntity.isNew() && !deviceObjectId.equals(requestEntity.getDeviceObjectId())) {
			return responseBadRequest(
					ApiResult.badRequest("Wrong deviceObjectId (%d) in deviceObjectLoadingSettings ", deviceObjectId));
		}

		requestEntity.setDeviceObject(deviceObject);

		ApiAction action = new EntityApiActionAdapter<DeviceObjectLoadingSettings>(requestEntity) {

			@Override
			public DeviceObjectLoadingSettings processAndReturnResult() {
				return deviceObjectLoadingSettingsService.saveOne(requestEntity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
