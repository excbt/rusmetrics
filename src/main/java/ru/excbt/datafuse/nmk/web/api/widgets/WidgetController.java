/**
 *
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 *
 */
@RestController
@RequestMapping("/{contZpointId}")
public class WidgetController  {

	private final ContEventMonitorV3Service contEventMonitorV3Service;

	protected final ContZPointService contZPointService;

	protected final ObjectAccessService objectAccessService;

    protected final PortalUserIdsService portalUserIdsService;


    protected final SubscriberTimeService subscriberTimeService;

	@Autowired
    public WidgetController(ContEventMonitorV3Service contEventMonitorV3Service, ContZPointService contZPointService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService, SubscriberTimeService subscriberTimeService) {
        this.contEventMonitorV3Service = contEventMonitorV3Service;
        this.contZPointService = contZPointService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
        this.subscriberTimeService = subscriberTimeService;
    }

    /**
	 *
	 * @param contZpointId
	 * @return
	 */
	@RequestMapping(value = "/monitor", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZpointMonitor(
			@PathVariable(value = "contZpointId", required = true) Long contZpointId) {

		if (!objectAccessService.checkContZPointId(contZpointId, portalUserIdsService.getCurrentIds())) {
			ApiResponse.responseForbidden();
		}

		Long contObjectId = contZPointService.selectContObjectId(contZpointId);

		if (contObjectId == null) {
			return null;
		}

		Map<String, Object> result = new HashMap<>();
		result.put("color", getMonitorColorValue(contObjectId, contZpointId).getKeyname());

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	protected ContEventLevelColorKeyV2 getMonitorColorValue(Long contObjectId, Long contZPointId) {

		if (contObjectId == null || contZPointId == null) {
			return null;
		}

        ContEventLevelColorKeyV2 worseMonitorColorKey = contEventMonitorV3Service.findMonitorColor(contObjectId, contZPointId);

		final ContEventLevelColorKeyV2 resultColorKey = worseMonitorColorKey != null ? worseMonitorColorKey
				: ContEventLevelColorKeyV2.GREEN;
		return resultColorKey;
	}

}
