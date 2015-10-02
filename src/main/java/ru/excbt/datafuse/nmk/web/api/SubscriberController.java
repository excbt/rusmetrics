package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping("/api/subscr")
public class SubscriberController extends SubscrApiController {

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/info/subscriberContObjectCount", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberContObjectCount() {
		int cnt = subscriberService.selectSubscriberContObjectCount(getSubscriberId());
		return ResponseEntity.ok(cnt);
	}

}
