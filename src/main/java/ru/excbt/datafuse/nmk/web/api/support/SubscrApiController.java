package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

public class SubscrApiController extends WebApiController {

	@Autowired
	protected SubscriberService subscriberService;

	@Autowired
	protected CurrentSubscriberService currentSubscriberService;

	@Autowired
	protected SubscrServiceAccessService subscrServiceAccessService;

	@Autowired
	protected CurrentUserService currentUserService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	protected boolean canAccessContObject(Long contObjectId) {
		if (contObjectId == null) {
			return false;
		}

		if (currentUserService.isSystem()) {
			return true;
		}

		List<Long> contObjectIds = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());
		return contObjectIds.contains(contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	protected long getSubscriberId() {
		return currentSubscriberService.getSubscriberId();
	}

	/**
	 * 
	 * @return
	 */
	protected LocalDate getSubscriberLocalDate() {
		Date d = subscriberService.getSubscriberCurrentTime(getSubscriberId());
		return new LocalDate(d);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	protected LocalDate getSubscriberLocalDate(Long subscriberId) {
		Date d = subscriberService.getSubscriberCurrentTime(subscriberId);
		return new LocalDate(d);
	}

	/**
	 * 
	 * @param objectList
	 * @return
	 */
	protected <T> List<T> filterObjectAccess(List<T> objectList) {
		SubscriberUserDetails sud = currentUserService.getCurrentUserDetails();
		if (sud != null && sud.getSkipServiceFilter()) {
			return new ArrayList<>(objectList);
		}
		return filterObjectAccess(objectList, getSubscriberId());
	}

	/**
	 * 
	 * @param subscriberId
	 * @param objectList
	 * @return
	 */
	protected <T> List<T> filterObjectAccess(List<T> objectList, Long subscriberId) {
		checkNotNull(objectList);

		List<T> resultObjects = subscrServiceAccessService.filterObjectAccess(objectList, subscriberId,
				getSubscriberLocalDate(subscriberId));

		return resultObjects;
	}

}
