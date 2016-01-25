package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetadataService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaDeviceObjectMetadataController extends SubscrApiController {

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects/metadata/measureUnits", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getMeasureUnits() {

		List<MeasureUnit> resultList = deviceObjectMetadataService.selectMeasureUnits();

		return responseOK(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metadata",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<DeviceObjectMetadata> resultList = deviceObjectMetadataService.selectDeviceObjectMetadata(deviceObjectId);

		return ResponseEntity.ok(resultList);
	}

}
