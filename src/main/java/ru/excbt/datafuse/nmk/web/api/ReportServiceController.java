package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportMakerParamService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportPeriodService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

/**
 * Контроллер для работы с отчетами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController extends WebApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReportServiceController.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
		boolean makeReport(ReportMakerParam reportMakerParam, LocalDateTime dateTime, OutputStream outputStream);

		String defaultFileName();

	}

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	private abstract class AbstractReportMaker implements ReportMaker {

		@Override
		public boolean makeReport(ReportMakerParam reportMakerParam, LocalDateTime dateTime,
				OutputStream outputStream) {
			checkNotNull(reportMakerParam);
			checkState(reportMakerParam.isSubscriberValid());
			checkState(reportMakerParam.isParamsetValid());
			checkNotNull(outputStream);
			checkNotNull(dateTime);
			boolean result = true;

			reportService.makeReportByParamset(reportMakerParam, dateTime, outputStream);

			return result;
		}
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}/download", method = RequestMethod.GET)
	public ResponseEntity<?> doDowndloadAnyReport(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId, HttpServletRequest request) throws IOException {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		return processDowndloadAnyReport(reportMakerParam, reportMaker);

	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}/preview", method = RequestMethod.GET)
	public ResponseEntity<?> doDowndloadAnyReportPreview(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId) throws IOException {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId, true);

		return processDowndloadAnyReport(reportMakerParam, reportMaker);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}/download", method = RequestMethod.PUT)
	public ResponseEntity<?> doPutDowndloadAnyReport(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset, contObjectIds);

		return processDowndloadAnyReport(reportMakerParam, reportMaker);

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
	@RequestMapping(value = "/{reportParamsetId}/download/zip", method = RequestMethod.PUT,
			produces = "application/zip")
	public ResponseEntity<?> doDowndloadReportPutZip(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(true);

		return procedDownloadAllReports(reportParamsetId, contObjectIds, reportParamset);

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
	public ResponseEntity<?> doDowndloadReportPutPdf(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.PDF);

		return procedDownloadAllReports(reportParamsetId, contObjectIds, reportParamset);

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
	public ResponseEntity<?> doDowndloadReportPutXlsx(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.XLSX);

		return procedDownloadAllReports(reportParamsetId, contObjectIds, reportParamset);

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
	public ResponseEntity<?> doDowndloadReportPutHtml(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.HTML);

		return procedDownloadAllReports(reportParamsetId, contObjectIds, reportParamset);

	}

	/**
	 * 
	 * @param reportMakerParam
	 * @param reportMaker
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private ResponseEntity<?> processDowndloadAnyReport(ReportMakerParam reportMakerParam, ReportMaker reportMaker)
			throws IOException {

		checkNotNull(reportMakerParam);
		checkNotNull(reportMaker);
		checkNotNull(reportMaker.defaultFileName());

		String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(reportMakerParam);

		logger.debug("ReportMakerParam JSON: {}", paramJson);

		if (!reportMakerParam.isParamsetValid() || !reportMakerParam.isSubscriberValid()) {
			return responseBadRequest(ApiResult.validationError("Report Maker Param is not valid"));
		}

		boolean checkParamsCommon = reportMakerParamService.isAllCommonRequiredParamsExists(reportMakerParam);
		boolean checParamsSpecial = reportMakerParamService.isAllSpecialRequiredParamsExists(reportMakerParam);

		if (!checkParamsCommon) {
			return responseBadRequest(ApiResult.validationError("Report Maker Param: Common params is not valid"));
		}

		if (!checParamsSpecial) {
			return responseBadRequest(ApiResult.validationError("Report Maker Param: Special params is not valid"));
		}

		byte[] byteArray = null;
		// XXX Time Zone Service
		try (ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream()) {
			reportMaker.makeReport(reportMakerParam, currentSubscriberService.getSubscriberCurrentTime_Joda(),
					memoryOutputStream);
			byteArray = memoryOutputStream.toByteArray();
		}

		if (byteArray == null || byteArray.length == 0) {
			return responseInternalServerError(ApiResult.internalError("Report Maker result is empty"));
		}

		InputStream is = new ByteArrayInputStream(byteArray);

		MediaType mediaType = MediaType.valueOf(reportMakerParam.getMimeType());

		String outputFilename = reportMakerParam.getReportParamset().getOutputFileNameTemplate();

		if (outputFilename == null) {
			outputFilename = reportMaker.defaultFileName();
		}

		boolean makeAttach = !reportMakerParam.isPreviewMode();

		return processDownloadInputStream(is, mediaType, byteArray.length, outputFilename, makeAttach);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	// private ResponseEntity<byte[]> processDowndloadRequestReportFix(
	// ReportMakerParam reportMakerParam, ReportMaker reportMaker,
	// HttpServletRequest request) throws IOException {
	//
	// checkNotNull(reportMakerParam);
	// checkNotNull(reportMaker);
	// checkNotNull(reportMaker.defaultFileName());
	//
	// byte[] result = new byte[0];
	//
	// String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
	// .writeValueAsString(reportMakerParam);
	//
	// logger.debug("ReportMakerParam JSON: {}", paramJson);
	//
	// if (!reportMakerParam.isParamsetValid()
	// || !reportMakerParam.isSubscriberValid()) {
	// return ResponseEntity.badRequest().body(result);
	// }
	//
	// boolean checkParamsCommon = reportMakerParamService
	// .isAllCommonRequiredParamsExists(reportMakerParam);
	// boolean checParamsSpecial = reportMakerParamService
	// .isAllSpecialRequiredParamsExists(reportMakerParam);
	//
	// if (!checkParamsCommon || !checParamsSpecial) {
	// return ResponseEntity.badRequest().body(result);
	// }
	//
	// byte[] byteArray = null;
	// // XXX Time Zone Service
	// try (ByteArrayOutputStream memoryOutputStream = new
	// ByteArrayOutputStream()) {
	// reportMaker.makeReport(reportMakerParam,
	// currentSubscriberService.getSubscriberCurrentTime_Joda(),
	// memoryOutputStream);
	// byteArray = memoryOutputStream.toByteArray();
	// }
	//
	// if (byteArray == null || byteArray.length == 0) {
	// return ResponseEntity.status(
	// HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(result);
	// }
	//
	// // set content attributes for the response
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.valueOf(reportMakerParam.getMimeType()));
	// headers.setContentLength(byteArray.length);
	//
	// String outputFilename = reportMakerParam.getReportParamset()
	// .getOutputFileNameTemplate();
	// if (outputFilename == null) {
	// outputFilename = reportMaker.defaultFileName();
	// }
	//
	// if (!reportMakerParam.isPreviewMode()) {
	// // set headers for the response
	// String headerKey = "Content-Disposition";
	// String headerValue = String.format("attachment; filename=\"%s\"",
	// outputFilename + reportMakerParam.getExt());
	// headers.set(headerKey, headerValue);
	// }
	//
	// logger.debug("Report Result file size: {} bytes", byteArray.length);
	//
	// result = byteArray;
	//
	// return new ResponseEntity<byte[]>(result, headers, HttpStatus.OK);
	//
	// }

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	private ReportMaker anyReportMaker(ReportTypeKey reportTypeKey) {
		return newAbstractReportMaker(reportTypeKey.getDefaultFileName());
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
	 * @param reportParamset
	 */
	private void setupReportParamset(ReportParamset reportParamset) {
		checkNotNull(reportParamset);

		Subscriber subscriber = currentSubscriberService.getSubscriber();
		checkNotNull(subscriber);
		checkNotNull(subscriber.getId());

		reportParamset.setSubscriberId(subscriber.getId());
		reportParamset.setSubscriber(subscriber);

		reportParamset.setReportPeriod(reportPeriodService.findByKeyname(reportParamset.getReportPeriodKey()));
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
	private ResponseEntity<?> procedDownloadAllReports(Long reportParamsetId, Long[] contObjectIds,
			ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(reportParamset.getReportTemplate().getReportTypeKeyname());

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset, contObjectIds);

		String reportKeyname = reportParamset.getReportTemplate().getReportTypeKeyname();
		ReportTypeKey reportTypeKey = ReportTypeKey.valueOf(reportKeyname);

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		checkState(reportMaker != null, "reportTypeKey:" + reportTypeKey + " is not supported");

		return processDowndloadAnyReport(reportMakerParam, reportMaker);

	}

}
