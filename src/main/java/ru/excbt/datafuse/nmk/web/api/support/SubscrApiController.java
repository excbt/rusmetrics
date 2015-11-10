package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
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

	@Autowired
	protected SubscrContObjectService subscrContObjectService;

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

		Long[] contObjectIds = new Long[] { contObjectId };
		return canAccessContObject(contObjectIds);
	}

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	protected boolean canAccessContObject(Long[] contObjectIds) {
		if (contObjectIds == null || contObjectIds.length == 0) {
			return false;
		}

		if (currentUserService.isSystem()) {
			return true;
		}

		List<Long> subscrContObjectIds = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());

		boolean result = true;
		for (Long id : contObjectIds) {
			result = result && subscrContObjectIds.contains(id);
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	protected long getCurrentSubscriberId() {
		return currentSubscriberService.getSubscriberId();
	}

	/**
	 * 
	 * @return
	 */
	protected LocalDate getCurrentSubscriberLocalDate() {
		Date d = subscriberService.getSubscriberCurrentTime(getCurrentSubscriberId());
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

		boolean isRma = currentSubscriberService.isRma();

		return filterObjectAccess(objectList, getCurrentSubscriberId(), isRma);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param objectList
	 * @return
	 */
	protected <T> List<T> filterObjectAccess(List<T> objectList, Long subscriberId, Boolean isRma) {
		checkNotNull(objectList);

		List<T> resultObjects = subscrServiceAccessService.filterObjectAccess(objectList, subscriberId, isRma,
				getSubscriberLocalDate(subscriberId));

		return resultObjects;
	}

	/**
	 * 
	 * @return
	 */
	protected boolean isSystemUser() {
		return currentUserService.isSystem();
	}

}
