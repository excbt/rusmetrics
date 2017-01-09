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
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

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

	@Inject
	private ContObjectService contObjectService;

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
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

		ZonedDateTime subscriberDateTime = getSubscriberZonedDateTime();

		List<ContServiceDataHWater> resultData = contServiceDataHWaterService.selectLastDataFromDate(contZpointId,
				TimeDetailKey.TYPE_1H.getKeyname(), subscriberDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate());

		WeatherForecast weatherForecast = contObjectService.selectWeatherForecast(contObjectId,
				subscriberDateTime.toLocalDate());

		Map<String, Object> result = new HashMap<>();
		result.put("color", getMonitorColorValue(contObjectId, contZpointId).getKeyname());
		if (weatherForecast != null && subscriberDateTime.toLocalDate()
				.compareTo(LocalDateUtils.asLocalDate(weatherForecast.getForecastDateTime())) == 0) {
			result.put("forecastTemp", weatherForecast.getTemperatureValue());
		}
		if (resultData.size() > 0) {
			result.put("lastHwData", resultData.get(0));
		}

		return responseOK(result);
	}

}
