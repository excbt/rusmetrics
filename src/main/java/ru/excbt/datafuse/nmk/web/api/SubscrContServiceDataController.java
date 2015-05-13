package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterSummary;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataController extends WebApiController {

	public static final String HEAT = "heat";
	public static final String HW = "hw";

	private static final List<String> SUPPORTED_SERVICES = Arrays.asList(HEAT,
			HW);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @param serviceType
	 * @param zPointId
	 * @param timeDetailType
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> serviceDataHWater(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(beginDateS);
		checkNotNull(endDateS);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ResponseEntity.badRequest().body(
					String.format("contZPointId (id=%d) not found",
							contZPointId));
		}

		if (contZPoint.getContObject() == null
				|| contZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("contZPointId (id=%d) is not valid for contObject (id=%d)",
									contZPointId, contObjectId));
		}

		String serviceType = contZPoint.getContServiceType().getKeyname();

		if (!SUPPORTED_SERVICES.contains(serviceType)) {
			return ResponseEntity.badRequest().body(
					String.format("Service type %s is not supported yet",
							serviceType));
		}

		DateTime beginD = null;
		DateTime endD = null;
		try {
			beginD = DATE_FORMATTER.parseDateTime(beginDateS);
			endD = DATE_FORMATTER.parseDateTime(endDateS);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		if (beginD.compareTo(endD) > 0) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ResponseEntity.badRequest().body(
					String.format("Invalid parameters timeDetailType:{}",
							timeDetailType));
		}

		DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);

		List<ContServiceDataHWater> result = contServiceDataHWaterService
				.selectByContZPoint(contZPointId, timeDetail, beginD, endOfDay);

		return ResponseEntity.ok(result);

	}

	/**
	 * 
	 * @param serviceType
	 * @param zPointId
	 * @param timeDetailType
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/paged", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> serviceDataHWaterPaged(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(beginDateS);
		checkNotNull(endDateS);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ResponseEntity.badRequest().body(
					String.format("contZPointId (id=%d) not found",
							contZPointId));
		}

		if (contZPoint.getContObject() == null
				|| contZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("contZPointId (id=%d) is not valid for contObject (id=%d)",
									contZPointId, contObjectId));
		}

		String serviceType = contZPoint.getContServiceType().getKeyname();

		if (!SUPPORTED_SERVICES.contains(serviceType)) {
			return ResponseEntity.badRequest().body(
					String.format("Service type %s is not supported yet",
							serviceType));
		}

		DateTime beginD = null;
		DateTime endD = null;
		try {
			beginD = DATE_FORMATTER.parseDateTime(beginDateS);
			endD = DATE_FORMATTER.parseDateTime(endDateS);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		if (beginD.compareTo(endD) > 0) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ResponseEntity.badRequest().body(
					String.format("Invalid parameters timeDetailType:{}",
							timeDetailType));
		}

		DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);

		Page<ContServiceDataHWater> result = contServiceDataHWaterService
				.selectByContZPoint(contZPointId, timeDetail, beginD, endOfDay,
						pageable);

		return ResponseEntity
				.ok(new PageInfoList<ContServiceDataHWater>(result));

	}

	/**
	 * 
	 * @param serviceType
	 * @param zPointId
	 * @param timeDetailType
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/summary", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> serviceDataHWaterSummary(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(beginDateS);
		checkNotNull(endDateS);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ResponseEntity.badRequest().body(
					String.format("contZPointId (id=%d) not found",
							contZPointId));
		}

		if (contZPoint.getContObject() == null
				|| contZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("contZPointId (id=%d) is not valid for contObject (id=%d)",
									contZPointId, contObjectId));
		}

		String serviceType = contZPoint.getContServiceType().getKeyname();

		if (!SUPPORTED_SERVICES.contains(serviceType)) {
			return ResponseEntity.badRequest().body(
					String.format("Service type %s is not supported yet",
							serviceType));
		}

		LocalDateTime beginD = null;
		LocalDateTime endD = null;
		try {
			beginD = DATE_FORMATTER.parseLocalDateTime(beginDateS);
			endD = DATE_FORMATTER.parseLocalDateTime(endDateS);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		if (beginD.compareTo(endD) > 0) {
			return ResponseEntity.badRequest().body(
					String.format(
							"Invalid parameters beginDateS:{}, endDateS:{}",
							beginDateS, endDateS));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ResponseEntity.badRequest().body(
					String.format("Invalid parameters timeDetailType:{}",
							timeDetailType));
		}

		LocalDateTime endOfDay = JodaTimeUtils.endOfDay(endD);

		ContServiceDataHWaterTotals totals = contServiceDataHWaterService
				.selectContZPointTotals(contZPointId, timeDetail, beginD,
						endOfDay);

		ContServiceDataHWater firstAbs = contServiceDataHWaterService
				.selectLastAbsData(contZPointId, beginD);

		ContServiceDataHWater lastAbs = contServiceDataHWaterService
				.selectLastAbsData(contZPointId, endOfDay);

		
		ContServiceDataHWaterSummary result = new ContServiceDataHWaterSummary();
		result.setTotals(totals);
		result.setFirstData(firstAbs);
		result.setLastData(lastAbs);
		
		
		return ResponseEntity.ok(result);
	}
}
