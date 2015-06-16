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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportMakerParamService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController extends WebApiController {

	private interface ReportMaker {
		boolean makeReport(ReportMakerParam reportMakerParam,
				LocalDateTime dateTime, OutputStream outputStream);

		String defaultFileName();

	}

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

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceController.class);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	private final static String DEFAULT_COMMERCE_FILENAME = "commerceReport";
	private final static String DEFAULT_CONS_T1_FILENAME = "cont_T2_Report";
	private final static String DEFAULT_CONS_T2_FILENAME = "cons_T1_Report";
	private final static String DEFAULT_EVENT_FILENAME = "eventReport";

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadCommerceReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = commerceReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/event/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadEventReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = eventReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/event/{reportParamsetId}/preview", method = RequestMethod.GET)
	public void doDowndloadEventReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = eventReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId, true);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);
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

		checkNotNull(reportMaker);
		checkNotNull(reportMaker.defaultFileName());

		if (!reportMakerParam.isParamsetValid()
				|| !reportMakerParam.isSubscriberValid()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
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
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadConsT1ReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/preview", method = RequestMethod.GET)
	public void doDowndloadConsT1ReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId, true);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadConsT2ReportGet(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT2ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}/preview", method = RequestMethod.GET)
	public void doDowndloadConsT2ReportGetPreview(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT2ReportMaker();

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamsetId, true);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker eventReportMaker() {
		return new AbstractReportMaker() {

			@Override
			public String defaultFileName() {
				return DEFAULT_EVENT_FILENAME;
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker commerceReportMaker() {
		return new AbstractReportMaker() {

			@Override
			public String defaultFileName() {
				return DEFAULT_COMMERCE_FILENAME;
			}

		};
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consT1ReportMaker() {
		return new AbstractReportMaker() {

			@Override
			public String defaultFileName() {
				return DEFAULT_CONS_T1_FILENAME;
			}

		};
	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker consT2ReportMaker() {
		return new AbstractReportMaker() {

			@Override
			public String defaultFileName() {
				return DEFAULT_CONS_T2_FILENAME;
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
	public void doDowndloadCommerceReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

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
				.getReportMakerParam(reportParamset, contObjectIds);

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

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
	public void doDowndloadEventReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = eventReportMaker();

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);
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
	public void doDowndloadConsT1ReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consT1ReportMaker();

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

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
	public void doDowndloadConsT2ReportPut(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		if (contObjectIds == null) {
			logger.warn("Attention: contObjectIds is null");
		}

		setupReportParamset(reportParamset);

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(reportParamset, contObjectIds);

		ReportMaker reportMaker = consT2ReportMaker();

		processDowndloadRequestReport(reportMakerParam, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamset
	 */
	private void setupReportParamset(ReportParamset reportParamset) {
		checkNotNull(reportParamset);
		reportParamset.setSubscriberId(currentSubscriberService
				.getSubscriberId());
		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());
	}
}
