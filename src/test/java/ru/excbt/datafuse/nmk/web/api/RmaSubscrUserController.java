package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

@Controller
@RequestMapping("/api/rma")
public class RmaSubscrUserController extends SubscrUserController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrUserController.class);

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/{rSubscriberId}/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscrUsers(@PathVariable("rSubscriberId") Long rSubscriberId) {
		checkNotNull(rSubscriberId);

		if (!currentSubscriberService.isRma()) {
			responseForbidden();
		}

		Subscriber subscriber = subscriberService.findOne(rSubscriberId);
		if (subscriber == null || subscriber.getRmaSubscriberId() == null
				|| !subscriber.getRmaSubscriberId().equals(getSubscriberId())) {
			return responseBadRequest();
		}

		List<SubscrUser> subscrUsers = subscrUserService.findBySubscriberId(rSubscriberId);
		return responseOK(subscrUsers);
	}

}
