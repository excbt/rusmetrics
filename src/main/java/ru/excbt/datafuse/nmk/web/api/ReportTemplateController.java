package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportTemplate")
public class ReportTemplateController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportTemplateController.class);

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
	@RequestMapping(value = "/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCommerceReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.COMMERCE_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.CONS_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getEventReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.EVENT_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchiveCommerceReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.COMMERCE_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchiveConsReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.CONS_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchiveEventReportTemplates() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.EVENT_REPORT, false);
		return ResponseEntity.ok(result);
	}


	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCommerceOne(@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

		ReportTemplate result = reportTemplateService.findOne(reportTemplateId);
		if (result == null) {
			ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}	

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsOne(@PathVariable(value = "reportTemplateId") Long reportTemplateId) {
		
		ReportTemplate result = reportTemplateService.findOne(reportTemplateId);
		if (result == null) {
			ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}	

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getEventOne(@PathVariable(value = "reportTemplateId") Long reportTemplateId) {
		
		ReportTemplate result = reportTemplateService.findOne(reportTemplateId);
		if (result == null) {
			ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result);
	}	
	
	
	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @param reportType
	 * @return
	 */
	public ResponseEntity<?> updateInternal(Long reportTemplateId,
			ReportTemplate reportTemplate, ReportTypeKeys reportType) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportTemplate);
		checkNotNull(reportTemplate.getId());
		checkNotNull(reportType);
		checkArgument(reportTemplate.getId().equals(reportTemplateId));
		checkArgument(reportTemplate.getReportType() == reportType);

		reportTemplate.setSubscriber(currentSubscriberService.getSubscriber());

		ReportTemplate resultEntity = null;
		try {
			resultEntity = reportTemplateService.updateOne(reportTemplate);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity ReportTemplate (id={}): {}",
					reportTemplateId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateCommerceOne(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKeys.COMMERCE_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateConsOne(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKeys.CONS_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/event/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateEventOne(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKeys.EVENT_REPORT);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/move", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> moveToArchive(
			@RequestParam(value = "{reportTemplateId}", required = true) Long reportTemplateId) {

		checkNotNull(reportTemplateId);

		ReportTemplate resultEntity = null;

		try {
			resultEntity = reportTemplateService
					.moveToArchive(reportTemplateId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity ReportTemplate (id={}): {}",
					reportTemplateId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		if (resultEntity == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/createByTemplate/{srcReportTemplateId}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(
			@PathVariable(value = "srcReportTemplateId") Long srcReportTemplateId,
			@RequestBody ReportTemplate reportTemplate,
			HttpServletRequest request) {

		checkNotNull(srcReportTemplateId);
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ReportTemplate resultEntity = null;

		try {
			resultEntity = reportTemplateService.createByReportTemplate(
					srcReportTemplateId, reportTemplate);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity by ReportTemplate (id={}): {}",
					srcReportTemplateId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create(request.getServletPath()
				+ getReportTypeURL(resultEntity.getReportType()) + "/"
				+ +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
	}

	/**
	 * 
	 * @param rtk
	 * @return
	 */
	private String getReportTypeURL(ReportTypeKeys rtk) {
		String result = null;
		switch (rtk) {
		case CONS_REPORT: {
			result = "/cons";
			break;
		}
		case COMMERCE_REPORT: {
			result = "/commerce";
			break;
		}
		case EVENT_REPORT: {
			result = "/event";
			break;
		}
		default:
			result = "";
			break;
		}
		return result;
	}
}
