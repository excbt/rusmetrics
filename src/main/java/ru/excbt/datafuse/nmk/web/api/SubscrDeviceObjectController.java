package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrDeviceObjectController extends WebApiController {

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjects(
			@PathVariable("contObjectId") Long contObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<DeviceObject> deviceObjects = deviceObjectService
				.selectDeviceObjectsByContObjectId(contObjectId);

		return ResponseEntity.ok(deviceObjects);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetaVzlet(
			@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObjectMetaVzlet result = deviceObjectService
				.selectDeviceObjectMetaVzlet(deviceObjectId);

		if (result == null) {
			ResponseEntity.ok();
		}

		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet/{entityId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetaVzletId(
			@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@PathVariable("entityId") Long entityId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		DeviceObjectMetaVzlet result = deviceObjectService
				.selectDeviceObjectMetaVzlet(deviceObjectId);

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
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDeviceObjectMetaVzlet(
			@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet,
			HttpServletRequest request) {

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
				setResultEntity(deviceObjectService
						.updateDeviceObjectMetaVzlet(entity));
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
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectMetaVzlet(
			@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody DeviceObjectMetaVzlet deviceObjectMetaVzlet) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		if (deviceObjectMetaVzlet.isNew()) {
			return responseBadRequest();
		}

		deviceObjectMetaVzlet.setDeviceObjectId(deviceObjectId);

		ApiAction action = new AbstractEntityApiAction<DeviceObjectMetaVzlet>(
				deviceObjectMetaVzlet) {

			@Override
			public void process() {
				setResultEntity(deviceObjectService
						.updateDeviceObjectMetaVzlet(entity));

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
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDeviceObjectMetaVzlet(
			@PathVariable("contObjectId") Long contObjectId,
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
	 * @param contObjectId
	 * @return
	 */
	private boolean canAccessContObject(Long contObjectId) {
		if (contObjectId == null) {
			return false;
		}
		List<Long> contObjectIds = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());
		return contObjectIds.contains(contObjectId);
	}

}
