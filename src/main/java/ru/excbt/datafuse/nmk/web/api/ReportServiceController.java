package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController {

	private interface ReportMaker {
		boolean makeReport(long reportParamsetId, LocalDateTime dateTime,
				OutputStream outputStream);

		String mimeType();

		String defaultFileName();

		String ext();
	}
	
	
	private abstract class ZipReportMaker implements ReportMaker {
	
		@Override
		public boolean makeReport(long reportParamsetId,
				LocalDateTime dateTime, OutputStream outputStream) {
			checkArgument(reportParamsetId > 0);
			checkNotNull(outputStream);
			checkNotNull(dateTime);
			boolean result = true;
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					outputStream, Charset.forName("UTF-8"));
			try {
				reportService.makeReport(reportParamsetId,
						currentSubscriberService.getSubscriberId(),
						dateTime, zipOutputStream, true);
			} finally {
				try {
					zipOutputStream.flush();
					zipOutputStream.close();
				} catch (IOException e) {
					result = false;
				}

			}
			return result;
		}
		
		@Override
		public String mimeType() {
			return MIME_ZIP;
		}
		
		@Override
		public String ext() {
			return EXT_ZIP;
		}
	}

	private abstract class PdfReportMaker implements ReportMaker {
		@Override
		public boolean makeReport(long reportParamsetId,
				LocalDateTime dateTime, OutputStream outputStream) {
			checkArgument(reportParamsetId > 0);
			checkNotNull(outputStream);
			checkNotNull(dateTime);
			boolean result = true;
			try {
				reportService.makeReport(reportParamsetId,
						currentSubscriberService.getSubscriberId(),
						dateTime, outputStream, false);
			} finally {
			}
			return result;
		}
		
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

	private final static String MIME_ZIP = "application/zip";
	private final static String MIME_PDF = "application/pdf";
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
	 * @param reportType
	 * @param contObjectId
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	private Object processRequest(ReportOutputFileType reportType,
			long contObjectId, String beginDateS, String endDateS) {
		boolean checkPass = true;

		if (contObjectId <= 0) {
			checkPass = false;
		}

		if (beginDateS == null || endDateS == null) {
			checkPass = false;
		}

		DateTime beginD = null;
		DateTime endD = null;
		try {
			beginD = DATE_FORMATTER.parseDateTime(beginDateS);
			endD = DATE_FORMATTER.parseDateTime(endDateS);
		} catch (Exception e) {
			checkPass = false;
		}

		if (!checkPass) {
			return ResponseEntity.badRequest().build();
		}

		String path = reportService.getCommercialReportPath(reportType,
				contObjectId, beginD, endD);

		StringBuilder sb = new StringBuilder();
		sb.append("redirect:");
		if (reportService.externalJasperServerEnable()) {
			String s = reportService.externalJasperServerUrl();
			logger.trace("externalJasperServerEnable: True. ServerURL: {}", s);
			sb.append(s);
		} else {
			logger.trace("externalJasperServerEnable: False");
		}
		sb.append(path);

		return sb.toString();
	}

//	/**
//	 * 
//	 * @param contObjectId
//	 * @param beginDateS
//	 * @param endDateS
//	 * @return
//	 */
//	@RequestMapping(value = "/commerce/{contObjectId}/html", method = RequestMethod.GET)
//	public Object commerceReportHtml(
//			@PathVariable("contObjectId") long contObjectId,
//			@RequestParam("beginDate") String beginDateS,
//			@RequestParam("endDate") String endDateS) {
//
//		logger.trace("Fire commercialReportHtml");
//
//		return processRequest(ReportOutputFileType.HTML, contObjectId,
//				beginDateS, endDateS);
//	}

//	/**
//	 * 
//	 * @param contObjectId
//	 * @param beginDateS
//	 * @param endDateS
//	 * @return
//	 */
//	@RequestMapping(value = "/commerce/{contObjectId}/pdf", method = RequestMethod.GET)
//	public Object commerceReportPdf(
//			@PathVariable("contObjectId") long contObjectId,
//			@RequestParam("beginDate") String beginDateS,
//			@RequestParam("endDate") String endDateS) {
//
//		logger.trace("Fire commercialReportPdf");
//
//		return processRequest(ReportOutputFileType.PDF, contObjectId,
//				beginDateS, endDateS);
//	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadCommerceReportZip(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = commerceReportMaker();

		doDowndloadInternalReport(reportParamsetId, reportMaker, request,
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
	public void doDowndloadEventReportZip(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		
		ReportMaker reportMaker = eventReportMaker();
		
		doDowndloadInternalReport(reportParamsetId, reportMaker, request,
				response);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void doDowndloadInternalReport(long reportParamsetId,
			ReportMaker reportMaker, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		checkNotNull(reportMaker);
		checkNotNull(reportMaker.mimeType());
		checkNotNull(reportMaker.defaultFileName());
		checkNotNull(reportMaker.ext());

		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		if (reportParamset == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();

		reportMaker.makeReport(reportParamsetId, LocalDateTime.now(),
				memoryOutputStream);

		byte[] byteArray = memoryOutputStream.toByteArray();

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
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}/download", method = RequestMethod.GET)
	public void doDowndloadConsT1ReportPdf(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ReportMaker reportMaker = consT1ReportMaker();

		doDowndloadInternalReport(reportParamsetId, reportMaker, request,
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
	public void doDowndloadConsT2ReportPdf(
			@PathVariable("reportParamsetId") long reportParamsetId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		
		ReportMaker reportMaker = consT2ReportMaker();
		
		doDowndloadInternalReport(reportParamsetId, reportMaker, request,
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
}
