/**
 *
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.widget.HeatWidgetService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 *
 */
@RestController
@RequestMapping(value = "/api/subscr/widgets/heat/{contZpointId}")
public class HeatWidgetController extends WidgetController {

	private final HeatWidgetService heatWidgetService;

	private final ContObjectService contObjectService;

    @Autowired
	public HeatWidgetController(ContEventMonitorV3Service contEventMonitorV3Service, ContZPointService contZPointService, HeatWidgetService heatWidgetService, ContObjectService contObjectService) {
        super(contEventMonitorV3Service, contZPointService);
        this.heatWidgetService = heatWidgetService;
        this.contObjectService = contObjectService;
    }

    /**
	 *
	 * @param contZpointId
	 * @param mode
	 * @return
	 */
	@RequestMapping(value = "/chart/data/{mode}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getChartData(@PathVariable(value = "contZpointId", required = true) Long contZpointId,
			@PathVariable(value = "mode", required = true) String mode) {
		if (!canAccessContZPoint(contZpointId)) {
			ApiResponse.responseForbidden();
		}

		if (mode == null || !heatWidgetService.isModeSupported(mode)) {
			return ApiResponse.responseBadRequest();
		}

		ZonedDateTime d = getSubscriberZonedDateTime();

		ApiActionProcess<List<HeatWidgetTemperatureDto>> action = () -> heatWidgetService.selectChartData(contZpointId,
				d, mode.toUpperCase());

		return ApiResponse.responseOK(action);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus(@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

		if (!canAccessContZPoint(contZpointId)) {
			ApiResponse.responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		if (contObjectId == null) {
			return ApiResponse.responseBadRequest();
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

		return ApiResponse.responseOK(result);
	}

}
