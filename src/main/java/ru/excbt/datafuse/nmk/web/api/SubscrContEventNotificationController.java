package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.model.support.SubscrContEventNotificationsStatus;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotifiicationService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

@Controller
@RequestMapping("/api/subscr/contEvent")
public class SubscrContEventNotificationController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContEventNotificationController.class);

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private CurrentUserService currentUserService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/notifications/all", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationsAll() {

		Page<SubscrContEventNotification> resultPage = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), null,
						null);

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
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "contEventTypeIds", required = false) Long[] contEventTypeIds,
			@RequestParam(value = "isNew", required = false) Boolean isNew,
			@RequestParam(value = "sortDesc", required = false, defaultValue = "true") Boolean sortDesc,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		List<Long> contObjectList = contObjectIds == null ? null : Arrays
				.asList(contObjectIds);
		List<Long> contEventTypeList = contEventTypeIds == null ? null : Arrays
				.asList(contEventTypeIds);

		DatePeriodParser datePeriodParser = DatePeriodParser.parse(fromDateStr,
				toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk()
				&& !datePeriodParser.getDatePeriod().isValidEq()) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}",
									fromDateStr, toDateStr));
		}

		Pageable pageRequest = SubscrContEventNotifiicationService
				.setupPageRequestSort(pageable, sortDesc);

		Page<SubscrContEventNotification> resultPage = null;

		logger.debug("date isOK condition");

		if (datePeriodParser.isOk()) {
			DateTime endOfDay = null;

			endOfDay = DatePeriodParser.endOfDay(datePeriodParser
					.getDatePeriod().getDateTimeTo());

			DatePeriod dp = DatePeriod
					.builder(datePeriodParser.getDatePeriod()).dateTo(endOfDay)
					.build();

			resultPage = subscrContEventNotifiicationService
					.selectByConditions(
							currentSubscriberService.getSubscriberId(), dp,
							contObjectList, contEventTypeList, isNew,
							pageRequest);

		} else {
			logger.debug("date isOK condition");
			resultPage = subscrContEventNotifiicationService
					.selectByConditions(
							currentSubscriberService.getSubscriberId(),
							DatePeriod.emptyPeriod(), contObjectList,
							contEventTypeList, isNew, pageRequest);

		}

		return ResponseEntity.ok(new PageInfoList<SubscrContEventNotification>(
				resultPage));

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/notifications/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationOne(
			@PathVariable("id") Long id) {

		checkNotNull(id);

		SubscrContEventNotification result = subscrContEventNotifiicationService
				.findOne(id);

		if (result == null) {

			return ResponseEntity
					.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body(ApiResult
							.build(ApiResultCode.ERR_UNPROCESSABLE_TRANSACTION));
		}

		return ResponseEntity.ok(result);

	}

	/**
	 * 
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationUpdateIsNewFalse(
			@RequestParam(value = "contObjectIds", required = true) Long[] notificationIds) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				subscrContEventNotifiicationService.updateIsNew(Boolean.FALSE,
						Arrays.asList(notificationIds),
						currentUserService.getCurrentUserId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision/isNew", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationUpdateIsNewTrue(
			@RequestParam(value = "contObjectIds", required = true) Long[] notificationIds) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				subscrContEventNotifiicationService.updateIsNew(Boolean.TRUE,
						Arrays.asList(notificationIds),
						currentUserService.getCurrentUserId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contObjectEventNotificationStatus(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr) {

		DatePeriodParser datePeriodParser = DatePeriodParser.parse(fromDateStr,
				toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk()
				&& !datePeriodParser.getDatePeriod().isValidEq()) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}",
									fromDateStr, toDateStr));
		}

		List<SubscrContEventNotificationsStatus> resultList = subscrContEventNotifiicationService
				.selectSubscrEventNotificationsStatus(
						currentSubscriberService.getSubscriberId(),
						datePeriodParser.getDatePeriod());

		return ResponseEntity.ok(resultList);
	}

}
