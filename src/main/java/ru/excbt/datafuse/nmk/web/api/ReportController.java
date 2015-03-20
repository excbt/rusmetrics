package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.DateTime;
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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportType;
import ru.excbt.datafuse.nmk.data.service.ReportService;

@Controller
@RequestMapping(value = "/api/report")
public class ReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportController.class);

	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	@Autowired
	private ReportService reportService;

	/**
	 * 
	 * @param reportType
	 * @param contObjectId
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	private Object processRequest(ReportType reportType, long contObjectId,
			String beginDateS, String endDateS) {
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

		String path = reportService.getCommercialReportPath(reportType, contObjectId,
				beginD, endD);

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
	@RequestMapping(value = "/commercial/{contObjectId}/html", method = RequestMethod.GET)
	public Object commercialReportHtml(
			@PathVariable("contObjectId") long contObjectId,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		logger.trace("Fire commercialReportHtml");

		return processRequest(ReportType.HTML, contObjectId, beginDateS,
				endDateS);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDateS
	 * @param endDateS
	 * @return
	 */
	@RequestMapping(value = "/commercial/{contObjectId}/pdf", method = RequestMethod.GET)
	public Object commercialReportPdf(
			@PathVariable("contObjectId") long contObjectId,
			@RequestParam("beginDate") String beginDateS,
			@RequestParam("endDate") String endDateS) {

		logger.trace("Fire commercialReportPdf");

		return processRequest(ReportType.PDF, contObjectId, beginDateS,
				endDateS);
	}

}
