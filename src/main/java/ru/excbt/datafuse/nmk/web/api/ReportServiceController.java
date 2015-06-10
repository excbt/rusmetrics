package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController extends WebApiController {

	private interface ReportMaker {
		boolean makeReport(ReportParamset reportParamset,
				LocalDateTime dateTime, OutputStream outputStream);

		String mimeType();

		String defaultFileName();

		String ext();
	}

	private abstract class AbstractReportMaker implements ReportMaker {
		public abstract boolean isZip();

		@Override
		public boolean makeReport(ReportParamset reportParamset,
				LocalDateTime dateTime, OutputStream outputStream) {
			checkNotNull(reportParamset);
			checkNotNull(reportParamset.getSubscriber());
			checkNotNull(outputStream);
			checkNotNull(dateTime);
			boolean result = true;

			reportService.makeReportByParamset(reportParamset, dateTime,
					outputStream, isZip());

			return result;
		}
	}

	private abstract class ZipReportMaker extends AbstractReportMaker {

		@Override
		public boolean isZip() {
			return true;
		};

		@Override
		public String mimeType() {
			return MIME_ZIP;
		}

		@Override
		public String ext() {
			return EXT_ZIP;
		}
	}

	private abstract class PdfReportMaker extends AbstractReportMaker {
		@Override
		public boolean isZip() {
			return false;
		};

		@Override
		public String mimeType() {
			return MIME_PDF;
		}

		@Override
		public String ext() {
			return EXT_PDF;
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
	private final static String EXT_ZIP = ".zip";
	private final static String EXT_PDF = ".pdf";

	@Autowired
	private ReportService reportService;

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
	public void doDowndloadCommerceReportDefaultZip(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = commerceReportMaker();

		proceccDowndloadRequestReport(reportParamsetId, reportMaker, request,
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
	public void doDowndloadEventReportDefaultZip(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = eventReportMaker();

		proceccDowndloadRequestReport(reportParamsetId, reportMaker, request,
				response);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void proceccDowndloadRequestReport(ReportParamset reportParamset,
			ReportMaker reportMaker, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		checkNotNull(reportMaker);
		checkNotNull(reportMaker.mimeType());
		checkNotNull(reportMaker.defaultFileName());
		checkNotNull(reportMaker.ext());

		if (reportParamset == null || reportParamset.getSubscriber() == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		byte[] byteArray = null;
		try (ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream()) {
			reportMaker.makeReport(reportParamset, LocalDateTime.now(),
					memoryOutputStream);
			byteArray = memoryOutputStream.toByteArray();
		}

		if (byteArray == null || byteArray.length == 0) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// set content attributes for the response
		response.setContentType(reportMaker.mimeType());
		response.setContentLength(byteArray.length);

		String outputFilename = reportParamset.getOutputFileNameTemplate();
		if (outputFilename == null) {
			outputFilename = reportMaker.defaultFileName();
		}

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				outputFilename + reportMaker.ext());
		response.setHeader(headerKey, headerValue);
		//
		OutputStream outStream = response.getOutputStream();
		outStream.write(byteArray);
		outStream.close();
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param reportMaker
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void proceccDowndloadRequestReport(long reportParamsetId,
			ReportMaker reportMaker, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		proceccDowndloadRequestReport(reportParamset, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadConsT1ReportDefaultPdf(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		proceccDowndloadRequestReport(reportParamsetId, reportMaker, request,
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
	public void doDowndloadConsT2ReportDefaultPdf(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT2ReportMaker();

		proceccDowndloadRequestReport(reportParamsetId, reportMaker, request,
				response);

	}

	/**
	 * 
	 * @return
	 */
	private ReportMaker eventReportMaker() {
		return new ZipReportMaker() {

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
		return new ZipReportMaker() {

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
		return new PdfReportMaker() {

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
		return new PdfReportMaker() {

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

		ReportMaker reportMaker = commerceReportMaker();

		reportParamset.setSubscriberId(currentSubscriberService
				.getSubscriberId());
		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());

		proceccDowndloadRequestReport(reportParamset, reportMaker, request,
				response);

	}

}
