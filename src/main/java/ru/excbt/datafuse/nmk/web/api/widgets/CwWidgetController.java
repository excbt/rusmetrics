/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.service.widget.CwWidgetService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 * 
 */
@Controller
@RequestMapping(value = "/api/subscr/widgets/cw/{contZpointId}")
public class CwWidgetController extends WidgetController {

	private static final Logger log = LoggerFactory.getLogger(CwWidgetController.class);

	@Inject
	private CwWidgetService cwWidgetService;

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

		if (mode == null || !cwWidgetService.getAvailableModes().contains(mode.toUpperCase())) {
			return responseBadRequest();
		}

		ZonedDateTime d = getSubscriberZonedDateTime();

		ApiActionProcess<List<ContServiceDataHWater>> action = () -> cwWidgetService.selectChartData(contZpointId, d,
				mode.toUpperCase());

		return responseOK(action);
	}

}
