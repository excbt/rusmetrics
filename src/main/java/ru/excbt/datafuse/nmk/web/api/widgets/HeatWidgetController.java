/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.data.service.widget.HeatWidgetService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 * 
 */

@RequestMapping(value = "/api/subscr/widgets")
@Controller
public class HeatWidgetController extends SubscrApiController {

	@Autowired
	private HeatWidgetService heatWidgetService;

	/**
	 * 
	 * @param contZpointId
	 * @param mode
	 * @return
	 */
	@RequestMapping(value = "/chart/heatTemp", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getHeatWidgetTemperature(
			@RequestParam(value = "contZpointId", required = true) Long contZpointId,
			@RequestParam(value = "mode", required = false, defaultValue = "WEEK") String mode) {
		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		checkNotNull(contZpointId);
		checkNotNull(mode);

		//java.time.LocalDate d = LocalDateUtils.asLocalDate(getCurrentSubscriberDate());
		java.time.LocalDate d = java.time.LocalDate.of(2016, 03, 07);

		List<HeatWidgetTemperatureDto> resultList = heatWidgetService.selectWidgetData(contZpointId, d,
				mode.toUpperCase());
		return responseOK(resultList);
	}

}
