package ru.excbt.datafuse.nmk.web.api;

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
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorX;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventMonitorXDTO;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContEventNotificationDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventDeviation;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService.SearchConditions;
import ru.excbt.datafuse.nmk.service.ContEventMonitorService;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContEventNotificationMapper;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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
public class SubscrContEventNotificationController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationController.class);

	private final static Pageable PAGE_LIMIT_1 = new PageRequest(0, 1);

	private final SubscrContEventNotificationService subscrContEventNotifiicationService;

	private final SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService;

	private final ContEventMonitorService contEventMonitorService;

	private final ContEventMonitorV3Service contEventMonitorV3Service;

	private final ContEventLevelColorService contEventLevelColorService;

	private final ContEventTypeService contEventTypeService;

	private final  ContEventService contEventService;

	private final SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service;

	private final ObjectAccessService objectAccessService;

	private final SubscrContEventNotificationMapper mapper;

	@Autowired
    public SubscrContEventNotificationController(SubscrContEventNotificationService subscrContEventNotifiicationService, SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService, ContEventMonitorService contEventMonitorService, ContEventMonitorV3Service contEventMonitorV3Service, ContEventLevelColorService contEventLevelColorService, ContEventTypeService contEventTypeService, ContEventService contEventService, SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service, ObjectAccessService objectAccessService, SubscrContEventNotificationMapper mapper) {
        this.subscrContEventNotifiicationService = subscrContEventNotifiicationService;
        this.subscrContEventNotifiicationStatusService = subscrContEventNotifiicationStatusService;
        this.contEventMonitorService = contEventMonitorService;
        this.contEventMonitorV3Service = contEventMonitorV3Service;
        this.contEventLevelColorService = contEventLevelColorService;
        this.contEventTypeService = contEventTypeService;
        this.contEventService = contEventService;
        this.subscrContEventNotifiicationStatusV2Service = subscrContEventNotifiicationStatusV2Service;
        this.objectAccessService = objectAccessService;
        this.mapper = mapper;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/notifications/all", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> contEventNotificationsAll() {

		Page<SubscrContEventNotificationDTO> resultPage = subscrContEventNotifiicationService
				.selectAll(getCurrentSubscriberId(), null, null).map(mapper::toDto);

		return ResponseEntity.ok(new PageInfoList<>(resultPage));

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
	@RequestMapping(value = "/notifications/paged", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsGetPaged(
			@RequestParam(value = "fromDate", required = false) String fromDateStr,
			@RequestParam(value = "toDate", required = false) String toDateStr,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "contEventTypeIds", required = false) Long[] contEventTypeIds,
			@RequestParam(value = "contEventCategories", required = false) String[] contEventCategories,
			@RequestParam(value = "contEventDeviations", required = false) String[] contEventDeviations,
			@RequestParam(value = "isNew", required = false) Boolean isNew,
			@RequestParam(value = "sortDesc", required = false, defaultValue = "true") Boolean sortDesc,
			@PageableDefault(size = ApiConst.DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

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
		Page<SubscrContEventNotificationDTO> resultPage = subscrContEventNotifiicationService
				.selectNotificationByConditions(searchConditions, pageRequest).map(mapper::toDto);

		return ResponseEntity.ok(new PageInfoList<>(resultPage));

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/notifications/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationGetOne(@PathVariable("id") Long id) {

		checkNotNull(id);

		SubscrContEventNotificationDTO result = mapper.toDto(subscrContEventNotifiicationService.findNotification(id));

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
	@RequestMapping(value = "/notifications/revision", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsUpdateRevisionFalse(
			@RequestParam(value = "notificationIds", required = true) Long[] notificationIds,
			@RequestParam(value = "isNew", required = false, defaultValue = "false") Boolean isNew) {

		checkNotNull(isNew);

		ApiActionVoidProcess process = () -> {
			if (notificationIds != null && notificationIds.length > 0) {
				subscrContEventNotifiicationService.updateNotificationsRevisions(getSubscriberParam(),
						Arrays.asList(notificationIds), isNew);
			}
		};

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				if (notificationIds != null && notificationIds.length > 0) {
					subscrContEventNotifiicationService.updateNotificationsRevisions(getSubscriberParam(),
							Arrays.asList(notificationIds), isNew);
				}
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(process);

	}

	/**
	 *
	 * @param notificationIds
	 * @return
	 */
	@RequestMapping(value = "/notifications/revision/isNew", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsUpdateRevisionTrue(
			@RequestParam(value = "notificationIds", required = true) Long[] notificationIds,
			@RequestParam(value = "isNew", required = false, defaultValue = "true") Boolean isNew) {

		checkNotNull(isNew);

		ApiActionVoidProcess process = () -> {
			if (notificationIds != null && notificationIds.length > 0) {
				subscrContEventNotifiicationService.updateNotificationsRevisions(getSubscriberParam(),
						Arrays.asList(notificationIds), isNew);
			}
		};

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {

			}
		};

		return ApiActionTool.processResponceApiActionUpdate(process);

	}

    /**
     *
     * @param fromDateStr
     * @param toDateStr
     * @param contObjectIds
     * @param contEventTypeIds
     * @param isNew
     * @param revisionIsNew
     * @return
     */
	@RequestMapping(value = "/notifications/revision/all", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
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

		ApiActionVoidProcess process = () -> {
			subscrContEventNotifiicationService.updateRevisionByConditionsFast(getSubscriberParam(), actionDP,
					contObjectList, contEventTypeIdPairList, revisionIsNew);
		};

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(process);
	}

	/**
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	//@RequestMapping(value = "/notifications/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)

	/**
	 *
	 * Makred for DELETE
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @param noGreenColor
	 * @return
	 */

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

		List<MonitorContEventNotificationStatus> preResultList = subscrContEventNotifiicationStatusService
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

		List<ContObject> contObjects = objectAccessService.findContObjects(getSubscriberId());

		List<MonitorContEventNotificationStatus> resultList = subscrContEventNotifiicationStatusService
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> cityMonitorContEventsStatusCollapse(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor,
			@RequestParam(value = "contGroupId", required = false) Long contGroupId) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(
					String.format("Invalid parameters fromDate:{} is greater than toDate:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = objectAccessService.findContObjects(getSubscriberId(), contGroupId);

		List<CityMonitorContEventsStatus> result = subscrContEventNotifiicationStatusService
				.selectCityMonitoryContEventsStatus(getSubscriberParam(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/cityStatusCollapseV2", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> cityMonitorContEventsStatusCollapseV2(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor,
			@RequestParam(value = "contGroupId", required = false) Long contGroupId) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(
					String.format("Invalid parameters fromDate:{} is greater than toDate:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = objectAccessService.findContObjects(getSubscriberId(), contGroupId);

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotifiicationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(getSubscriberParam(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/{contObjectId}/eventTypes/statusCollapse",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> monitorContEventTypesStatusCollapse(
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

		List<MonitorContEventTypeStatus> resultList = subscrContEventNotifiicationStatusService
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsContObjectMonitorEvents(
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		List<ContEventMonitor> resultList = contEventMonitorService.selectByContObject(contObjectId);

		return ResponseEntity.ok(resultList);
	}

	/**
	 * TODO contServiceType feature
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/notifications/contObject/{contObjectId}/monitorEventsV2", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsContObjectMonitorV2Events(
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		List<ContEventMonitorXDTO> resultList = contEventMonitorV3Service.selectByContObject(contObjectId).stream()
            .map(ContEventMonitorXDTO::new)
            .collect(Collectors.toList());

		if (resultList.isEmpty()) {
			return ApiResponse.responseOK();
		}

		List<ContEventMonitorXDTO> filteredResultList = resultList.stream().filter(i -> i.getContEventLevel() != null)
			.collect(Collectors.toList());

        filteredResultList = contEventMonitorV3Service.enhanceWithContServiceType(filteredResultList);

		return ResponseEntity.ok(filteredResultList);
	}


	@RequestMapping(value = "/notifications/contObject/{contObjectId}/monitorEventsV2/byContZPoint/{contZPointId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> notificationsContObjectMonitorV2EventsByContZPoint(
			@PathVariable(value = "contObjectId") Long contObjectId,
            @PathVariable(value = "contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		List<ContEventMonitorX> resultList = contEventMonitorV3Service.selectByContZPoint(contObjectId,contZPointId).stream().filter(i -> i.getContEventLevel() != null)
            .collect(Collectors.toList());

		if (resultList.isEmpty()) {
			return ApiResponse.responseOK();
		}

        List<ContEventMonitorXDTO> resultDTO = contEventService.loadContEventTypeModel(resultList).stream()
            .map(ContEventMonitorXDTO::new).collect(Collectors.toList());

        return ResponseEntity.ok(resultDTO);
	}

	/**
	 *
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/notifications/monitorColor", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
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

				Page<SubscrContEventNotificationDTO> pageResult = subscrContEventNotifiicationService
						.selectNotificationByConditions(searchConditions, PAGE_LIMIT_1).map(mapper::toDto);

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
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventCategory() {
		List<ContEventCategory> xList = contEventService.selectContEventCategoryList();
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(xList));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/deviations", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventDeviation() {
		List<ContEventDeviation> resultList = contEventService.findContEventDeviation();
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
