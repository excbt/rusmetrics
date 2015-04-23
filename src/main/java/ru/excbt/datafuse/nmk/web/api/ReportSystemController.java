package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.service.ReportSystemService;

@Controller
@RequestMapping(value = "/api/reportSystem")
public class ReportSystemController extends WebApiController {

	@Autowired
	private ReportSystemService reportSystemService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/columnSettings/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleActive() {
		ReportColumnSettings result = reportSystemService
				.getReportColumnSettings();
		return ResponseEntity.ok(result);
	}

}
