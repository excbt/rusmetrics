package ru.excbt.datafuse.nmk.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotifiicationService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping("/api/subscr/contEvent")
public class SubscrContEventNotificationController extends WebApiController {

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/notifications/all", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationsAll() {

		Page<SubscrContEventNotification> resultPage = subscrContEventNotifiicationService
				.selectAllNotifications(
						currentSubscriberService.getSubscriberId(), null, null);

		return ResponseEntity.ok(new PageInfoList<SubscrContEventNotification>(
				resultPage));

	}

	
	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @param contEventTypeIds
	 * @param contObjectIds
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/notifications/paged", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationPaged(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr,
			@RequestParam(value = "contEventTypeIds", required = false) Long[] contEventTypeIds,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		List<Long> contObjectList = contObjectIds != null ? Arrays
				.asList(contObjectIds) : new ArrayList<Long>();

		if (fromDateStr != null && toDateStr != null) {
			DateTime fromD = null;
			DateTime toD = null;
			try {
				fromD = DATE_FORMATTER.parseDateTime(fromDateStr);
				toD = DATE_FORMATTER.parseDateTime(toDateStr);
			} catch (Exception e) {
				return ResponseEntity
						.badRequest()
						.body(String
								.format("Invalid parameters fromDateStr:{}, toDateStr:{}",
										fromDateStr, toDateStr));
			}

			if (fromD.compareTo(toD) > 0) {
				return ResponseEntity.badRequest().body(
						String.format(
								"fromDateStr:%s is bigger than toDateStr:%s",
								fromDateStr, toDateStr));
			}

			DateTime endOfDay = toD.withHourOfDay(23).withMinuteOfHour(59)
					.withSecondOfMinute(59).withMillisOfSecond(999);

			// Page<ContEvent> resultPage = contEventService
			// .selectBySubscriberAndDateAndContObjectIds(
			// currentSubscriberService.getSubscriberId(), fromD,
			// endOfDay, contObjectList);

			return ResponseEntity.ok().build();

		}

		return ResponseEntity.badRequest().build();
	}

}
