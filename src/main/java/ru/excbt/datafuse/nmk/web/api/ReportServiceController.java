package ru.excbt.datafuse.nmk.web.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;

@Controller
@RequestMapping(value = "/api/reportService")
public class ReportServiceController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceController.class);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	private final static String MIME_ZIP = "application/zip";
	private final static String DEFAULT_COMMERCE_FILENAME = "commerceReport";
	private final static String EXT_ZIP = ".zip";
	
	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportParamsetService reportParamsetService;

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

	/**
	 * 
	 * @param contObjectId
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/commerce/{contObjectId}/html", method = RequestMethod.GET)
	public Object commerceReportHtml(
			@PathVariable("contObjectId") long contObjectId,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		logger.trace("Fire commercialReportHtml");

		return processRequest(ReportOutputFileType.HTML, contObjectId,
				beginDateS, endDateS);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/commerce/{contObjectId}/pdf", method = RequestMethod.GET)
	public Object commerceReportPdf(
			@PathVariable("contObjectId") long contObjectId,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		logger.trace("Fire commercialReportPdf");

		return processRequest(ReportOutputFileType.PDF, contObjectId,
				beginDateS, endDateS);
	}

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

		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		if (reportParamset == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
		reportService.makeCommerceReportZip(reportParamsetId,
				LocalDateTime.now(), memoryOutputStream);

		byte[] byteArray = memoryOutputStream.toByteArray();

		if (byteArray == null || byteArray.length == 0) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		
		// set content attributes for the response
		response.setContentType(MIME_ZIP); 
		response.setContentLength(byteArray.length);

		String outputFilename = reportParamset.getOutputFileNameTemplate();
		if (outputFilename == null) {
			outputFilename = DEFAULT_COMMERCE_FILENAME;
		} 

		// set headers for the response
		String headerKey = "Content-Disposition"; 
		String headerValue = String.format("attachment; filename=\"%s\"",
				outputFilename + EXT_ZIP);
		response.setHeader(headerKey, headerValue);
		//
		OutputStream outStream = response.getOutputStream();
		outStream.write(byteArray);
		outStream.close();
	}
}
