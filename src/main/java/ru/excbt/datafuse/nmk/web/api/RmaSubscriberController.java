package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;

/**
 * Контроллер для работы абонентами для РМА
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 *
 */
@Controller
@RequestMapping("/api/rma")
public class RmaSubscriberController extends SubscriberController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberController.class);

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET)
	public ResponseEntity<?> getRmaSubscribers() {
		if (!currentSubscriberService.isRma()) {
			return responseForbidden();
		}

		List<Subscriber> subscriberList = rmaSubscriberService.selectRmaSubscribers(getCurrentSubscriberId());
		List<Subscriber> resultList = ObjectFilters.deletedFilter(subscriberList);

		return responseOK(subscriberService.enhanceSubscriber(resultList));
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

		Subscriber subscriber = subscriberService.selectSubscriber(rSubscriberId);

		if (subscriber.getRmaSubscriberId() == null
				|| !subscriber.getRmaSubscriberId().equals(getCurrentSubscriberId())) {
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
		checkNotNull(rSubscriber.getOrganizationId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<Subscriber, Long>(rSubscriber, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public Subscriber processAndReturnResult() {
				return rmaSubscriberService.createRmaSubscriber(entity, getCurrentSubscriberId());
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
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestBody Subscriber rSubscriber) {

		checkNotNull(rSubscriberId);
		checkNotNull(rSubscriber);
		checkNotNull(rSubscriber.getOrganizationId());

		ApiAction action = new ApiActionEntityAdapter<Subscriber>(rSubscriber) {

			@Override
			public Subscriber processAndReturnResult() {
				return rmaSubscriberService.updateRmaSubscriber(rSubscriber, getCurrentSubscriberId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		checkNotNull(rSubscriberId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				if (Boolean.TRUE.equals(isPermanent)) {
					rmaSubscriberService.deleteRmaSubscriberPermanent(rSubscriberId, getCurrentSubscriberId());
				} else {
					rmaSubscriberService.deleteRmaSubscriber(rSubscriberId, getCurrentSubscriberId());
				}

			}

		};
		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscribers/organizations", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOrganizations() {
		List<Organization> organizations = organizationService.selectOrganizations(getSubscriberParam());
		return responseOK(organizations);
	}

}
