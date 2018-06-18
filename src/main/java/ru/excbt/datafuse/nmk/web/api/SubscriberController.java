package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

/**
 * Контроллер для работы с абонентами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.10.2015
 *
 */
@RestController
@RequestMapping("/api/subscr")
public class SubscriberController {

    private final ObjectAccessService objectAccessService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscriberController(ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/info/subscriberContObjectCount", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberContObjectCount() {
		int cnt = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId()).size();
		return ResponseEntity.ok(cnt);
	}

}
