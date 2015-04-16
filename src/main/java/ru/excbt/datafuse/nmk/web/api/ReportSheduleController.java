package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportShedule")
public class ReportSheduleController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportSheduleController.class);

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/active", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleActive() {
		List<ReportShedule> result = reportSheduleService.selectReportShedule(
				DateTime.now(), currentSubscriberService.getSubscriberId());
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleOne(
			@PathVariable(value = "reportSheduleId") Long reportSheduleId) {
		ReportShedule result = reportSheduleService.findOne(reportSheduleId);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportShedule(
			@PathVariable(value = "reportSheduleId") Long reportSheduleId) {
		reportSheduleService.deleteOne(reportSheduleId);
		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneShedule(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "reportParamsetId", required = true) Long reportParamsetId,
			@RequestBody ReportShedule reportShedule, HttpServletRequest request) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamsetId);
		checkNotNull(reportShedule);
		checkArgument(reportShedule.isNew());

		ReportParamset checkParamset = reportParamsetService
				.findOne(reportParamsetId);

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body(
					"ReportParamset is not found");
		}

		if (!reportTemplateId.equals(checkParamset.getReportTemplate().getId())) {
			return ResponseEntity.badRequest().body(
					"Invalid reportTemplateId & reportParamsetId");
		}

		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		reportShedule.setReportParamset(checkParamset);

		ReportShedule resultEntity = null;
		try {
			resultEntity = reportSheduleService.createOne(reportShedule);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save new entity ReportShedule: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create(request.getRequestURI() + "/"
				+ +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @param reportParamset
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneShedule(
			@PathVariable(value = "reportSheduleId") Long reportSheduleId,
			@RequestBody ReportShedule reportShedule) {

		checkNotNull(reportShedule);
		checkArgument(!reportShedule.isNew());

		checkNotNull(reportShedule.getReportTemplate());
		checkNotNull(reportShedule.getReportParamset());

		ReportShedule checkReportShedule = reportSheduleService
				.findOne(reportSheduleId);
		if (checkReportShedule == null) {
			return ResponseEntity.badRequest().body(
					"ReportShedule is not found");
		}

		if (!checkReportShedule.getSubscriberId().equals(
				currentSubscriberService.getSubscriberId())) {
			return ResponseEntity.badRequest().body("Invalid subscriber");
		}

		ReportParamset checkParamset = reportParamsetService
				.findOne(reportShedule.getReportParamset().getId());

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body(
					"ReportParamset is not found");
		}

		if (!reportShedule.getReportTemplate().getId()
				.equals(checkParamset.getReportTemplate().getId())) {
			return ResponseEntity.badRequest().body(
					"Invalid reportTemplateId & reportParamsetId");
		}

		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		reportShedule.setReportParamset(checkParamset);

		ReportShedule resultEntity = null;
		try {
			resultEntity = reportSheduleService.updateOne(reportShedule);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity ReportShedule: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

}
