package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationStatusV2Service;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping("/api/subscr/contEventMonitor")
public class SubscrContEventMonitorController extends AbstractSubscrApiResource {

	@Autowired
	private SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service;

	/**
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/cityStatusCollapse", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> cityMonitorContEventsStatusCollapse(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor,
			@RequestParam(value = "contGroupId", required = false) Long contGroupId) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDate:{} is greater than toDate:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(getSubscriberParam(),
				contGroupId);

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotifiicationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(getSubscriberParam(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ResponseEntity.ok(result);
	}

}
