package ru.excbt.datafuse.nmk.web.api;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrServicePackService;
import ru.excbt.datafuse.nmk.data.service.SubscrServicePriceService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscServiceManageController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscServiceManageController.class);

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePriceService subscrServicePriceService;

	@Autowired
	private SubscrServiceAccessService subscriberAccessService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/manage/service/servicePackList", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePacks() {
		List<SubscrServicePack> result = subscrServicePackService.selectServicePackList();
		return responseOK(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/manage/service/serviceItemList", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceItems() {
		List<SubscrServiceItem> result = subscrServiceItemService.selectServiceItemList();
		return responseOK(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/manage/service/servicePriceList", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePrices() {
		List<SubscrServicePrice> result = subscrServicePriceService.selectPriceByDate(LocalDate.now());
		return responseOK(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/access", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscriberServiceAccess(@PathVariable("subscriberId") Long subscriberId) {
		return responseOK(subscriberServiceAccessList(subscriberId));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/manage/service/access", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentServiceAccess() {
		return responseOK(subscriberServiceAccessList(getSubscriberId()));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	private List<SubscrServiceAccess> subscriberServiceAccessList(Long subscriberId) {
		LocalDate accessDate = new LocalDate(subscriberService.getSubscriberCurrentTime(subscriberId));
		List<SubscrServiceAccess> result = subscriberAccessService.selectSubscriberServiceAccess(subscriberId,
				accessDate);

		result.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});
		return result;

	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscriberAccessList
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/access", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscriberServiceAccess(@PathVariable("subscriberId") Long subscriberId,
			@RequestBody final List<SubscrServiceAccess> subscriberAccessList) {

		ApiAction action = new AbstractEntityApiAction<List<SubscrServiceAccess>>() {

			@Override
			public void process() {
				setResultEntity(
						subscriberAccessService.processAccessList(subscriberId, LocalDate.now(), subscriberAccessList));

			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscriberAccessList
	 * @return
	 */
	@RequestMapping(value = "/manage/service/access", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateCurrentServiceAccess(
			@RequestBody final List<SubscrServiceAccess> subscriberAccessList) {

		ApiAction action = new AbstractEntityApiAction<List<SubscrServiceAccess>>() {

			@Override
			public void process() {
				setResultEntity(subscriberAccessService.processAccessList(getSubscriberId(), LocalDate.now(),
						subscriberAccessList));

			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/manage/service/permissions", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentServicePermissions() {
		List<SubscrServicePermission> permissions = subscrServiceAccessService
				.selectSubscriberPermissions(getSubscriberId(), getSubscriberLocalDate());
		return responseOK(permissions);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/permissions", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscriberServicePermissions(@PathVariable("subscriberId") Long subscriberId) {
		List<SubscrServicePermission> permissions = subscrServiceAccessService.selectSubscriberPermissions(subscriberId,
				getSubscriberLocalDate(subscriberId));
		List<SubscrServicePermission> result = permissions.stream().filter((i) -> Boolean.TRUE.equals(i.getIsFront()))
				.collect(Collectors.toList());
		return responseOK(result);
	}

}
