package ru.excbt.datafuse.nmk.web.api;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.utils.ApiJodaDateFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Контроллер для работы с контейнерами учета для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.02.2015
 *
 */
@RestController
@RequestMapping(value = "/api/subscr")
public class SubscrContEventController  {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventController.class);

	private final ContEventService contEventService;

	private final PortalUserIdsService portalUserIdsService;

    public SubscrContEventController(ContEventService contEventService, PortalUserIdsService portalUserIdsService) {
        this.contEventService = contEventService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/events", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("contObjectId") long contObjectId) {
		List<ContEvent> result = contEventService.findEventsByContObjectId(contObjectId);
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/events", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll() {
		Page<ContEvent> result = contEventService.selectEventsBySubscriber(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ResponseEntity.ok(Lists.newArrayList(result.iterator()));
	}

    /**
     *
     * @param startDateStr
     * @param endDateStr
     * @param contObjectIds
     * @return
     */
	@RequestMapping(value = "/contObjects/eventsFilter", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> eventsFilter(@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds) {

		List<Long> contObjectList = contObjectIds != null ? Arrays.asList(contObjectIds) : new ArrayList<Long>();

		if (startDateStr != null && endDateStr != null) {
			DateTime startD = null;
			DateTime endD = null;
			try {
				startD = ApiJodaDateFormatter.DATE_FORMATTER.parseDateTime(startDateStr);
				endD = ApiJodaDateFormatter.DATE_FORMATTER.parseDateTime(endDateStr);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(
						String.format("Invalid parameters startDateStr:{}, endDateStr:{}", startDateStr, endDateStr));
			}

			if (startD.compareTo(endD) > 0) {
				return ResponseEntity.badRequest()
						.body(String.format("startDateStr:%s is bigger than endDateStr:%s", startDateStr, endDateStr));
			}

			DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
					.withMillisOfSecond(999);

			Page<ContEvent> resultPage = contEventService.selectBySubscriberAndDateAndContObjectIds(
                portalUserIdsService.getCurrentIds().getSubscriberId(), startD, endOfDay, contObjectList);

			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		if (contObjectList != null && contObjectList.size() > 0) {
			Page<ContEvent> resultPage = contEventService.selectBySubscriberAndContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId(),
					contObjectList);
			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		Page<ContEvent> resultPage = contEventService.selectEventsBySubscriber(portalUserIdsService.getCurrentIds().getSubscriberId());

		return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));
	}

    /**
     *
     * @param startDateStr
     * @param endDateStr
     * @param contObjectIds
     * @param pageable
     * @return
     */
	@RequestMapping(value = "/contObjects/eventsFilterPaged", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> eventsFilterPaged(@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@PageableDefault(size = ApiConst.DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		List<Long> contObjectList = contObjectIds != null ? Arrays.asList(contObjectIds) : new ArrayList<Long>();

		if (startDateStr != null && endDateStr != null) {
			DateTime startD = null;
			DateTime endD = null;
			try {
				startD = ApiJodaDateFormatter.DATE_FORMATTER.parseDateTime(startDateStr);
				endD = ApiJodaDateFormatter.DATE_FORMATTER.parseDateTime(endDateStr);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(
						String.format("Invalid parameters startDateStr:{}, endDateStr:{}", startDateStr, endDateStr));
			}

			if (startD.compareTo(endD) > 0) {
				return ResponseEntity.badRequest()
						.body(String.format("startDateStr:%s is bigger than endDateStr:%s", startDateStr, endDateStr));
			}

			DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
					.withMillisOfSecond(999);

			Page<ContEvent> resultPage = contEventService.selectBySubscriberAndDateAndContObjectIds(
                portalUserIdsService.getCurrentIds().getSubscriberId(), startD, endOfDay, contObjectList, pageable);

			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		if (contObjectList != null && contObjectList.size() > 0) {
			Page<ContEvent> resultPage = contEventService.selectBySubscriberAndContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId(),
					contObjectList, pageable);
			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		Page<ContEvent> resultPage = contEventService.selectEventsBySubscriber(portalUserIdsService.getCurrentIds().getSubscriberId(), pageable);

		return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

	}

}
