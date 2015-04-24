package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportWizardService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.model.ReportWizardParam;

@Controller
@RequestMapping(value = "/api/reportWizard")
public class ReportWizardController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportWizardController.class);

	@Autowired
	private ReportWizardService reportWizardService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

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

		ReportTemplate resultEntity = null;
		try {
			resultEntity = reportWizardService.createCommerceWizard(
					reportWizardParam.getReportTemplate(),
					reportWizardParam.getReportColumnSettings(),
					currentSubscriberService.getSubscriber());

		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during Create WIzardCommerceReport: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

}
