package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.model.ReportWizardParam;
import ru.excbt.datafuse.nmk.report.service.ReportWizardService;

@Controller
@RequestMapping(value = "/api/reportWizard")
public class ReportWizardController extends WebApiController {

	@Autowired
	private ReportWizardService reportWizardService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/columnSettings/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleActive() {
		ReportColumnSettings result = reportWizardService
				.getReportColumnSettings();
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createWizardCommerceReport(
			@RequestBody ReportWizardParam reportWizardParam) {

		checkNotNull(reportWizardParam);
		checkNotNull(reportWizardParam.getReportTemplate());
		checkNotNull(reportWizardParam.getReportColumnSettings());
		
		if (!reportWizardParam.getReportTemplate().isNew()) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.accepted().build();
	}	
	
}
