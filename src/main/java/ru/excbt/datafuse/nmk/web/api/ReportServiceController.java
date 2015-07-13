package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportMakerParamService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportPeriodService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceController.class);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final static String DEFAULT_COMMERCE_FILENAME = "commerceReport";
	private final static String DEFAULT_CONS_T1_FILENAME = "cont_T2_Report";
	private final static String DEFAULT_CONS_T2_FILENAME = "cons_T1_Report";
	private final static String DEFAULT_EVENT_FILENAME = "eventReport";
	private final static String DEFAULT_METROLOGICAL_FILENAME = "metrologicalReport";
	private final static String DEFAULT_CONSUMPTION_FILENAME = "consumptionReport";
	private final static String DEFAULT_CONSUMPTION_HISTORY_FILENAME = "consumptionHistoryReport";

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ReportPeriodService reportPeriodService;

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	private interface ReportMaker {
		boolean makeReport(ReportMakerParam reportMakerParam,
				LocalDateTime dateTime, OutputStream outputStream);

		String defaultFileName();

	}

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	private abstract class AbstractReportMaker implements ReportMaker {

		@Override
		public boolean makeReport(ReportMakerParam reportMakerParam,
				LocalDateTime dateTime, OutputStream outputStream) {
			checkNotNull(reportMakerParam);
			checkState(reportMakerParam.isSubscriberValid());
			checkState(reportMakerParam.isParamsetValid());
			checkNotNull(outputStream);
			checkNotNull(dateTime);
			boolean result = true;

			reportService.makeReportByParamset(reportMakerParam, dateTime,
					outputStream);

			return result;
		}
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadCommerceReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = commerceReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/event/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadEventReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = eventReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/event/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadEventReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = eventReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void processDowndloadRequestReport(
			ReportMakerParam reportMakerParam, ReportMaker reportMaker,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportMakerParam);
		checkNotNull(reportMaker);
		checkNotNull(reportMaker.defaultFileName());

		String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
				.writeValueAsString(reportMakerParam);

		logger.trace("ReportMakerParam JSON: {}", paramJson);

		if (!reportMakerParam.isParamsetValid()
				|| !reportMakerParam.isSubscriberValid()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		boolean checkParamsCommon = reportMakerParamService
				.isAllCommonRequiredParamsExists(reportMakerParam);
		boolean checParamsSpecial = reportMakerParamService
				.isAllSpecialRequiredParamsExists(reportMakerParam);

		if (!checkParamsCommon || !checParamsSpecial) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		byte[] byteArray = null;
		try (ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream()) {
			reportMaker.makeReport(reportMakerParam, LocalDateTime.now(),
					memoryOutputStream);
			byteArray = memoryOutputStream.toByteArray();
		}

		if (byteArray == null || byteArray.length == 0) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// set content attributes for the response
		response.setContentType(reportMakerParam.getMimeType());
		response.setContentLength(byteArray.length);
		response.setStatus(HttpServletResponse.SC_OK);

		String outputFilename = reportMakerParam.getReportParamset()
				.getOutputFileNameTemplate();
		if (outputFilename == null) {
			outputFilename = reportMaker.defaultFileName();
		}

		if (!reportMakerParam.isPreviewMode()) {
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					outputFilename + reportMakerParam.getExt());
			response.setHeader(headerKey, headerValue);
		}

		logger.debug("Report Result file size: {} bytes", byteArray.length);

		//
		OutputStream outStream = response.getOutputStream();
		outStream.write(byteArray);
		outStream.close();

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private ResponseEntity<byte[]> processDowndloadRequestReportFix(
			ReportMakerParam reportMakerParam, ReportMaker reportMaker,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportMakerParam);
		checkNotNull(reportMaker);
		checkNotNull(reportMaker.defaultFileName());

		byte[] result = new byte[0];

		String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
				.writeValueAsString(reportMakerParam);

		logger.debug("ReportMakerParam JSON: {}", paramJson);

		if (!reportMakerParam.isParamsetValid()
				|| !reportMakerParam.isSubscriberValid()) {
			return ResponseEntity.badRequest().body(result);
		}

		boolean checkParamsCommon = reportMakerParamService
				.isAllCommonRequiredParamsExists(reportMakerParam);
		boolean checParamsSpecial = reportMakerParamService
				.isAllSpecialRequiredParamsExists(reportMakerParam);

		if (!checkParamsCommon || !checParamsSpecial) {
			return ResponseEntity.badRequest().body(result);
		}

		byte[] byteArray = null;
		// TODO Time Zone Service
		try (ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream()) {
			reportMaker.makeReport(reportMakerParam, LocalDateTime.now(),
					memoryOutputStream);
			byteArray = memoryOutputStream.toByteArray();
		}

		if (byteArray == null || byteArray.length == 0) {
			return ResponseEntity.status(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(result);
		}

		// set content attributes for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(reportMakerParam.getMimeType()));
		headers.setContentLength(byteArray.length);

		String outputFilename = reportMakerParam.getReportParamset()
				.getOutputFileNameTemplate();
		if (outputFilename == null) {
			outputFilename = reportMaker.defaultFileName();
		}

		if (!reportMakerParam.isPreviewMode()) {
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					outputFilename + reportMakerParam.getExt());
			headers.set(headerKey, headerValue);
		}

		logger.debug("Report Result file size: {} bytes", byteArray.length);

		result = byteArray;

		return new ResponseEntity<byte[]>(result, headers, HttpStatus.OK);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsT1ReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsT1ReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsT2ReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consT2ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsT2ReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consT2ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/metrological/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadMetrologicalReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = metrologicalReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/metrological/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadMetrologicalReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = metrologicalReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsumptionReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consumptionReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsumptionReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consumptionReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption_history/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsumptionHistoryReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consumptionHistoryReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption_history/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doDowndloadConsumptionHistoryReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request) throws IOException {

		ReportMaker reportMaker = consumptionHistoryReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamsetId, true);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker eventReportMaker() {
		return newAbstractReportMaker(DEFAULT_EVENT_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker commerceReportMaker() {
		return newAbstractReportMaker(DEFAULT_COMMERCE_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consT1ReportMaker() {
		return newAbstractReportMaker(DEFAULT_CONS_T1_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consT2ReportMaker() {
		return newAbstractReportMaker(DEFAULT_CONS_T2_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker metrologicalReportMaker() {
		return newAbstractReportMaker(DEFAULT_METROLOGICAL_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consumptionReportMaker() {
		return newAbstractReportMaker(DEFAULT_CONSUMPTION_FILENAME);
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consumptionHistoryReportMaker() {
		return newAbstractReportMaker(DEFAULT_CONSUMPTION_HISTORY_FILENAME);
	}

	/**
	 * 
	 * @param defaultFileName
	 * @return
	 */
	private ReportMaker newAbstractReportMaker(final String defaultFileName) {
		return new AbstractReportMaker() {
			@Override
			public String defaultFileName() {
				return defaultFileName;
			}

		};

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadCommerceReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		ReportMaker reportMaker = commerceReportMaker();

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/event/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadEventReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = eventReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadConsT1ReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consT1ReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadConsT2ReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consT2ReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/metrological/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadMetrologicalReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = metrologicalReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadConsumptionReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consumptionReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/consumption_hisotry/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doDowndloadConsumptionHisotryReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consumptionHistoryReportMaker();

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);
	}

	/**
	 * 
	 * @param reportParamset
	 */
	private void setupReportParamset(ReportParamset reportParamset) {
		checkNotNull(reportParamset);

		Subscriber subscriber = currentSubscriberService.getSubscriber();
		checkNotNull(subscriber);
		checkNotNull(subscriber.getId());

		reportParamset.setSubscriberId(subscriber.getId());
		reportParamset.setSubscriber(subscriber);

		reportParamset.setReportPeriod(reportPeriodService
				.findByKeyname(reportParamset.getReportPeriodKey()));
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportParamsetId}/download/zip", method = RequestMethod.PUT, produces = "application/zip")
	public ResponseEntity<byte[]> doDowndloadReportPutZip(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(true);

		return procedDownloadAllReports(reportParamsetId, contObjectIds,
				reportParamset, request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportParamsetId}/download/pdf", method = RequestMethod.PUT, produces = MIME_PDF)
	public ResponseEntity<byte[]> doDowndloadReportPutPdf(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.PDF);

		return procedDownloadAllReports(reportParamsetId, contObjectIds,
				reportParamset, request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportParamsetId}/download/xlsx", method = RequestMethod.PUT, produces = MIME_XLSX)
	public ResponseEntity<byte[]> doDowndloadReportPutXlsx(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.XLSX);

		return procedDownloadAllReports(reportParamsetId, contObjectIds,
				reportParamset, request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportParamsetId}/download/html", method = RequestMethod.PUT, produces = MIME_TEXT)
	public ResponseEntity<byte[]> doDowndloadReportPutHtml(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.HTML);

		return procedDownloadAllReports(reportParamsetId, contObjectIds,
				reportParamset, request);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private ResponseEntity<byte[]> procedDownloadAllReports(
			Long reportParamsetId, Long[] contObjectIds,
			ReportParamset reportParamset, HttpServletRequest request)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(reportParamset.getReportTemplate().getReportTypeKey());

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.newReportMakerParam(reportParamset, contObjectIds);

		ReportTypeKey reportTypeKey = reportParamset.getReportTemplate()
				.getReportTypeKey();

		ReportMaker reportMaker = null;

		switch (reportTypeKey) {
		case COMMERCE_REPORT:
			reportMaker = commerceReportMaker();
			break;
		case EVENT_REPORT:
			reportMaker = eventReportMaker();
			break;
		case CONS_T1_REPORT:
			reportMaker = consT1ReportMaker();
			break;
		case CONS_T2_REPORT:
			reportMaker = consT2ReportMaker();
			break;

		default:
			break;
		}

		checkState(reportMaker != null, "reportTypeKey:" + reportTypeKey
				+ " is not supported");

		return processDowndloadRequestReportFix(reportMakerParam, reportMaker,
				request);

	}

}
