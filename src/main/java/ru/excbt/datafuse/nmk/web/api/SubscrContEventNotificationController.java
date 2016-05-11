package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventDeviation;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatus;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventNotificationStatus;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventTypeStatus;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.service.ContEventLevelColorService;
import ru.excbt.datafuse.nmk.data.service.ContEventMonitorService;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService.SearchConditions;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с уведомлениями абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
@Controller
@RequestMapping("/api/subscr/contEvent")
public class SubscrContEventNotificationController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationController.class);

	private final static Pageable PAGE_LIMIT_1 = new PageRequest(0, 1);

	@Autowired
	private SubscrContEventNotificationService subscrContEventNotifiicationService;

	@Autowired
	private ContEventMonitorService contEventMonitorService;

	@Autowired
	private ContEventLevelColorService contEventLevelColorService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	@Autowired
	private ContEventService contEventService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/notifications/all", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationsAll() {

		Page<SubscrContEventNotification> resultPage = subscrContEventNotifiicationService
				.selectAll(getCurrentSubscriberId(), null, null);

		return ResponseEntity.ok(new PageInfoList<SubscrContEventNotification>(resultPage));

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
	public ResponseEntity<?> notificationsGetPaged(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "contEventTypeIds", required = false) Long[] contEventTypeIds,
			@RequestParam(value = "contEventCategories", required = false) String[] contEventCategories,
			@RequestParam(value = "contEventDeviations", required = false) String[] contEventDeviations,
			@RequestParam(value = "isNew", required = false) Boolean isNew,
			@RequestParam(value = "sortDesc", required = false, defaultValue = "true") Boolean sortDesc,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		List<Long> contObjectIdList = contObjectIds == null ? null : Arrays.asList(contObjectIds);
		List<Long> contEventTypeList = contEventTypeIds == null ? null : Arrays.asList(contEventTypeIds);
		List<String> contEventCategoryList = contEventCategories == null ? null : Arrays.asList(contEventCategories);

		final List<Long> contEventTypeIdPairList = contEventTypeList == null ? null
				: contEventTypeService.selectContEventTypesPaired(contEventTypeList);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && !datePeriodParser.getLocalDatePeriod().isValidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		Pageable pageRequest = SubscrContEventNotificationService.setupPageRequestSort(pageable, sortDesc);

		LocalDatePeriod requestDatePeriod = LocalDatePeriod.emptyPeriod();

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isValidEq()) {

			requestDatePeriod = datePeriodParser.getLocalDatePeriod().buildEndOfDay();

		}

		SearchConditions searchConditions = new SearchConditions(getCurrentSubscriberId(), requestDatePeriod, isNew);
		searchConditions.initContObjectIds(contObjectIdList);
		searchConditions.initContEventTypes(contEventTypeIdPairList);
		searchConditions.initContEventCategories(contEventCategoryList);
		searchConditions.initContEventDeviations(contEventDeviations);
		// TODO query upgrade
		Page<SubscrContEventNotification> resultPage = subscrContEventNotifiicationService
				.selectNotificationByConditions(searchConditions, pageRequest);

		return ResponseEntity.ok(new PageInfoList<SubscrContEventNotification>(resultPage));

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/notifications/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationGetOne(@PathVariable("id") Long id) {

		checkNotNull(id);

		SubscrContEventNotification result = subscrContEventNotifiicationService.findOneNotification(id);

		if (result == null) {

			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body(ApiResult.build(ApiResultCode.ERR_UNPROCESSABLE_TRANSACTION));
		}

		return ResponseEntity.ok(result);

	}

	/**
	 * 
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsUpdateRevisionFalse(
			@RequestParam(value = "notificationIds", required = true) Long[] notificationIds,
			@RequestParam(value = "isNew", required = false, defaultValue = "false") Boolean isNew) {

		checkNotNull(isNew);

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				subscrContEventNotifiicationService.updateNotificationIsNew(isNew, Arrays.asList(notificationIds),
						currentSubscriberService.getCurrentUserId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision/isNew", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsUpdateRevisionTrue(
			@RequestParam(value = "notificationIds", required = true) Long[] notificationIds,
			@RequestParam(value = "isNew", required = false, defaultValue = "true") Boolean isNew) {

		checkNotNull(isNew);

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				subscrContEventNotifiicationService.updateNotificationIsNew(isNew, Arrays.asList(notificationIds),
						currentSubscriberService.getCurrentUserId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision/all", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsUpdateRevisionAll(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "contEventTypeIds", required = false) Long[] contEventTypeIds,
			@RequestParam(value = "isNew", required = false) Boolean isNew,
			@RequestParam(value = "revisionIsNew", required = true) Boolean revisionIsNew) {

		List<Long> contObjectList = contObjectIds == null ? null : Arrays.asList(contObjectIds);
		List<Long> contEventTypeList = contEventTypeIds == null ? null : Arrays.asList(contEventTypeIds);

		final List<Long> contEventTypeIdPairList = contEventTypeList == null ? null
				: contEventTypeService.selectContEventTypesPaired(contEventTypeList);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		LocalDatePeriod requestDatePeriod = LocalDatePeriod.emptyPeriod();

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isValidEq()) {

			requestDatePeriod = datePeriodParser.getLocalDatePeriod().buildEndOfDay();

		}

		final LocalDatePeriod actionDP = requestDatePeriod;

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				subscrContEventNotifiicationService.updateRevisionByConditions(getCurrentSubscriberId(), actionDP,
						contObjectList, contEventTypeIdPairList, isNew, revisionIsNew,
						currentSubscriberService.getCurrentUserId());
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
	//@RequestMapping(value = "/notifications/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	protected ResponseEntity<?> notificationsContObjects(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<MonitorContEventNotificationStatus> preResultList = subscrContEventNotifiicationService
				.selectMonitorContEventNotificationStatus(getCurrentSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		List<MonitorContEventNotificationStatus> resultList = null;

		if (Boolean.TRUE.equals(noGreenColor)) {
			resultList = preResultList.stream()
					.filter((n) -> n.getContEventLevelColorKey() != ContEventLevelColorKey.GREEN)
					.collect(Collectors.toList());
		} else {
			resultList = preResultList;
		}

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	//@RequestMapping(value = "/notifications/contObject/statusCollapse", method = RequestMethod.GET,
	//		produces = APPLICATION_JSON_UTF8)
	protected ResponseEntity<?> notificationsContObjectsStatusCollapse(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(getSubscriberParam());

		List<MonitorContEventNotificationStatus> resultList = subscrContEventNotifiicationService
				.selectMonitorContEventNotificationStatusCollapse(getSubscriberParam(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/cityStatusCollapse", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsCityContObjectsStatusCollapse(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor) {

		// TODO contGroupId upgrade

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<CityMonitorContEventsStatus> result = subscrContEventNotifiicationService
				.selectMonitoryContObjectCityStatus(getSubscriberParam(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	//@RequestMapping(value = "/notifications/contObject/{contObjectId}/eventTypes", method = RequestMethod.GET,
	//		produces = APPLICATION_JSON_UTF8)
	protected ResponseEntity<?> notificationsContObjectEventTypes(
			@PathVariable(value = "contObjectId") Long contObjectId,
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr) {

		checkNotNull(contObjectId);
		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<MonitorContEventTypeStatus> resultList = subscrContEventNotifiicationService
				.selectMonitorContEventTypeStatus(getCurrentSubscriberId(), contObjectId,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/{contObjectId}/eventTypes/statusCollapse",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsContObjectEventTypesStatusCollapse(
			@PathVariable(value = "contObjectId") Long contObjectId,
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr) {

		checkNotNull(contObjectId);
		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<MonitorContEventTypeStatus> resultList = subscrContEventNotifiicationService
				.selectMonitorContEventTypeStatusCollapse(getSubscriberParam(), contObjectId,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/{contObjectId}/monitorEvents", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsContObjectMonitorEvents(
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		List<ContEventMonitor> resultList = contEventMonitorService.selectByContObject(contObjectId);

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/notifications/monitorColor", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsMonitorColor(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr) {

		ContEventLevelColor monitorColor = contEventMonitorService.getColorBySubscriberId(getCurrentSubscriberId());

		if (monitorColor == null) {

			monitorColor = contEventLevelColorService.getEventColorCached(ContEventLevelColorKey.GREEN);

			LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

			if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isValidEq()) {

				SearchConditions searchConditions = new SearchConditions(getCurrentSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

				Page<SubscrContEventNotification> pageResult = subscrContEventNotifiicationService
						.selectNotificationByConditions(searchConditions, PAGE_LIMIT_1);

				if (pageResult.getTotalElements() > 0) {
					monitorColor = contEventLevelColorService.getEventColorCached(ContEventLevelColorKey.YELLOW);
				}
			}

		}

		return ResponseEntity.ok(monitorColor);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventCategory() {
		List<ContEventCategory> xList = contEventService.selectContEventCategoryList();
		return responseOK(ObjectFilters.deletedFilter(xList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deviations", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventDeviation() {
		List<ContEventDeviation> resultList = contEventService.findContEventDeviation();
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
