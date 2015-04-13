package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportSettings")
public class ReportSettingsController extends WebApiController {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ReportSettingsController.class);
	
	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportTypeRepository reportTypeRepository;

	@Autowired
	private ReportPeriodRepository reportPeriodRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportType", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTypeJson() {
		return ResponseEntity.ok(reportTypeRepository.findAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportPeriod", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportPeriodJson() {
		return ResponseEntity.ok(reportPeriodRepository.findAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportTemplate/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberCommerceReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.COMMERCE_REPORT, DateTime.now());
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/reportTemplate/commerce/{reportTemplareId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(
			@PathVariable(value = "reportTemplareId") Long reportTemplareId,
			@RequestBody ReportTemplate reportTemplate) {

		checkNotNull(reportTemplareId);
		checkNotNull(reportTemplate);
		checkNotNull(reportTemplate.getId());
		checkArgument(reportTemplate.getId().equals(reportTemplareId));
		checkArgument(reportTemplate.getReportType() == ReportTypeKeys.COMMERCE_REPORT);
		
		reportTemplate.setSubscriber(currentSubscriberService.getSubscriber());
		
		ReportTemplate resultEntity = null;
		try {
			resultEntity = reportTemplateService.updateOne(reportTemplate);	
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity ReportTemplate (id={}): {}", reportTemplareId,
					e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		

		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportTemplate/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberConsReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.CONS_REPORT, DateTime.now());
		return ResponseEntity.ok(result);
	}
	
	
}
