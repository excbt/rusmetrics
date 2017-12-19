package ru.excbt.datafuse.nmk.web.rest.support;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * Базовый класс для контроллера с абонентом
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public abstract class AbstractSubscrApiResource {

	private static final Logger log = LoggerFactory.getLogger(AbstractSubscrApiResource.class);

	@Autowired
	protected SubscriberService subscriberService;

	@Autowired
	protected CurrentSubscriberService currentSubscriberService;

	@Autowired
	protected SubscrServiceAccessService subscrServiceAccessService;

    @Autowired
	private ObjectAccessService objectAccessService;

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	protected boolean canAccessContObject(Long contObjectId) {
        Objects.requireNonNull(contObjectId);
		return objectAccessService.checkContObjectId(contObjectId, getSubscriberParam());
	}

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	protected boolean canAccessContObject(Long[] contObjectIds) {
        Objects.requireNonNull(contObjectIds);
	    return objectAccessService.checkContObjectIds(Arrays.asList(contObjectIds), currentSubscriberService.getSubscriber());
	}

    /**
     *
     * @param contObjectIds
     * @return
     */
	protected boolean canAccessContObject(List<Long> contObjectIds) {
        return objectAccessService.checkContObjectIds(contObjectIds, currentSubscriberService.getSubscriber());
	}

	/**
	 *
	 * @param contZPointIds
	 * @return
	 */
	protected boolean canAccessContZPoint(Long[] contZPointIds) {
		if (currentSubscriberService.isSystemUser()) {
			return true;
		}
		return objectAccessService.checkContZPointIds(Arrays.asList(contZPointIds), currentSubscriberService.getSubscriberParam());
	}

    /**
     *
     * @param contZPointId
     * @return
     */
	protected boolean canAccessContZPoint(Long contZPointId) {
        Objects.requireNonNull(contZPointId);
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
	protected SubscriberParam getSubscriberParam() {
		return currentSubscriberService.getSubscriberParam();
	}

	/**
	 *
	 * @return
	 */
	protected Long getCurrentSubscUserId() {
		return currentSubscriberService.getCurrentUserId();
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
	 * @return
	 */
	protected ZonedDateTime getSubscriberZonedDateTime() {
		Date d = subscriberService.getSubscriberCurrentTime(getCurrentSubscriberId());
		return d != null ? ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()) : ZonedDateTime.now();
	}

	protected ZonedDateTime getSubscriberZonedDateTime2() {
		Date d = subscriberService.getSubscriberCurrentTime(getCurrentSubscriberId());
        Long duration = 0L;
		if (d != null) {
            LocalDateTime sd = LocalDateUtils.asLocalDateTime(d);
            duration = Duration.between(sd, LocalDateTime.now()).toNanos();
        }
		return d != null ? ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).plusNanos(duration) : ZonedDateTime.now();
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
		SubscriberUserDetails sud = currentSubscriberService.getCurrentUserDetails();
		if (sud != null && sud.getSkipServiceFilter()) {
			return new ArrayList<>(objectList);
		}

		boolean isRma = currentSubscriberService.isRma();

		return filterObjectAccess(objectList, getSubscriberParam());
	}

    /**
     *
     * @param objectList
     * @param subscriberParam
     * @param <T>
     * @return
     */
	protected <T> List<T> filterObjectAccess(List<T> objectList, SubscriberParam subscriberParam) {
        Objects.requireNonNull(objectList);

		List<T> resultObjects = subscrServiceAccessService.filterObjectAccess(objectList, subscriberParam,
				getSubscriberLocalDate(subscriberParam.getSubscriberId()));

		return resultObjects;
	}

	/**
	 *
	 * @return
	 */
	protected boolean isSystemUser() {
		return currentSubscriberService.isSystemUser();
	}

}
