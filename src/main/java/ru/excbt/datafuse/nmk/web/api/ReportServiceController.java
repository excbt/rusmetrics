package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportMakerParamService;
import ru.excbt.datafuse.nmk.data.service.ReportPeriodService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

import static com.google.common.base.Preconditions.*;

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
public class ReportServiceController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportServiceController.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

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
	public ResponseEntity<?> downloadAnyReportGet(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId, HttpServletRequest request) throws IOException {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
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
	public ResponseEntity<?> downloadAnyReportPreview(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId) throws IOException {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParamPreview(reportParamsetId);

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
	public ResponseEntity<?> downloadAnyReportCustom(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
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
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		setupReportParamset(reportParamset);

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset,
				fixContObjectIds);

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
	//	@RequestMapping(value = "/{reportParamsetId}/download/zip", method = RequestMethod.PUT,
	//			produces = "application/zip")
	protected ResponseEntity<?> doDowndloadReportPutZip(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(true);

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return initDownloadProcessAllReports(reportParamsetId, reportParamset, fixContObjectIds);

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
	//@RequestMapping(value = "/{reportParamsetId}/download/pdf", method = RequestMethod.PUT, produces = MIME_PDF)
	protected ResponseEntity<?> doDowndloadReportPutPdf(@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.PDF);

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return initDownloadProcessAllReports(reportParamsetId, reportParamset, fixContObjectIds);

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
	//@RequestMapping(value = "/{reportParamsetId}/download/xlsx", method = RequestMethod.PUT, produces = MIME_XLSX)
	protected ResponseEntity<?> doDowndloadReportPutXlsx(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.XLSX);

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return initDownloadProcessAllReports(reportParamsetId, reportParamset, fixContObjectIds);

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
	//@RequestMapping(value = "/{reportParamsetId}/download/html", method = RequestMethod.PUT, produces = MIME_TEXT)
	protected ResponseEntity<?> doDowndloadReportPutHtml(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);

		reportParamset.setOutputFileZipped(false);
		reportParamset.setOutputFileType(ReportOutputFileType.HTML);

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return initDownloadProcessAllReports(reportParamsetId, reportParamset, fixContObjectIds);

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

		logger.debug("Processing Report with params: {}", paramJson);

		if (!reportMakerParam.isParamsetValid() || !reportMakerParam.isSubscriberValid()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report Maker Param is not valid"));
		}

		boolean checkParamsCommon = reportMakerParam.isAllCommonRequiredParamsExists();
		boolean checParamsSpecial = reportMakerParam.isAllSpecialRequiredParamsExists();

		if (!checkParamsCommon) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report Maker Param: Common params is not valid"));
		}

		if (!checParamsSpecial) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report Maker Param: Special params is not valid"));
		}

		byte[] byteArray = null;
		// XXX Time Zone Service
		try (ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream()) {
			reportMaker.makeReport(reportMakerParam, currentSubscriberService.getSubscriberCurrentTime_Joda(),
					memoryOutputStream);
			byteArray = memoryOutputStream.toByteArray();
		}

		if (byteArray == null || byteArray.length == 0) {
			return ApiResponse.responseInternalServerError(ApiResult.internalError("Report Maker result is empty"));
		}

		InputStream is = new ByteArrayInputStream(byteArray);

		MediaType mediaType = MediaType.valueOf(reportMakerParam.getMimeType());

		String outputFilename = reportMakerParam.getReportParamset().getOutputFileNameTemplate();

		if (outputFilename == null) {
			outputFilename = reportMaker.defaultFileName();
		}

		boolean makeAttach = !reportMakerParam.isPreviewMode();

		return ApiResponse.processDownloadInputStream(is, mediaType, byteArray.length, outputFilename, makeAttach);

	}

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
	private ResponseEntity<?> initDownloadProcessAllReports(Long reportParamsetId, ReportParamset reportParamset,
			Long[] contObjectIds) throws IOException {

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

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param contObjectId
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}/context/{contObjectId}", method = RequestMethod.GET)
	public ResponseEntity<?> downloadAnyReportContextGet(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId, @PathVariable("contObjectId") long contObjectId,
			HttpServletRequest request) throws IOException {
		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		Long[] contObjectIds = new Long[] { contObjectId };

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId,
				contObjectIds);

		return processDowndloadAnyReport(reportMakerParam, reportMaker);

	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}/contextPreview/{contObjectId}",
			method = RequestMethod.GET)
	public ResponseEntity<?> downloadAnyReportContextPreviewGet(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportParamsetId") long reportParamsetId, @PathVariable("contObjectId") long contObjectId)
			throws IOException {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ReportMaker reportMaker = anyReportMaker(reportTypeKey);

		Long[] contObjectIds = new Long[] { contObjectId };

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParamPreview(reportParamsetId,
				contObjectIds);

		return processDowndloadAnyReport(reportMakerParam, reportMaker);
	}

}
