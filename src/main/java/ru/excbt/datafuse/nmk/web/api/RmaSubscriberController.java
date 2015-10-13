package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.Subscriber;

@Controller
@RequestMapping("/api/rma")
public class RmaSubscriberController extends SubscriberController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberController.class);

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET)
	public ResponseEntity<?> getRmaSubscribers() {
		if (!currentSubscriberService.isRma()) {
			logger.warn("Current User is not RMA");
			return responseForbidden();
		}
		List<Subscriber> resultList = subscriberService.selectRmaSubscribers(getSubscriberId());
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.GET)
	public ResponseEntity<?> getRmaSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId) {
		if (!currentSubscriberService.isRma()) {
			logger.warn("Current User is not RMA");
			return responseForbidden();
		}

		Subscriber subscriber = subscriberService.findOne(rSubscriberId);

		if (subscriber.getRmaSubscriberId() == null || !subscriber.getRmaSubscriberId().equals(getSubscriberId())) {
			return responseForbidden();
		}
		return responseOK(subscriber);
	}

}
