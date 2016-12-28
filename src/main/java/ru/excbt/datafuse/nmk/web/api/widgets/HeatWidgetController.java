/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Sets;

import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.widget.HeatWidgetService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 * 
 */
@Controller
@RequestMapping(value = "/api/subscr/widgets/heat/{contZpointId}")
public class HeatWidgetController extends WidgetController {

	private final static String[] availableModes = { "TODAY", "YESTERDAY", "WEEK" };

	@Autowired
	private HeatWidgetService heatWidgetService;

	@Autowired
	private ContObjectService contObjectService;

	/**
	 * 
	 * @param contZpointId
	 * @param mode
	 * @return
	 */
	@RequestMapping(value = "/chart/data/{mode}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getHeatWidgetTemperature(
			@PathVariable(value = "contZpointId", required = true) Long contZpointId,
			@PathVariable(value = "mode", required = true) String mode) {
		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		if (mode == null || !Sets.newHashSet(availableModes).contains(mode.toUpperCase())) {
			return responseBadRequest();
		}

		java.time.LocalDate d = LocalDateUtils.asLocalDate(getCurrentSubscriberDate());
		//java.time.LocalDate d = java.time.LocalDate.of(2016, 03, 07);

		List<HeatWidgetTemperatureDto> resultList = heatWidgetService.selectChartData(contZpointId, d,
				mode.toUpperCase());
		return responseOK(resultList);
	}

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

		java.time.LocalDate currentDate = LocalDateUtils.asLocalDate(getCurrentSubscriberDate());

		WeatherForecast weatherForecast = contObjectService.selectWeatherForecast(contObjectId, currentDate);

		Map<String, Object> result = new HashMap<>();
		result.put("color", getMonitorColorValue(contObjectId, contZpointId).getKeyname());
		if (weatherForecast != null
				&& currentDate.compareTo(LocalDateUtils.asLocalDate(weatherForecast.getForecastDateTime())) == 0) {
			result.put("forecastTemp", weatherForecast.getTemperatureValue());
		}

		return responseOK(result);
	}

}
