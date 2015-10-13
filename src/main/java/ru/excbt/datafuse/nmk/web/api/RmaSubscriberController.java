package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

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

		List<Subscriber> subscriberList = subscriberService.selectRmaSubscribers(getSubscriberId());
		List<Subscriber> resultList = currentUserService.isSystem() ? subscriberList
				: ObjectFilters.deletedFilter(subscriberList);
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

	/**
	 * 
	 * @param rSubscriber
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscriber(@RequestBody Subscriber rSubscriber, HttpServletRequest request) {

		checkNotNull(rSubscriber);
		checkNotNull(rSubscriber.getOrganiazationId());

		ApiActionLocation action = new EntityApiActionLocationAdapter<Subscriber, Long>(rSubscriber, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public Subscriber processAndReturnResult() {
				return subscriberService.createRmaSubscriber(entity, getSubscriberId());
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @param rSubscriber
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestBody Subscriber rSubscriber) {

		checkNotNull(rSubscriberId);
		checkNotNull(rSubscriber);
		checkNotNull(rSubscriber.getOrganiazationId());

		ApiAction action = new EntityApiActionAdapter<Subscriber>(rSubscriber) {

			@Override
			public Subscriber processAndReturnResult() {
				return subscriberService.updateRmaSubscriber(rSubscriber, getSubscriberId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId) {

		checkNotNull(rSubscriberId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscriberService.deleteRmaSubscriber(rSubscriberId, getSubscriberId());

			}

		};
		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
