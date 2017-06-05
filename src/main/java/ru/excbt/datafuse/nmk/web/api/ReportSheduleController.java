package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
public class ReportSheduleController extends AbstractApiResource {

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

		ApiActionObjectProcess actionProcess = () -> {
			LocalDateTime nowDate = LocalDateTime.now().withMillisOfDay(0);
			List<ReportShedule> result = reportSheduleService
					.selectReportShedule(currentSubscriberService.getSubscriberId(), nowDate);
			return result;
		};

		return responseOK(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportShedule() {

		ApiActionObjectProcess actionProcess = () -> {
			List<ReportShedule> result = reportSheduleService
					.selectReportShedule(currentSubscriberService.getSubscriberId());
			return result;
		};
		return responseOK(actionProcess);
	}

	/**
	 *
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleOne(@PathVariable(value = "reportSheduleId") Long reportSheduleId) {

		ApiActionObjectProcess actionProcess = () -> {
			return reportSheduleService.findOne(reportSheduleId);
		};
		return responseOK(actionProcess);
	}

	/**
	 *
	 * @param reportSheduleId
	 * @return
	 */
	@RequestMapping(value = "/{reportSheduleId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportShedule(@PathVariable(value = "reportSheduleId") final Long reportSheduleId) {

		ApiActionVoidProcess actionProcess = () -> reportSheduleService.deleteOne(reportSheduleId);

		return WebApiHelper.processResponceApiActionDelete(actionProcess);
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
			@RequestBody final ReportShedule reportShedule, HttpServletRequest request) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamsetId);
		checkNotNull(reportShedule);
		checkArgument(reportShedule.isNew());

		ReportParamset checkParamset = reportParamsetService.findReportParamset(reportParamsetId);

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body("ReportParamset is not found");
		}

		if (!reportTemplateId.equals(checkParamset.getReportTemplate().getId())) {
			return ResponseEntity.badRequest().body("Invalid reportTemplateId & reportParamsetId");
		}

		ApiActionProcess<ReportShedule> actionProcess = () -> {
			reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
			reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());

			reportShedule.setReportTemplate(checkParamset.getReportTemplate());
			reportShedule.setReportParamset(checkParamset);
			return reportSheduleService.createOne(reportShedule);

		};

		return responseCreate(actionProcess, () -> request.getRequestURI());

		//		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		//		reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());
		//
		//		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		//		reportShedule.setReportParamset(checkParamset);
		//
		//		ApiActionLocation action = new ApiActionEntityLocationAdapter<ReportShedule, Long>(reportShedule, request) {
		//
		//			@Override
		//			protected Long getLocationId() {
		//				return getResultEntity().getId();
		//			}
		//
		//			@Override
		//			public ReportShedule processAndReturnResult() {
		//				return reportSheduleService.createOne(entity);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionCreate(action);
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

		final ReportParamset checkParamset = reportParamsetService.findReportParamset(reportParamsetId);

		if (checkParamset == null) {
			return ResponseEntity.badRequest().body("ReportParamset is not found");
		}

		ReportShedule checkShedule = reportSheduleService.findOne(reportShedule.getId());
		if (checkShedule == null) {
			return ResponseEntity.badRequest().build();
		}

		ApiActionObjectProcess actionProcess = () -> {
			reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
			reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());

			reportShedule.setReportTemplate(checkParamset.getReportTemplate());
			reportShedule.setReportParamset(checkParamset);

			return reportSheduleService.updateOne(reportShedule);
		};
		return responseUpdate(actionProcess);

		//		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
		//		reportShedule.setSubscriberId(currentSubscriberService.getSubscriberId());
		//
		//		reportShedule.setReportTemplate(checkParamset.getReportTemplate());
		//		reportShedule.setReportParamset(checkParamset);
		//
		//		ApiAction action = new AbstractEntityApiAction<ReportShedule>(reportShedule) {
		//
		//			@Override
		//			public void process() {
		//				setResultEntity(reportSheduleService.updateOne(entity));
		//			}
		//
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);

	}

}
