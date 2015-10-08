package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.VzletSystem;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.VzletSystemRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.DeviceModelService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrDeviceObjectController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectController.class);

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private VzletSystemRepository vzletSystemRepository;

	@Autowired
	private DeviceModelService deviceModelService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private SubscriberService subscriberService;

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

		return ResponseEntity.ok(deviceObjects);
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

		deviceObjectMetaVzlet.setDeviceObjectId(deviceObjectId);

		ApiActionLocation action = new AbstractEntityApiActionLocation<DeviceObjectMetaVzlet, Long>(
				deviceObjectMetaVzlet, request) {

			@Override
			public void process() {
				setResultEntity(deviceObjectService.updateDeviceObjectMetaVzlet(entity));
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
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
	 * @param request
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

		deviceObjectMetaVzlet.setDeviceObjectId(deviceObjectId);

		ApiAction action = new AbstractEntityApiAction<DeviceObjectMetaVzlet>(deviceObjectMetaVzlet) {

			@Override
			public void process() {
				setResultEntity(deviceObjectService.updateDeviceObjectMetaVzlet(entity));

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

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				deviceObjectService.deleteDeviceObjectMetaVzlet(deviceObjectId);

			}
		};

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
		List<VzletSystem> result = preList.stream().sorted((s1, s2) -> {
			return Long.compare(s1.getId(), s2.getId());
		}).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deviceObjects/deviceModels", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deviceModelsGet() {
		List<DeviceModel> deviceModels = deviceModelService.findAll();
		deviceModels.sort(DeviceModelService.COMPARE_BY_NAME);
		if (!currentUserService.isSystem()) {
			deviceModels = ObjectFilters.devModeFilter(deviceModels);
		}
		return ResponseEntity.ok(deviceModels);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deviceObjectByContObjectGet(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObject deviceObject = deviceObjectService.findOne(deviceObjectId);

		if (deviceObject == null) {
			return responseNoContent();
		}

		if (deviceObject.getContObject() == null || !contObjectId.equals(deviceObject.getContObject().getId())) {
			return responseBadRequest();
		}

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
	public ResponseEntity<?> deviceObjectByContObjectUpdate(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestParam(value = "subscrDataSourceId", required = false) Long subscrDataSourceId,
			@RequestParam(value = "subscrDataSourceAddr", required = false) String subscrDataSourceAddr,
			@RequestBody DeviceObject deviceObject) {

		checkNotNull(deviceObject);
		checkArgument(!deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());
		checkArgument(deviceObject.getId().equals(deviceObjectId));

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject contObject = contObjectService.findOne(contObjectId);

		deviceObject.setDeviceModel(null);
		deviceObject.setContObject(contObject);

		final DeviceObjectDataSource deviceObjectDataSource = subscrDataSourceId == null ? null
				: new DeviceObjectDataSource();

		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setSubscrDataSourceId(subscrDataSourceId);
			deviceObjectDataSource.setSubscrDataSourceAddr(subscrDataSourceAddr);
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
	public ResponseEntity<?> deviceObjectByContObjectCreate(@PathVariable("contObjectId") Long contObjectId,
			@RequestParam(value = "subscrDataSourceId", required = false) Long subscrDataSourceId,
			@RequestParam(value = "subscrDataSourceAddr", required = false) String subscrDataSourceAddr,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

		checkNotNull(deviceObject);
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject contObject = contObjectService.findOne(contObjectId);

		deviceObject.setDeviceModel(null);
		deviceObject.setContObject(contObject);

		final DeviceObjectDataSource deviceObjectDataSource = subscrDataSourceId == null ? null
				: new DeviceObjectDataSource();

		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setSubscrDataSourceId(subscrDataSourceId);
			deviceObjectDataSource.setSubscrDataSourceAddr(subscrDataSourceAddr);
			deviceObjectDataSource.setIsActive(true);
		}

		ApiActionLocation action = new AbstractEntityApiActionLocation<DeviceObject, Long>(deviceObject, request) {
			@Override
			public void process() {
				DeviceObject result = deviceObjectService.saveOne(entity, deviceObjectDataSource);
				setResultEntity(result);
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
	 * @param contObjectId
	 * @param subscrDataSourceId
	 * @param subscrDataSourceAddr
	 * @param deviceObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deviceObjectCreate(
			@RequestParam(value = "contObjectId", required = true) Long contObjectId,
			@RequestParam(value = "subscrDataSourceId", required = false) Long subscrDataSourceId,
			@RequestParam(value = "subscrDataSourceAddr", required = false) String subscrDataSourceAddr,
			@RequestBody DeviceObject deviceObject, HttpServletRequest request) {

		checkNotNull(deviceObject);
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId());

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ContObject contObject = contObjectService.findOne(contObjectId);

		deviceObject.setDeviceModel(null);
		deviceObject.setContObject(contObject);

		final DeviceObjectDataSource deviceObjectDataSource = subscrDataSourceId == null ? null
				: new DeviceObjectDataSource();

		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setSubscrDataSourceId(subscrDataSourceId);
			deviceObjectDataSource.setSubscrDataSourceAddr(subscrDataSourceAddr);
			deviceObjectDataSource.setIsActive(true);
		}

		ApiActionLocation action = new AbstractEntityApiActionLocation<DeviceObject, Long>(deviceObject, request) {
			@Override
			public void process() {
				DeviceObject result = deviceObjectService.saveOne(entity, deviceObjectDataSource);
				setResultEntity(result);
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
	public ResponseEntity<?> deviceObjectsGet() {
		List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsBySubscriber(getSubscriberId());
		return responseOK(deviceObjects);
	}

}
