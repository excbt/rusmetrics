package ru.excbt.datafuse.nmk.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContEventController extends WebApiController {

	
	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContEventController.class);
	
	@Autowired
	private SubscriberService subscrUserService;

	@Autowired
	private ContEventService contEventService;

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/events", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(
			@PathVariable("contObjectId") long contObjectId) {
		List<ContEvent> result = contEventService
				.findEventsByContObjectId(contObjectId);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/events", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll() {
		Page<ContEvent> result = contEventService
				.selectEventsBySubscriber(currentSubscriberService
						.getSubscriberId());
		return ResponseEntity.ok(Lists.newArrayList(result.iterator()));
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(value = "/contObjects/eventsFilter", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> eventsFilter(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds) {

		List<Long> contObjectList = contObjectIds != null ? Arrays
				.asList(contObjectIds) : new ArrayList<Long>();

		if (startDateStr != null && endDateStr != null) {
			DateTime startD = null;
			DateTime endD = null;
			try {
				startD = DATE_FORMATTER.parseDateTime(startDateStr);
				endD = DATE_FORMATTER.parseDateTime(endDateStr);
			} catch (Exception e) {
				return ResponseEntity
						.badRequest()
						.body(String
								.format("Invalid parameters startDateStr:{}, endDateStr:{}",
										startDateStr, endDateStr));
			}

			if (startD.compareTo(endD) > 0) {
				return ResponseEntity.badRequest().body(String
								.format("startDateStr:%s is bigger than endDateStr:%s",
										startDateStr, endDateStr));
			}

			Page<ContEvent> resultPage = contEventService
					.selectBySubscriberAndDateAndContObjectIds(
							currentSubscriberService.getSubscriberId(), startD,
							endD, contObjectList);

			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		if (contObjectList != null && contObjectList.size() > 0) {
			Page<ContEvent> resultPage = contEventService
					.selectBySubscriberAndContObjectIds(
							currentSubscriberService.getSubscriberId(),
							contObjectList);
			return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));

		}

		Page<ContEvent> resultPage = contEventService
				.selectEventsBySubscriber(currentSubscriberService
						.getSubscriberId());

		return ResponseEntity.ok(new PageInfoList<ContEvent>(resultPage));
	}

	
	// @RequestMapping(value = "/persons", method = RequestMethod.GET)
	// HttpEntity<?> persons(Pageable pageable,
	// PagedResourcesAssembler assembler) {
	//
	// Page<ContEvent> result =
	// contEventRepository.selectBySubscriberId(currentSubscriberService
	// .getSubscriberId(), pageable);
	// //assembler;
	// assembler.toResource(result, null);
	// return null; // new ResponseEntity<>(assembler.toResources(result),
	// HttpStatus.OK);
	// }

}
