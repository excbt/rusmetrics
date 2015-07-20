package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

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

		List<Long> contObjectIds = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());

		if (!contObjectIds.contains(contObjectId)) {
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

		List<Long> contObjectIds = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());

		if (!contObjectIds.contains(contObjectId)) {
			return responseForbidden();
		}

		DeviceObjectMetaVzlet result = deviceObjectService
				.selectDeviceObjectMetaVzlet(deviceObjectId);

		if (result == null) {
			ResponseEntity.ok();
		}

		return ResponseEntity.ok(result);
	}

}
