package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterSummary;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContServiceDataController.class);

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

		LocalDateTime endOfPeriod = JodaTimeUtils.startOfDay(endD.plusDays(1));
		LocalDateTime endOfDay = JodaTimeUtils.endOfDay(endD);

		ContServiceDataHWaterTotals totals = contServiceDataHWaterService
				.selectContZPointTotals(contZPointId, timeDetail, beginD,
						endOfDay);

		ContServiceDataHWater firstAbs = contServiceDataHWaterService
				.selectLastAbsData(contZPointId, beginD);

		ContServiceDataHWater lastAbs = contServiceDataHWaterService
				.selectLastAbsData(contZPointId, endOfPeriod);

		ContServiceDataHWaterSummary result = new ContServiceDataHWaterSummary();
		result.setTotals(totals);
		result.setFirstData(firstAbs);
		result.setLastData(lastAbs);

		ContServiceDataHWaterTotals diffs = new ContServiceDataHWaterTotals();

		if (firstAbs != null && lastAbs != null) {

			diffs.setM_in(lastAbs.getM_in() == null
					|| firstAbs.getM_in() == null ? null : lastAbs.getM_in()
					.subtract(firstAbs.getM_in()));
			diffs.setM_out(lastAbs.getM_out() == null
					|| firstAbs.getM_out() == null ? null : lastAbs.getM_out()
					.subtract(firstAbs.getM_out()));

			diffs.setV_in(lastAbs.getV_in() == null
					|| firstAbs.getV_in() == null ? null : lastAbs.getV_in()
					.subtract(firstAbs.getV_in()));
			diffs.setV_out(lastAbs.getV_out() == null
					|| firstAbs.getV_out() == null ? null : lastAbs.getV_out()
					.subtract(firstAbs.getV_out()));

			diffs.setH_delta(lastAbs.getH_delta() == null
					|| firstAbs.getH_delta() == null ? null : lastAbs
					.getH_delta().subtract(firstAbs.getH_delta()));
		}
		result.setDiffs(diffs);

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
	 * @throws IOException
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/csv", method = RequestMethod.GET)
	public void serviceDataHWaterCsvDownload(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(beginDateS);
		checkNotNull(endDateS);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (contZPoint.getContObject() == null
				|| contZPoint.getContObject().getId() != contObjectId) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String serviceType = contZPoint.getContServiceType().getKeyname();

		if (!SUPPORTED_SERVICES.contains(serviceType)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		DateTime beginD = null;
		DateTime endD = null;
		try {
			beginD = DATE_FORMATTER.parseDateTime(beginDateS);
			endD = DATE_FORMATTER.parseDateTime(endDateS);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (beginD.compareTo(endD) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = contServiceDataHWaterService
				.selectDataAbs_Csv(contZPointId, timeDetail, beginD,
						endOfDay);

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWaterAbs_Csv.class,
				ContServiceDataHWater_CsvFormat.class);

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterAbs_Csv.class)
				.withHeader();

		byte[] byteArray = mapper.writer(schema).writeValueAsBytes(cvsDataList);

		response.setContentType(MIME_CSV);
		response.setContentLength(byteArray.length);

		String outputFilename = String.format(
				"HWaters_(contObject_%d)_(contZPoint_%d)_%s_(%s-%s)",
				contObjectId, contObjectId, timeDetailType, beginDateS,
				endDateS);

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				outputFilename + FILE_CSV_EXT);
		response.setHeader(headerKey, headerValue);
		//
		OutputStream outStream = response.getOutputStream();
		outStream.write(byteArray);
		outStream.close();

	}

}
