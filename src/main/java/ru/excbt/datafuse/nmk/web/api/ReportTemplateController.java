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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.ReportWizardService;
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

	@Autowired
	private ReportWizardService reportSystemService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesCommerce() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.COMMERCE_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t1", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesConsT1() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T1_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t2", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesConsT2() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T2_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesConsOld() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T2_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesEvent() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.EVENT_REPORT, true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesArchiveCommerce() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.COMMERCE_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t1", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesArchiveConsT1() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T1_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t2", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesArchiveConsT2() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T2_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesArchiveConsOld() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.CONS_T2_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesArchiveEvent() {
		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.EVENT_REPORT, false);
		return ResponseEntity.ok(result);
	}

	/** */

	private void deleteInternal(long reportTemplateId) {
		reportTemplateService.deleteOne(reportTemplateId);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce/{reportTemplateId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportTemplatesArchiveCommerce(
			@RequestParam("reportTemplateId") long reportTemplateId) {

		deleteInternal(reportTemplateId);
		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t1", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportTemplatesArchiveConsT1(
			@RequestParam("reportTemplateId") long reportTemplateId) {

		deleteInternal(reportTemplateId);
		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t2", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportTemplatesArchiveConsT2(
			@RequestParam("reportTemplateId") long reportTemplateId) {
		
		deleteInternal(reportTemplateId);
		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportTemplatesArchiveConsOld(
			@RequestParam("reportTemplateId") long reportTemplateId) {
		
		deleteInternal(reportTemplateId);
		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportTemplatesArchiveEvent(
			@RequestParam("reportTemplateId") long reportTemplateId) {

		deleteInternal(reportTemplateId);
		return ResponseEntity.accepted().build();
	}

	/** */
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneCommerce(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

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
	@RequestMapping(value = "/cons_t1/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneConsT1(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

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
	@RequestMapping(value = "/cons_t2/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneConsT2(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

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
	public ResponseEntity<?> getOneConsOld(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

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
	public ResponseEntity<?> getOneEvent(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

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
	private ResponseEntity<?> updateInternal(Long reportTemplateId,
			ReportTemplate reportTemplate, ReportTypeKey reportType) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportTemplate);
		checkNotNull(reportTemplate.getId());
		checkNotNull(reportType);
		checkArgument(reportTemplate.getId().equals(reportTemplateId));
		checkArgument(reportTemplate.getReportTypeKey() == reportType);

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
	public ResponseEntity<?> updateOneCommerce(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKey.COMMERCE_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t1/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneConsT1(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKey.CONS_T1_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t2/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneConsT2(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKey.CONS_T2_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneConsOld(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKey.CONS_T2_REPORT);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/event/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneEvent(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {
		return updateInternal(reportTemplateId, reportTemplate,
				ReportTypeKey.EVENT_REPORT);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/move", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> moveToArchive(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId) {

		checkNotNull(reportTemplateId);

		ReportTemplate resultEntity = null;

		try {
			resultEntity = reportTemplateService
					.moveToArchive(reportTemplateId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during move to archive entity ReportTemplate (id={}): {}",
					reportTemplateId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		if (resultEntity == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/createByTemplate/{srcId}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(
			@PathVariable(value = "srcId") Long srcId,
			@RequestBody ReportTemplate reportTemplate,
			HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ReportTemplate resultEntity = null;

		try {
			resultEntity = reportTemplateService.createByTemplate(srcId,
					reportTemplate, currentSubscriberService.getSubscriber());
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity by ReportTemplate (id={}): {}",
					srcId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create("/api/reportTemplate"
				+ ReportConstants.getReportTypeURL(resultEntity
						.getReportTypeKey()) + "/" + +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
	}

}
