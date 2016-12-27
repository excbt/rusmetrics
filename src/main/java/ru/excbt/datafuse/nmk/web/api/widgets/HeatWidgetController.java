/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Sets;

import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.data.service.widget.HeatWidgetService;

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

		//java.time.LocalDate d = LocalDateUtils.asLocalDate(getCurrentSubscriberDate());
		java.time.LocalDate d = java.time.LocalDate.of(2016, 03, 07);

		List<HeatWidgetTemperatureDto> resultList = heatWidgetService.selectWidgetData(contZpointId, d,
				mode.toUpperCase());
		return responseOK(resultList);
	}

}
