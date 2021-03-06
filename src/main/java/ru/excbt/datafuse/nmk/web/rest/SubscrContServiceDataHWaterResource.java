package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.dto.ContServiceDataHWaterDTO;
import ru.excbt.datafuse.nmk.service.utils.CsvUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.service.utils.ObjectAccessUtil;
import ru.excbt.datafuse.nmk.utils.FileInfoMD5;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * Контроллер для работы с данными по теплоснабжению для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
@RestController
@RequestMapping(value = "/api/subscr")
@Primary
public class SubscrContServiceDataHWaterResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContServiceDataHWaterResource.class);


	public static final String HEAT = "heat";
	public static final String HW = "hw";
	public static final String CW = "cw";

	private static final List<String> SUPPORTED_SERVICES = Arrays.asList(HEAT, HW, CW);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(ReportService.DATE_TEMPLATE);

	private final ContZPointService contZPointService;

	private final HWatersCsvService hWatersCsvService;

	private final WebAppPropsService webAppPropsService;

	private final CurrentSubscriberService currentSubscriberService;

	private final ContServiceDataHWaterService contServiceDataHWaterService;

	private final ContServiceDataHWaterDeltaService contObjectHWaterDeltaService;

	private final ContServiceDataHWaterImportService contServiceDataHWaterImportService;

	private final SubscrDataSourceService subscrDataSourceService;

	private final ObjectAccessService objectAccessService;

	protected final PortalUserIdsService portalUserIdsService;

	@Autowired
    public SubscrContServiceDataHWaterResource(ContZPointService contZPointService,
                                               HWatersCsvService hWatersCsvService,
                                               WebAppPropsService webAppPropsService,
                                               CurrentSubscriberService currentSubscriberService,
                                               ContServiceDataHWaterService contServiceDataHWaterService,
                                               ContServiceDataHWaterDeltaService contObjectHWaterDeltaService,
                                               ContServiceDataHWaterImportService contServiceDataHWaterImportService,
                                               SubscrDataSourceService subscrDataSourceService,
                                               ObjectAccessService objectAccessService,
                                               PortalUserIdsService portalUserIdsService) {
        this.contZPointService = contZPointService;
        this.hWatersCsvService = hWatersCsvService;
        this.webAppPropsService = webAppPropsService;
        this.currentSubscriberService = currentSubscriberService;
        this.contServiceDataHWaterService = contServiceDataHWaterService;
        this.contObjectHWaterDeltaService = contObjectHWaterDeltaService;
        this.contServiceDataHWaterImportService = contServiceDataHWaterImportService;
        this.subscrDataSourceService = subscrDataSourceService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
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
    @GetMapping("/{contObjectId}/service/{timeDetailType}/{contZPointId}")
    @Timed
    public ResponseEntity<?> getDataHWater(@PathVariable("contObjectId") long contObjectId,
                                           @PathVariable("contZPointId") long contZPointId,
                                           @PathVariable("timeDetailType") String timeDetailType,
                                           @RequestParam("beginDate") String fromDateStr,
                                           @RequestParam("endDate") String toDateStr) {

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

		List<ContServiceDataHWaterDTO> result = contServiceDataHWaterService.selectDTOByContZPoint(contZPointId, timeDetail,
				datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		return ResponseEntity.ok(result);

	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param timeDetailType
     * @param fromDateStr
     * @param toDateStr
     * @param dataDateSort
     * @param pageable
     * @return
     */
    @GetMapping("/{contObjectId}/service/{timeDetailType}/{contZPointId}/paged")
    @Timed
	public ResponseEntity<?> getDataHWaterPaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = ApiConst.DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

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
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType: %s", timeDetailType));
		}

		Sort sort = new Sort(dataDateDirection, "dataDate");

		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<ContServiceDataHWaterDTO> result = contServiceDataHWaterService.selectByContZPoint(contZPointId, timeDetail,
				datePeriodParser.getLocalDatePeriod().buildEndOfDay(), pageRequest);

		return ResponseEntity.ok(new PageInfoList<>(result));

	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param timeDetailType
     * @param beginDateS
     * @param endDateS
     * @return
     */
    @GetMapping("/{contObjectId}/service/{timeDetailType}/{contZPointId}/summary")
    @Timed
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
			return ApiResponse.responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		LocalDatePeriod period = LocalDatePeriod.builder().dateFrom(beginDateS).dateTo(endDateS).build();

		if (period.isInvalidEq()) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters beginDateS: %s, endDateS:%s", beginDateS, endDateS));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType:%s", timeDetailType));
		}

		//LocalDateTime endOfPeriod = JodaTimeUtils.startOfDay(period.getTo().plusDays(1));

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

			diffs.setM_in(processDeltaDouble(firstAbs.getM_in(), lastAbs.getM_in()));
			diffs.setM_out(processDeltaDouble(firstAbs.getM_out(), lastAbs.getM_out()));
			diffs.setV_in(processDeltaDouble(firstAbs.getV_in(), lastAbs.getV_in()));
			diffs.setV_out(processDeltaDouble(firstAbs.getV_out(), lastAbs.getV_out()));
			diffs.setH_delta(processDeltaDouble(firstAbs.getH_delta(), lastAbs.getH_delta()));

		}
		result.setDiffs(diffs);

		return ResponseEntity.ok(result);
	}

	private BigDecimal processDelta(BigDecimal a, BigDecimal b) {
		return a == null || b == null ? null : b.subtract(a);
	}

	private Double processDeltaDouble(Double a, Double b) {
		return a == null || b == null ? null : b - a;
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param timeDetailType
     * @param beginDateS
     * @param endDateS
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/{contObjectId}/service/{timeDetailType}/{contZPointId}/csv")
    @Timed
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

		byte[] byteArray = hWatersCsvService.writeDataHWaterToCsvAbs(cvsDataList);

		response.setContentType(ApiConst.MIME_CSV);
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
	@GetMapping("/{contObjectId}/service/{timeDetailType}/{contZPointId}/csv/noAbs")
    @Timed
	public ResponseEntity<?> getDataHWater_CsvDownload(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {



	    ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContObjectId = accessUtil.checkContObjectId (getCurrentPortalUserIds());
		if (!checkContObjectId.test(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Invalid parameters fromDateStr:%s and toDateStr:%s",
					fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ApiResponse.responseBadRequest();
		}

		LocalDatePeriod ldp = datePeriodParser.getLocalDatePeriod();

		List<ContServiceDataHWater> dataHWaterList = contServiceDataHWaterService.selectByContZPoint(contZPointId,
				timeDetail, ldp.buildEndOfDay());

		byte[] csaBytes;
		try {
			csaBytes = hWatersCsvService.writeDataHWaterToCsv(dataHWaterList);
		} catch (JsonProcessingException e) {
			return ApiResponse.responseInternalServerError(ApiResult.error(e));
		}

		ByteArrayInputStream is = new ByteArrayInputStream(csaBytes);

		String outputFilename = String.format("HWaters_(contObject_%d)_(contZPoint_%d)_%s_(%s-%s)_noabs.csv",
				contObjectId, contObjectId, timeDetailType, ldp.getDateFromStr(), ldp.getDateToStr());

		return ApiResponse.processDownloadInputStream(is, HWatersCsvService.MEDIA_TYPE_CSV, csaBytes.length, outputFilename);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param multipartFile
	 * @return
	 */
	@PostMapping("/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/{timeDetailType}/csv")
    @Timed
	public ResponseEntity<?> uploadManualDataHWater(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("file") MultipartFile multipartFile) {

        ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContObjectId = accessUtil.checkContObjectId (getCurrentPortalUserIds());
        if (!checkContObjectId.test(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);

		//		if (TimeDetailKey.TYPE_1H.getKeyname().equals(timeDetailType)) {
		//			return responseBadRequest(ApiResult.validationError("Data of 1h is not supported for uploading"));
		//		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
		}

		String inFilename = webAppPropsService.getHWatersCsvInputDir() + webAppPropsService.getSubscriberCsvFilename(
				currentSubscriberService.getSubscriberId(), currentSubscriberService.getCurrentUserId());

		File inFile = new File(inFilename);

		try {
			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
		} catch (IOException e) {
			logger.error("Exception:{}", e);
			return ApiResponse.responseInternalServerError(ApiResult.error(e));
		}

		List<ContServiceDataHWater> inData;
		try (FileInputStream fio = new FileInputStream(inFilename)) {
			inData = hWatersCsvService.parseDataHWaterCsv(fio);
		} catch (IOException e) {
			logger.error("Exception: {}", e);
			return ApiResponse.responseInternalServerError(ApiResult.error(e));
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
		return ApiResponse.responseUpdate(actionProcess);

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
    @PostMapping("/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/csv")
    @Timed
	public ResponseEntity<?> uploadManualDataHWaterUniversal(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestParam("file") MultipartFile multipartFile) {

        ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContObjectId = accessUtil.checkContObjectId (getCurrentPortalUserIds());
        if (!checkContObjectId.test(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
		}

		String inFilename = webAppPropsService.getHWatersCsvInputDir() + webAppPropsService.getSubscriberCsvFilename(
				currentSubscriberService.getSubscriberId(), currentSubscriberService.getCurrentUserId());

		File inFile = new File(inFilename);

		try {
			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
		} catch (IOException e) {
			logger.error("Exception:{}", e);
			return ApiResponse.responseInternalServerError(ApiResult.error(e));
		}

		List<ContServiceDataHWater> inData;
		try (FileInputStream fio = new FileInputStream(inFilename)) {
			inData = hWatersCsvService.parseDataHWaterCsv(fio);
		} catch (IOException e) {
			logger.error("Exception: {}", e);
			return ApiResponse.responseInternalServerError(ApiResult.error(e));
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

		return ApiActionTool.processResponceApiActionUpdate(action);
	}


    /**
     *
     */
    private class FileNameData {
        final String fileName;
        final String deviceSerial;
        final String tsNumber;

        private FileNameData(String fileName, String deviceSerial, String tsNumber) {
            this.fileName = fileName;
            this.deviceSerial = deviceSerial;
            this.tsNumber = tsNumber;
        }
    }


	/**
	 *
     * Input file type:
     * {DEVICE_OBJECT_SERIAL}_{CONT_ZPOINT_TS_NR}_other
     *
	 * @param multipartFiles
	 * @return
	 */
	@PostMapping("/service/datahwater/contObjects/importData")
    @Timed
	public ResponseEntity<?> importDataHWaterMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles) {

		checkNotNull(multipartFiles);

		if (multipartFiles.length == 0) {
			return ApiResponse.responseBadRequest();
		}

        PortalUserIds userIds = getCurrentPortalUserIds();

        List<CsvUtil.CheckFileResult> checkFileResults = CsvUtil.checkCsvFiles(multipartFiles);
        List<CsvUtil.CheckFileResult> isNotPassed = checkFileResults.stream().filter((i) -> !i.isPassed()).collect(Collectors.toList());

        if (isNotPassed.size() > 0) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest(isNotPassed.stream().map((i) -> i.getErrorDesc()).collect(Collectors.toList())));
        }


		List<String> fileNameErrorDesc = new ArrayList<>();
		List<FileNameData> fileNameDataList = new ArrayList<>();

        checkFileResults.forEach((i) -> {
            String[] nameParts = i.getFileName().split("_");
            if (nameParts == null || nameParts.length < 2) {
                fileNameErrorDesc.add("Некоррекное имя файла: " + i.getFileName()+ ". Ожидается {#Серийный номер прибора}_{#Теплосистемы}_......");
                return;
            }

            fileNameDataList.add(new FileNameData(i.getFileName(), nameParts[0] , nameParts[1]));
            for (String s : nameParts) {
                logger.debug("Name parts: {}", s);
            }

        });

        if (fileNameErrorDesc.size() > 0 || fileNameDataList.size() == 0) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest(fileNameErrorDesc));
        }


		// HWaterImport

		logger.debug("Looking for subscriberId: {}, serials: {}", userIds.getSubscriberId(),
				fileNameDataList.stream().map(i -> i.deviceSerial).collect(Collectors.toList()));

		List<Tuple> deviceObjectsData = objectAccessService.findAllContZPointDeviceObjectsEx(userIds.getSubscriberId(), fileNameDataList.stream().map(i -> i.deviceSerial).collect(Collectors.toList()));

		deviceObjectsData.forEach(i -> logger.info("deviceObjectNumber: {}, tsNumber: {}, isManualLoading: {}",
				i.get("deviceObjectNumber"), i.get("tsNumber"), i.get("isManualLoading")));

		List<ServiceDataImportInfo> serviceDataImportInfos = new ArrayList<>();

		Map<String, Tuple> filenameDBInfos = new HashMap<>();

		for (FileNameData data : fileNameDataList) {

			final List<Tuple> checkRows = deviceObjectsData.stream()
					.filter(i -> data.deviceSerial.equals(i.get("deviceObjectNumber").toString())
							&& data.tsNumber.equals(i.get("tsNumber").toString()))
					.collect(Collectors.toList());

			if (checkRows.size() == 0) {
				fileNameErrorDesc
						.add(String.format("Точка учета с прибором № %s и теплосистемой № %s не найдена. Файл: %s",
								data.deviceSerial, data.tsNumber, data.fileName));
				continue;
			}

			if (checkRows.size() > 1) {
				fileNameErrorDesc
						.add(String.format("Точка учета с прибором № %s и теплосистемой № %s не уникальна. Файл: %s",
                            data.deviceSerial, data.tsNumber, data.fileName));
				continue;
			}

			final Tuple row = checkRows.get(0);

			if (!Boolean.TRUE.equals(DBRowUtil.asBoolean(row.get("isManualLoading")))) {
				fileNameErrorDesc.add(String.format(
						"Точка учета с прибором № %s и теплосистемой № %s не поддерживают ипорт данных из файла. Файл: %s",
                    data.deviceSerial, data.tsNumber, data.fileName));
				continue;
			}

			filenameDBInfos.put(data.fileName, row);

		}

		Collection<Long> loadContZPoints = filenameDBInfos.values().stream()
				.map(i -> DBRowUtil.asLong(i.get("contZPointId"))).collect(Collectors.toSet());

		Collection<Long> checkDataSourceIds = filenameDBInfos.values().stream()
				.map(i -> DBRowUtil.asLong(i.get("subscrDataSourceId"))).collect(Collectors.toSet());


        ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContZPointId = accessUtil.checkContZPointId(userIds);

        boolean denyAccess =
            loadContZPoints.stream().filter(i -> !checkContZPointId.test(i)).findAny().map(i -> true).orElse(false);

		if (!loadContZPoints.isEmpty() && denyAccess) {
			fileNameErrorDesc.add("Нет доступа к точке учета");
		}

		List<Long> availableDataSourceIds = subscrDataSourceService
				.selectDataSourceIdsBySubscriber(userIds.getSubscriberId());

		if (!checkDataSourceIds.isEmpty() && !ObjectAccessUtil.checkIds(checkDataSourceIds, availableDataSourceIds)) {
			fileNameErrorDesc.add("Нет доступа к источнику данных");
		}

		if (fileNameErrorDesc.size() > 0) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest(fileNameErrorDesc));
		}

		// All conditions is passed

		//SLogSessionTN sLogSessionTN = sLogWriterService.newSessionWebTN(subscriberParam.getSubscrUserId());

		UUID trxId = Generators.timeBasedGenerator().generate();
		//
		//		result.put("sessionTrxId", trxId.toString());

		for (MultipartFile multipartFile : multipartFiles) {

			String fileName = FilenameUtils.getName(multipartFile.getOriginalFilename());

			String internalFilename = webAppPropsService.getSubscriberCsvPath(userIds.getSubscriberId(),
					userIds.getUserId(), trxId.toString().substring(30, 36) + '_' + fileName);

			File inFile = new File(internalFilename);

			try {
				@SuppressWarnings("unused")
				String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
			} catch (IOException e) {
				logger.error("Exception:{}", e);
				return ApiResponse.responseInternalServerError(ApiResult.error(e));
			}

			Tuple row = filenameDBInfos.get(fileName);

			checkState(row != null);

			ServiceDataImportInfo importInfo = new ServiceDataImportInfo(userIds.getSubscriberId(),
					DBRowUtil.asLong(row.get("contObjectId")), DBRowUtil.asLong(row.get("contZPointId")),
					DBRowUtil.asLong(row.get("deviceObjectId")), DBRowUtil.asLong(row.get("subscrDataSourceId")),
                fileName, internalFilename);

			serviceDataImportInfos.add(importInfo);

		}

		contServiceDataHWaterImportService.submitImportTask(userIds.getUserId(), serviceDataImportInfos);

		return ApiResponse.responseOK();

	}


	/**
	 *
	 * @return
	 */
	@GetMapping("/service/out/csv")
    @Timed
	public ResponseEntity<?> getOutCsvDownloadsAvailable() {

		List<File> listFiles = HWatersCsvFileUtils.getOutFiles(webAppPropsService,
				currentSubscriberService.getSubscriberId());

		if (listFiles == null || listFiles.isEmpty()) {
			return ApiResponse.responseNotFound();
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
//	@GetMapping(value = "/service/out/csv/{filename}", produces = "text/csv")
	@RequestMapping(value = "/service/out/csv/{filename}", method = RequestMethod.GET)
    @Timed
	public ResponseEntity<?> getOutCsvDownload(@PathVariable("filename") String filename) {

		logger.debug("Request for downloading file: {}", filename);

		File file = HWatersCsvFileUtils.getOutCsvFile(webAppPropsService, currentSubscriberService.getSubscriberId(),
				filename);

		if (file == null) {
			return ApiResponse.responseBadRequest(ApiResult.build(ApiResultCode.ERR_VALIDATION, "File not found"));
		}

		return ApiResponse.processDownloadFile(file, HWatersCsvService.MEDIA_TYPE_CSV);

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
    @DeleteMapping("/contObjects/{contObjectId}/contZPoints/{contZPointId}/service/{timeDetailType}/csv")
    @Timed
	public ResponseEntity<?> deleteManualDataHWater(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String dateFromStr, @RequestParam("endDate") String dateToStr) {

        ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContObjectId = accessUtil.checkContObjectId (getCurrentPortalUserIds());
        if (!checkContObjectId.test(contObjectId)) {
			return ApiResponse.responseForbidden();
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
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", dateFromStr, dateToStr));
		}

		if (TimeDetailKey.searchKeyname(timeDetailType) == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("TimeDetailKey %s is not supported", timeDetailType));
		}

		//		if (TimeDetailKey.TYPE_1H.getKeyname().equals(timeDetailType)) {
		//			return responseBadRequest(ApiResult.validationError("Data of 1h is not supported for uploading"));
		//		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (BooleanUtils.isNotTrue(contZPoint.getIsManualLoading())) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("ContZPoint is not suported manual loading"));
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

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@GetMapping("/service/hwater/contObjects/serviceTypeInfo")
    @Timed
	public ResponseEntity<?> getContObjectsServiceTypeInfo(@RequestParam("dateFrom") String dateFromStr,
			@RequestParam("dateTo") String dateToStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters dateFrom:%s and dateTo:%s", dateFromStr, dateToStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters dateFrom:%s is greater than dateTo:%s", dateFromStr, dateToStr));
		}

		List<CityContObjectsServiceTypeInfo> resultList = contObjectHWaterDeltaService
				.getAllCityMapContObjectsServiceTypeInfo(getCurrentPortalUserIds().getSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param dateFromStr
	 * @param dateToStr
	 * @return
	 */
	@GetMapping("/service/hwater/contObjects/serviceTypeInfo/{contObjectId}")
    @Timed
	public ResponseEntity<?> getContObjectsServiceTypeInfoContObject(@PathVariable("contObjectId") long contObjectId,
			@RequestParam("dateFrom") String dateFromStr, @RequestParam("dateTo") String dateToStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);



        ObjectAccessUtil accessUtil = objectAccessService.objectAccessUtil();
        Predicate<Long> checkContObjectId = accessUtil.checkContObjectId (getCurrentPortalUserIds());
        if (!checkContObjectId.test(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkDatePeriodResponse = ApiResponse.checkDatePeriodArguments(datePeriodParser);
		if (checkDatePeriodResponse != null) {
			return checkDatePeriodResponse;
		}

		List<ContObjectServiceTypeInfo> contObjectServiceTypeInfos = contObjectHWaterDeltaService
				.getContObjectServiceTypeInfo(getCurrentPortalUserIds().getSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), contObjectId);

		return ApiResponse.responseOK(contObjectServiceTypeInfos.isEmpty() ? null : contObjectServiceTypeInfos.get(0));
	}

    /**
     *
     * @param dateFromStr
     * @param dateToStr
     * @param cityFiasStr
     * @return
     */
    @GetMapping("/service/hwater/contObjects/serviceTypeInfo/city")
    @Timed
	public ResponseEntity<?> getContObjectsServiceTypeInfoCity(@RequestParam("dateFrom") String dateFromStr,
			@RequestParam("dateTo") String dateToStr, @RequestParam("cityFias") String cityFiasStr) {

		checkNotNull(dateFromStr);
		checkNotNull(dateToStr);
		checkNotNull(cityFiasStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(dateFromStr, dateToStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkDatePeriodResponse = ApiResponse.checkDatePeriodArguments(datePeriodParser);
		if (checkDatePeriodResponse != null) {
			return checkDatePeriodResponse;
		}

		UUID cityFiasUUID = null;
		try {
			cityFiasUUID = UUID.fromString(cityFiasStr);
		} catch (Exception e) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("cityFias is not valid UUID"));
		}

		List<CityContObjectsServiceTypeInfo> resultList = contObjectHWaterDeltaService
				.getOneCityMapContObjectsServiceTypeInfo(getCurrentPortalUserIds().getSubscriberId(),
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), cityFiasUUID);

		return ApiResponse.responseOK(resultList);
	}

    /**
     *
     * @return
     */
    protected PortalUserIds getCurrentPortalUserIds() {

        return portalUserIdsService.getCurrentIds();
    }



}
