/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
@Controller
@RequestMapping(value = "/api/subscr/widgets/hw/{contZpointId}")
public class HwWidgetController extends WidgetController {

	@Inject
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZpointStatus(
			@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		if (contObjectId == null) {
			return responseBadRequest();
		}

		//		contServiceDataHWaterService.selectLast

		ZonedDateTime currentDate = getSubscriberZonedDateTime();

		List<ContServiceDataHWater> resultData = contServiceDataHWaterService.selectLastDataFromDate(contZpointId,
				TimeDetailKey.TYPE_1H.getKeyname(), currentDate.truncatedTo(ChronoUnit.DAYS));

		//		java.time.LocalDate currentDate = LocalDateUtils.asLocalDate(getCurrentSubscriberDate());

		//	WeatherForecast weatherForecast = contObjectService.selectWeatherForecast(contObjectId, currentDate);

		Map<String, Object> result = new HashMap<>();
		result.put("color", getMonitorColorValue(contObjectId, contZpointId).getKeyname());
		//		if (resultData.size() > 0
		//				&& currentDate.compareTo(LocalDateUtils.asLocalDate(weatherForecast.getForecastDateTime())) == 0) {
		//			result.put("forecastTemp", weatherForecast.getTemperatureValue());
		//		}

		return responseOK(result);
	}

}
