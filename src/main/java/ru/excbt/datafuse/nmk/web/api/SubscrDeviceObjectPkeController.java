package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceObjectPkeType;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectPkeService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr/deviceObjects/pke")
public class SubscrDeviceObjectPkeController extends SubscrApiController {

	@Autowired
	protected DeviceObjectPkeService deviceObjectPkeService;

	/**
	 * 
	 * @param deviceObjectId
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{deviceObjectId}/warn", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectPkeWarn(@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkPeriod = checkLocalDatePeriodParser(datePeriodParser);
		if (checkPeriod != null) {
			return checkPeriod;
		}

		List<DeviceObjectPkeWarn> resultList = deviceObjectPkeService.selectDeviceObjectPkeWarn(deviceObjectId,
				datePeriodParser.getLocalDatePeriod());

		return responseOK(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectPkeType() {
		List<DeviceObjectPkeType> resultList = deviceObjectPkeService.selectDeviceObjectPkeType();
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
