package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

/**
 * Контроллер для работы с расписанием отчетов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportShedule")
public class ReportSheduleController extends WebApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReportSheduleController.class);

	@Autowired
	private ReportSheduleService reportSheduleService;

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
		LocalDateTime nowDate = LocalDateTime.now().withMillisOfDay(0);
		List<ReportShedule> result = reportSheduleService
				.selectReportShedule(currentSubscriberService.getSubscriberId(), nowDate);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportShedule() {
		List<ReportShedule> result = reportSheduleService
				.selectReportShedule(currentSubscriberService.getSubscriberId());
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleOne(@PathVariable(value = "reportSheduleId") Long reportSheduleId) {
		ReportShedule result = reportSheduleService.findOne(reportSheduleId);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportShedule(@PathVariable(value = "reportSheduleId") final Long reportSheduleId) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				reportSheduleService.deleteOne(reportSheduleId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
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

		ReportParamset checkParamset = reportParamsetService.findOne(reportParamsetId);

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body("ReportParamset is not found");
		}

		if (!reportTemplateId.equals(checkParamset.getReportTemplate().getId())) {
			return ResponseEntity.badRequest().body("Invalid reportTemplateId & reportParamsetId");
		}

		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());

		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		reportShedule.setReportParamset(checkParamset);

		ApiActionLocation action = new EntityApiActionLocationAdapter<ReportShedule, Long>(reportShedule, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public ReportShedule processAndReturnResult() {
				return reportSheduleService.createOne(entity);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @param reportParamset
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneShedule(@PathVariable(value = "reportSheduleId") Long reportSheduleId,
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "reportParamsetId", required = true) Long reportParamsetId,
			@RequestBody ReportShedule reportShedule) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamsetId);
		checkNotNull(reportShedule);
		checkArgument(!reportShedule.isNew());

		ReportParamset checkParamset = reportParamsetService.findOne(reportParamsetId);

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body("ReportParamset is not found");
		}

		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());

		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		reportShedule.setReportParamset(checkParamset);

		ReportShedule checkShedule = reportSheduleService.findOne(reportShedule.getId());
		if (checkShedule == null) {
			return ResponseEntity.badRequest().build();
		}

		if (checkShedule.getSubscriberId() != currentSubscriberService.getSubscriberId()) {
			return ResponseEntity.badRequest().build();
		}

		ApiAction action = new AbstractEntityApiAction<ReportShedule>(reportShedule) {

			@Override
			public void process() {
				setResultEntity(reportSheduleService.updateOne(entity));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

}
