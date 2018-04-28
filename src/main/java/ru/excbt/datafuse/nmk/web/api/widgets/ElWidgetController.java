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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.data.service.widget.ElWidgetService;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

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

	private final ContObjectService contObjectService;

	private final ElWidgetService elWidgetService;

	@Autowired
    public ElWidgetController(ContEventMonitorV3Service contEventMonitorV3Service,
                              ContZPointService contZPointService,
                              ContObjectService contObjectService,
                              ElWidgetService elWidgetService,
                              ObjectAccessService objectAccessService,
                              PortalUserIdsService portalUserIdsService,
                              SubscriberTimeService subscriberTimeService) {
        super(contEventMonitorV3Service, contZPointService, objectAccessService, portalUserIdsService, subscriberTimeService);
        this.contObjectService = contObjectService;
        this.elWidgetService = elWidgetService;
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
        if (!objectAccessService.checkContZPointId(contZpointId, portalUserIdsService.getCurrentIds())) {
			ApiResponse.responseForbidden();
		}

		if (mode == null || !elWidgetService.isModeSupported(mode)) {
			return ApiResponse.responseBadRequest();
		}

		ZonedDateTime d = subscriberTimeService.getSubscriberZonedDateTime(portalUserIdsService.getCurrentIds());

		ApiActionProcess<List<ContServiceDataElCons>> action = () -> ObjectFilters
				.deletedFilter(elWidgetService.selectChartData(contZpointId, d, mode.toUpperCase()));

		return ApiResponse.responseOK(action);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus(@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

        if (!objectAccessService.checkContZPointId(contZpointId, portalUserIdsService.getCurrentIds())) {
			ApiResponse.responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		if (contObjectId == null) {
			return ApiResponse.responseBadRequest();
		}

		ZonedDateTime subscriberDateTime = subscriberTimeService.getSubscriberZonedDateTime(portalUserIdsService.getCurrentIds());

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
