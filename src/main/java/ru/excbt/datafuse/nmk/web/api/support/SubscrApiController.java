package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

/**
 * Базовый класс для контроллера с абонентом
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
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
	 * @param checkIds
	 * @param availableIds
	 * @return
	 */
	protected boolean checkIds(Long[] checkIds, List<Long> availableIds) {

		if (availableIds == null || availableIds.isEmpty()) {
			return false;
		}

		boolean result = true;
		for (Long id : checkIds) {
			result = result && availableIds.contains(id);
		}
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	protected boolean canAccessContObject(Long contObjectId) {
		checkNotNull(contObjectId);
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

		return checkIds(contObjectIds, subscrContObjectIds);
	}

	/**
	 * 
	 * @param contZPointIds
	 * @return
	 */
	protected boolean canAccessContZPoint(Long[] contZPointIds) {
		if (contZPointIds == null || contZPointIds.length == 0) {
			return false;
		}

		if (currentUserService.isSystem()) {
			return true;
		}

		List<Long> availableIds = subscrContObjectService.selectSubscriberContZPointIds(getCurrentSubscriberId());

		return checkIds(contZPointIds, availableIds);
	}

	/**
	 * 
	 * @param contZPointIds
	 * @return
	 */
	protected boolean canAccessContZPoint(Long contZPointId) {
		checkNotNull(contZPointId);
		Long[] contObjectIds = new Long[] { contZPointId };
		return canAccessContZPoint(contObjectIds);
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
	protected long getSubscriberId() {
		return currentSubscriberService.getSubscriberId();
	}

	/**
	 * 
	 * @return
	 */
	protected Subscriber getCurrentSubscriber() {
		return currentSubscriberService.getSubscriber();
	}

	/**
	 * 
	 * @return
	 */
	protected Long getRmaSubscriberId() {
		Subscriber subscriber = currentSubscriberService.getSubscriber();
		if (subscriber == null) {
			return null;
		}
		if (Boolean.TRUE.equals(subscriber.getIsRma())) {
			return subscriber.getId();
		}
		return subscriber.getRmaSubscriberId();
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
