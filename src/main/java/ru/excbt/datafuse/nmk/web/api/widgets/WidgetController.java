/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.service.ContEventMonitorV2Service;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 * 
 */
public class WidgetController extends SubscrApiController {

	@Autowired
	private ContEventMonitorV2Service contEventMonitorV2Service;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
	@RequestMapping(value = "/monitor/{contZpointId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZpointMonitor(
			@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

		if (!canAccessContZPoint(contZpointId)) {
			responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		List<ContEventMonitorV2> mon = contEventMonitorV2Service.selectByContZpoint(contObjectId, contZpointId);

		ContEventLevelColorV2 worseMonitorColor = contEventMonitorV2Service.sortWorseColor(mon);
		ContEventLevelColorKeyV2 worseMonitorColorKey = ContEventLevelColorKeyV2.findByKeyname(worseMonitorColor);

		final ContEventLevelColorKeyV2 resultColorKey = worseMonitorColorKey != null ? worseMonitorColorKey
				: ContEventLevelColorKeyV2.GREEN;

		Map<String, String> result = new HashMap<>();
		result.put("color", resultColorKey.getKeyname());

		return responseOK(result);
	}

}
