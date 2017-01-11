/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.widget.ElWidgetService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 * 
 */
@Controller
@RequestMapping(value = "/api/subscr/widgets/el/{contZpointId}")
public class ElWidgetController extends WidgetController {

	@Inject
	private ContObjectService contObjectService;

	@Inject
	private ElWidgetService elWidgetService;

	/**
	 * 
	 * @param contZpointId
	 * @param mode
	 * @return
	 */
	@RequestMapping(value = "/chart/data/{mode}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getChartData(@PathVariable(value = "contZpointId", required = true) Long contZpointId,
			@PathVariable(value = "mode", required = true) String mode) {
		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		if (mode == null || !elWidgetService.isModeSupported(mode)) {
			return responseBadRequest();
		}

		ZonedDateTime d = getSubscriberZonedDateTime();

		ApiActionProcess<List<ContServiceDataElCons>> action = () -> ObjectFilters
				.deletedFilter(elWidgetService.selectChartData(contZpointId, d, mode.toUpperCase()));

		return responseOK(action);
	}

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus(@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		if (contObjectId == null) {
			return responseBadRequest();
		}

		ZonedDateTime subscriberDateTime = getSubscriberZonedDateTime();

		WeatherForecast weatherForecast = contObjectService.selectWeatherForecast(contObjectId,
				subscriberDateTime.toLocalDate());

		Map<String, Object> result = new HashMap<>();
		result.put("color", getMonitorColorValue(contObjectId, contZpointId).getKeyname());
		if (weatherForecast != null && subscriberDateTime.toLocalDate()
				.compareTo(LocalDateUtils.asLocalDate(weatherForecast.getForecastDateTime())) == 0) {
			result.put("forecastTemp", weatherForecast.getTemperatureValue());
		}
		return responseOK(result);
	}

}
