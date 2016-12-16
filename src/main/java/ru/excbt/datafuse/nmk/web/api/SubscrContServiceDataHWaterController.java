package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjectsServiceTypeInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectServiceTypeInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterSummary;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.model.support.ServiceDataImportInfo;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterDeltaService;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvFileUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.nmk.utils.FileInfoMD5;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionTN;

/**
 * Контроллер для работы с данными по теплоснабжению для абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataHWaterController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContServiceDataHWaterController.class);

	public static final String HEAT = "heat";
	public static final String HW = "hw";
	public static final String CW = "cw";

	private static final List<String> SUPPORTED_SERVICES = Arrays.asList(HEAT, HW, CW);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(ReportService.DATE_TEMPLATE);

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private HWatersCsvService hWatersCsvService;

	@Autowired
	private WebAppPropsService webAppPropsService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	private ContServiceDataHWaterDeltaService contObjectHWaterDeltaService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private SLogWriterService sLogWriterService;

	/**
	 * 
	 * @param serviceType
	 * @param zPointId
	 * @param timeDetailType
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataHWater(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(fromDateStr);
		checkNotNull(toDateStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters fromDateStr:{} and toDateStr:{}", fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ResponseEntity.badRequest().body(String.format("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity.badRequest().body(String
					.format("contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		String serviceType = contZPoint.getContServiceType().getKeyname();

		if (!SUPPORTED_SERVICES.contains(serviceType)) {
			return ResponseEntity.badRequest().body(String.format("Service type %s is not supported yet", serviceType));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters timeDetailType:{}", timeDetailType));
		}

		List<ContServiceDataHWater> result = contServiceDataHWaterService.selectByContZPoint(contZPointId, timeDetail,
				datePeriodParser.getLocalDatePeriod().buildEndOfDay());

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
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/paged", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataHWaterPaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(fromDateStr);
		checkNotNull(toDateStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		Direction dataDateDirection = Sort.Direction.DESC;
		if ("asc".equalsIgnoreCase(dataDateSort)) {
			dataDateDirection = Sort.Direction.ASC;
		}

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters fromDateStr:%s and toDateStr:%s", fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType: %s", timeDetailType));
		}

		Sort sort = new Sort(dataDateDirection, "dataDate");

		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<ContServiceDataHWater> result = contServiceDataHWaterService.selectByContZPoint(contZPointId, timeDetail,
				datePeriodParser.getLocalDatePeriod().buildEndOfDay(), pageRequest);

		return ResponseEntity.ok(new PageInfoList<>(result));

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
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/summary",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataHWaterSummary(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS, @RequestParam("endDate") String endDateS) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(beginDateS);
		checkNotNull(endDateS);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		LocalDatePeriod period = LocalDatePeriod.builder().dateFrom(beginDateS).dateTo(endDateS).build();

		if (period.isInvalidEq()) {
			return responseBadRequest(
					ApiResult.validationError("Invalid parameters beginDateS: %s, endDateS:%s", beginDateS, endDateS));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType:%s", timeDetailType));
		}

		//LocalDateTime endOfPeriod = JodaTimeUtils.startOfDay(period.getDateTimeTo().plusDays(1));

		LocalDateTime endOfDay = JodaTimeUtils.endOfDay(period.getDateTimeTo());

		ContServiceDataHWaterTotals totals = contServiceDataHWaterService.selectContZPoint_Totals(contZPointId,
				timeDetail, period.getDateTimeFrom(), endOfDay);

		ContServiceDataHWater firstAbs = contServiceDataHWaterService.selectLastAbsData(contZPointId, timeDetail,
				period.getDateTimeFrom(), false);

		ContServiceDataHWater lastAbs = contServiceDataHWaterService.selectLastAbsData(contZPointId, timeDetail,
				endOfDay, true);

		ContServiceDataHWater avg = contServiceDataHWaterService.selectContZPoint_Avgs(contZPointId, timeDetail,
				period);

		ContServiceDataHWaterSummary result = new ContServiceDataHWaterSummary();
		result.setTotals(totals);
		result.setFirstData(firstAbs);
		result.setLastData(lastAbs);
		result.setAverage(avg);

		ContServiceDataHWaterTotals diffs = new ContServiceDataHWaterTotals();

		if (firstAbs != null && lastAbs != null) {

			diffs.setM_in(processDelta(firstAbs.getM_in(), lastAbs.getM_in()));
			diffs.setM_out(processDelta(firstAbs.getM_out(), lastAbs.getM_out()));
			diffs.setV_in(processDelta(firstAbs.getV_in(), lastAbs.getV_in()));
			diffs.setV_out(processDelta(firstAbs.getV_out(), lastAbs.getV_out()));
			diffs.setH_delta(processDelta(firstAbs.getH_delta(), lastAbs.getH_delta()));

		}
		result.setDiffs(diffs);

		return ResponseEntity.ok(result);
	}

	private BigDecimal processDelta(BigDecimal a, BigDecimal b) {
		return a == null || b == null ? null : b.subtract(a);
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
	public void getDataHWater_CsvAbsDownload(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String beginDateS, @RequestParam("endDate") String endDateS,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

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

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
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

		DateTime endOfDay = endD.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = contServiceDataHWaterService.selectDataAbs_Csv(contZPointId,
				timeDetail, beginD, endOfDay);

		byte[] byteArray = hWatersCsvService.writeHWaterDataToCsvAbs(cvsDataList);

		response.setContentType(MIME_CSV);
		response.setContentLength(byteArray.length);

		String outputFilename = String.format("HWaters_(contObject_%d)_(contZPoint_%d)_%s_(%s-%s).csv", contObjectId,
				contObjectId, timeDetailType, beginDateS, endDateS);

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", outputFilename);
		response.setHeader(headerKey, headerValue);
		//
		OutputStream outStream = response.getOutputStream();
		outStream.write(byteArray);
		outStream.close();

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/service/{timeDetailType}/{contZPointId}/csv/noAbs",
			method = RequestMethod.GET)
	public ResponseEntity<?> getDataHWater_CsvDownload(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return responseBadRequest(ApiResult.validationError("Invalid parameters fromDateStr:%s and toDateStr:%s",
					fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return responseBadRequest();
		}

		LocalDatePeriod ldp = datePeriodParser.getLocalDatePeriod();

		List<ContServiceDataHWater> dataHWaterList = contServiceDataHWaterService.selectByContZPoint(contZPointId,
				timeDetail, ldp.buildEndOfDay());

		byte[] csaBytes;
		try {
			csaBytes = hWatersCsvService.writeHWaterDataToCsv(dataHWaterList);
		} catch (JsonProcessingException e) {
			return responseInternalServerError(ApiResult.error(e));
		}

		ByteArrayInputStream is = new ByteArrayInputStream(csaBytes);

		String outputFilename = String.format("HWaters_(contObject_%d)_(contZPoint_%d)_%s_(%s-%s)_noabs.csv",
				contObjectId, contObjectId, timeDetailType, ldp.getDateFromStr(), ldp.getDateToStr());

		return processDownloadInputStream(is, HWatersCsvService.MEDIA_TYPE_CSV, csaBytes.length, outputFilename);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param multipartFile
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/{timeDetailType}/csv",
			method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> uploadManualDataHWater(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("file") MultipartFile multipartFile) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);

		//		if (TimeDetailKey.TYPE_1H.getKeyname().equals(timeDetailType)) {
		//			return responseBadRequest(ApiResult.validationError("Data of 1h is not supported for uploading"));
		//		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
		}

		String inFilename = webAppPropsService.getHWatersCsvInputDir() + webAppPropsService.getSubscriberCsvFilename(
				currentSubscriberService.getSubscriberId(), currentSubscriberService.getCurrentUserId());

		File inFile = new File(inFilename);

		try {
			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
		} catch (IOException e) {
			logger.error("Exception:{}", e);
			return responseInternalServerError(ApiResult.error(e));
		}

		List<ContServiceDataHWater> inData;
		try (FileInputStream fio = new FileInputStream(inFilename)) {
			inData = hWatersCsvService.parseHWaterDataCsv(fio);
		} catch (IOException e) {
			logger.error("Exception: {}", e);
			return responseInternalServerError(ApiResult.error(e));
		}

		if (inData.stream().map(i -> i.getTimeDetailType()).distinct().filter(s -> s == null).count() > 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					ApiResult.build(ApiResultCode.ERR_VALIDATION, "Null value for timeDetailType is not allowed"));
		}

		List<String> timeDetailTypes = inData.stream().map(i -> i.getTimeDetailType()).distinct()
				.collect(Collectors.toList());
		if (timeDetailTypes.size() > 1 || timeDetailTypes.get(0) == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					ApiResult.build(ApiResultCode.ERR_VALIDATION, "More than 2 type of timeDetailType is not allowed"));
		}

		inData.forEach(i -> {
			i.setTimeDetailType(timeDetailType);
		});

		ApiActionObjectProcess actionProcess = () -> {

			contServiceDataHWaterService.insertManualLoadDataHWater(contZPointId, inData, inFile);
			FileInfoMD5 resultFileInfo = new FileInfoMD5(inFile);

			return resultFileInfo;
		};
		return responseUpdate(actionProcess);

		//		ApiAction action = new AbstractEntityApiAction<FileInfoMD5>() {
		//
		//			@Override
		//			public void process() {
		//				inData.forEach(i -> {
		//					i.setTimeDetailType(timeDetailType);
		//				});
		//
		//				contServiceDataHWaterService.insertManualLoadDataHWater(contZPointId, inData, inFile);
		//				FileInfoMD5 resultFileInfo = new FileInfoMD5(inFile);
		//				setResultEntity(resultFileInfo);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param multipartFile
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/csv",
			method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> uploadManualDataHWaterUniversal(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestParam("file") MultipartFile multipartFile) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
		}

		String inFilename = webAppPropsService.getHWatersCsvInputDir() + webAppPropsService.getSubscriberCsvFilename(
				currentSubscriberService.getSubscriberId(), currentSubscriberService.getCurrentUserId());

		File inFile = new File(inFilename);

		try {
			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
		} catch (IOException e) {
			logger.error("Exception:{}", e);
			return responseInternalServerError(ApiResult.error(e));
		}

		List<ContServiceDataHWater> inData;
		try (FileInputStream fio = new FileInputStream(inFilename)) {
			inData = hWatersCsvService.parseHWaterDataCsv(fio);
		} catch (IOException e) {
			logger.error("Exception: {}", e);
			return responseInternalServerError(ApiResult.error(e));
		}

		if (inData.stream().map(i -> i.getTimeDetailType()).distinct().filter(s -> s == null).count() > 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					ApiResult.build(ApiResultCode.ERR_VALIDATION, "Null value for timeDetailType is not allowed"));
		}

		List<String> timeDetailTypes = inData.stream().map(i -> i.getTimeDetailType()).distinct()
				.collect(Collectors.toList());
		if (timeDetailTypes.size() > 1 || timeDetailTypes.get(0) == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					ApiResult.build(ApiResultCode.ERR_VALIDATION, "More than 2 type of timeDetailType is not allowed"));
		}

		ApiAction action = new AbstractEntityApiAction<FileInfoMD5>() {

			@Override
			public void process() {
				contServiceDataHWaterService.insertManualLoadDataHWater(contZPointId, inData, inFile);
				FileInfoMD5 resultFileInfo = new FileInfoMD5(inFile);
				setResultEntity(resultFileInfo);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param multipartFiles
	 * @return
	 */
	@RequestMapping(value = "/service/datahwater/contObjects/importData", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> importDataHWaterMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles) {

		checkNotNull(multipartFiles);

		SubscriberParam subscriberParam = getSubscriberParam();

		List<String> fileNameErrorDesc = new ArrayList<>();
		List<String[]> fileNameData = new ArrayList<>();

		// Check file names
		for (MultipartFile multipartFile : multipartFiles) {
			String fileName = FilenameUtils.getName(multipartFile.getOriginalFilename());
			if (fileName == null) {
				return responseBadRequest();
			}

			String[] nameParts = fileName.split("_");
			if (nameParts == null || nameParts.length < 2) {
				fileNameErrorDesc.add("Некоррекное имя файла: " + fileName);
				break;
			}

			fileNameData.add(new String[] { fileName, nameParts[0], nameParts[1] });
		}

		if (fileNameErrorDesc.size() > 0) {
			return responseBadRequest(ApiResult.badRequest(fileNameErrorDesc));
		}

		logger.info("Looking for subscriberId: {}, serials: {}", getSubscriberParam().getSubscriberId(),
				fileNameData.stream().map(i -> i[1]).collect(Collectors.toList()));

		List<Tuple> deviceObjectsData = subscrContObjectService.selectSubscriberDeviceObjectByNumber(
				getSubscriberParam(), fileNameData.stream().map(i -> i[1]).collect(Collectors.toList()));

		deviceObjectsData.forEach(i -> logger.info("deviceObjectNumber: {}, tsNumber: {}, isManualLoading: {}",
				i.get("deviceObjectNumber"), i.get("tsNumber"), i.get("isManualLoading")));

		List<ServiceDataImportInfo> serviceDataImportInfos = new ArrayList<>();

		Map<String, Tuple> filenameDBInfos = new HashedMap();

		for (String[] s : fileNameData) {

			final List<Tuple> checkRows = deviceObjectsData.stream()
					.filter(i -> s[1].equals(i.get("deviceObjectNumber").toString())
							&& s[2].equals(i.get("tsNumber").toString()))
					.collect(Collectors.toList());

			if (checkRows.size() == 0) {
				fileNameErrorDesc.add(String.format(
						"Точка учета с прибором № %s и теплосистемой № %s не найдена. Файл: %s", s[0], s[1], s[2]));
				continue;
			}

			if (checkRows.size() > 1) {
				fileNameErrorDesc.add(String.format(
						"Точка учета с прибором № %s и теплосистемой № %s не уникальна. Файл: %s", s[0], s[1], s[2]));
				continue;
			}

			final Tuple row = checkRows.get(0);

			if (!Boolean.TRUE.equals(DBRowUtils.asBoolean(row.get("isManualLoading")))) {
				fileNameErrorDesc.add(String.format(
						"Точка учета с прибором № %s и теплосистемой № %s не поддерживают ипорт данных из файла. Файл: %s",
						s[0], s[1], s[2]));
				continue;
			}

			filenameDBInfos.put(s[0], row);

		}

		if (fileNameErrorDesc.size() > 0) {
			return responseBadRequest(ApiResult.badRequest(fileNameErrorDesc));
		}

		List<UUID> trxIds = new ArrayList<>();

		// All conditions is passed

		SLogSessionTN sLogSessionTN = sLogWriterService.newSessionWebT1(subscriberParam.getSubscrUserId());

		UUID trxId = sLogSessionTN.getSessionUUID();

		trxIds.add(trxId);

		for (MultipartFile multipartFile : multipartFiles) {

			String fileName = FilenameUtils.getName(multipartFile.getOriginalFilename());

			String inFilename = webAppPropsService.getSubscriberCsvPath(subscriberParam.getSubscriberId(),
					subscriberParam.getSubscrUserId(), trxId.toString().substring(30, 36) + '_' + fileName);

			File inFile = new File(inFilename);

			try {
				@SuppressWarnings("unused")
				String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
			} catch (IOException e) {
				logger.error("Exception:{}", e);
				return responseInternalServerError(ApiResult.error(e));
			}

			Tuple row = filenameDBInfos.get(fileName);

			checkState(row != null);

			ServiceDataImportInfo importInfo = new ServiceDataImportInfo(subscriberParam.getSubscriberId(),
					DBRowUtils.asLong(row.get("contObjectId")), DBRowUtils.asLong(row.get("contZPointId")),
					DBRowUtils.asLong(row.get("deviceObjectId")), DBRowUtils.asLong(row.get("subscrDataSourceId")),
					subscriberParam.getSubscrUserId(), inFilename);

			serviceDataImportInfos.add(importInfo);

		}

		return responseOK(trxIds.toString());

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/service/out/csv", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOutCsvDownloadsAvailable() {

		List<File> listFiles = HWatersCsvFileUtils.getOutFiles(webAppPropsService,
				currentSubscriberService.getSubscriberId());

		if (listFiles == null || listFiles.isEmpty()) {
			return responseNotFound();
		}

		List<FileInfoMD5> resultFiles = listFiles.stream().map((i) -> {
			return new FileInfoMD5(i.getName());
		}).collect(Collectors.toList());

		return ResponseEntity.ok(resultFiles);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	@RequestMapping(value = "/service/out/csv/{filename}", method = RequestMethod.GET)
	public ResponseEntity<?> getOutCsvDownload(@PathVariable("filename") String filename) {

		logger.debug("Request for downloading file: {}", filename);

		File file = HWatersCsvFileUtils.getOutCsvFile(webAppPropsService, currentSubscriberService.getSubscriberId(),
				filename);

		if (file == null) {
			return responseBadRequest(ApiResult.build(ApiResultCode.ERR_VALIDATION, "File not found"));
		}

		return processDownloadFile(file, HWatersCsvService.MEDIA_TYPE_CSV);

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/{timeDetailType}/csv",
			method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteManualDataHWater(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String dateFromStr, @RequestParam("endDate") String dateToStr) {

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		checkNotNull(timeDetailType);
		checkNotNull(contZPointId);
		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters fromDateStr:{} and toDateStr:{}", dateFromStr, dateToStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", dateFromStr, dateToStr));
		}

		if (TimeDetailKey.searchKeyname(timeDetailType) == null) {
			return responseBadRequest(ApiResult.badRequest("TimeDetailKey %s is not supported", timeDetailType));
		}

		//		if (TimeDetailKey.TYPE_1H.getKeyname().equals(timeDetailType)) {
		//			return responseBadRequest(ApiResult.validationError("Data of 1h is not supported for uploading"));
		//		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
		}

		String outFilename = webAppPropsService.getHWatersCsvOutputDir() + webAppPropsService.getSubscriberCsvFilename(
				currentSubscriberService.getSubscriberId(), currentSubscriberService.getCurrentUserId());

		File outFile = new File(outFilename);

		ApiAction action = new AbstractEntityApiAction<FileInfoMD5>() {

			@Override
			public void process() {

				@SuppressWarnings("unused")
				List<ContServiceDataHWater> deletedRecords = contServiceDataHWaterService.deleteManualDataHWater(
						contZPointId, datePeriodParser.getLocalDatePeriod().buildEndOfDay(),
						TimeDetailKey.searchKeyname(timeDetailType), outFile);
				FileInfoMD5 resultFileInfo = new FileInfoMD5(outFile);
				setResultEntity(resultFileInfo);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@RequestMapping(value = "/service/hwater/contObjects/serviceTypeInfo", method = RequestMethod.GET)
	public ResponseEntity<?> getContObjectsServiceTypeInfo(@RequestParam("dateFrom") String dateFromStr,
			@RequestParam("dateTo") String dateToStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return responseBadRequest(
					ApiResult.validationError("Invalid parameters dateFrom:%s and dateTo:%s", dateFromStr, dateToStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return responseBadRequest(ApiResult.validationError(
					"Invalid parameters dateFrom:%s is greater than dateTo:%s", dateFromStr, dateToStr));
		}

		List<CityContObjectsServiceTypeInfo> resultList = contObjectHWaterDeltaService
				.getAllCityMapContObjectsServiceTypeInfo(getCurrentSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		return responseOK(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@RequestMapping(value = "/service/hwater/contObjects/serviceTypeInfo/{contObjectId}", method = RequestMethod.GET)
	public ResponseEntity<?> getContObjectsServiceTypeInfoContObject(@PathVariable("contObjectId") long contObjectId,
			@RequestParam("dateFrom") String dateFromStr, @RequestParam("dateTo") String dateToStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkDatePeriodResponse = checkDatePeriodArguments(datePeriodParser);
		if (checkDatePeriodResponse != null) {
			return checkDatePeriodResponse;
		}

		List<ContObjectServiceTypeInfo> contObjectServiceTypeInfos = contObjectHWaterDeltaService
				.getContObjectServiceTypeInfo(getCurrentSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), contObjectId);

		return responseOK(contObjectServiceTypeInfos.isEmpty() ? null : contObjectServiceTypeInfos.get(0));
	}

	/**
	 * 
	 * @param contObjectId
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@RequestMapping(value = "/service/hwater/contObjects/serviceTypeInfo/city", method = RequestMethod.GET)
	public ResponseEntity<?> getContObjectsServiceTypeInfoCity(@RequestParam("dateFrom") String dateFromStr,
			@RequestParam("dateTo") String dateToStr, @RequestParam("cityFias") String cityFiasStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);
		checkNotNull(cityFiasStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkDatePeriodResponse = checkDatePeriodArguments(datePeriodParser);
		if (checkDatePeriodResponse != null) {
			return checkDatePeriodResponse;
		}

		UUID cityFiasUUID = null;
		try {
			cityFiasUUID = UUID.fromString(cityFiasStr);
		} catch (Exception e) {
			return responseBadRequest(ApiResult.validationError("cityFias is not valid UUID"));
		}

		List<CityContObjectsServiceTypeInfo> resultList = contObjectHWaterDeltaService
				.getOneCityMapContObjectsServiceTypeInfo(getCurrentSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), cityFiasUUID);

		return responseOK(resultList);
	}

}
