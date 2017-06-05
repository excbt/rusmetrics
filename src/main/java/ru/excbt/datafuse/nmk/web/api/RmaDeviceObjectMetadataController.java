package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.service.ContServiceTypeService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetadataService;
import ru.excbt.datafuse.nmk.data.service.MeasureUnitService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;

import java.util.List;

/**
 * Контроллер для работы с метаданными прибора для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2016
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaDeviceObjectMetadataController extends AbstractSubscrApiResource {

	@Autowired
	private MeasureUnitService measureUnitService;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	@Autowired
	private ContServiceTypeService contServiceTypeService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects/metadata/measureUnits", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getMeasureUnits(
			@RequestParam(value = "measureUnit", required = false) String measureUnit) {

		List<MeasureUnit> resultList = null;
		if (measureUnit != null) {
			resultList = measureUnitService.selectMeasureUnitsSame(measureUnit);
		} else {
			resultList = measureUnitService.selectMeasureUnits();
		}

		return responseOK(resultList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/deviceObjects/metadata/contServiceTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContServiceType() {

		List<ContServiceType> resultList = contServiceTypeService.selectContServiceType();

		return responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metadata",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<DeviceObjectMetadata> resultList = deviceObjectMetadataService.selectDeviceObjectMetadata(deviceObjectId);

		return responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param deviceObjectId
	 * @param DeviceObjectMetadataList
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metadata",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDeviceObjectMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestBody List<DeviceObjectMetadata> DeviceObjectMetadataList) {

		ApiAction action = new ApiActionEntityAdapter<List<DeviceObjectMetadata>>(DeviceObjectMetadataList) {

			@Override
			public List<DeviceObjectMetadata> processAndReturnResult() {
				return deviceObjectMetadataService.updateDeviceObjectMetadata(deviceObjectId, entity);
			}
		};
		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/deviceObjects/byContZPoint/{contZPointId}/metadata",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectMetadataByContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<List<DeviceObjectMetadata>>() {

			@Override
			public List<DeviceObjectMetadata> processAndReturnResult() {
				List<DeviceObjectMetadata> resultList = deviceObjectMetadataService.selectByContZPoint(contZPointId);

				boolean checkTransform = deviceObjectMetadataService.deviceObjectMetadataTransform(resultList);

				if (checkTransform) {
					resultList = deviceObjectMetadataService.selectByContZPoint(contZPointId);
				}
				return resultList;
			}
		};

		return WebApiHelper.processResponceApiActionOk(action);
	}

}
