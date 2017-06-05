package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с шаблонами отчетов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportTemplate")
public class ReportTemplateController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportTemplateController.class);

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 * @param reportUrlName
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyList(@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ApiActionObjectProcess actionProcess = () -> {
			return reportTemplateService.getAllReportTemplates(getCurrentSubscriberId(), reportTypeKey,
					ReportConstants.IS_ACTIVE);
		};
		return responseOK(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyArchList(@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ApiActionObjectProcess actionProcess = () -> {
			return reportTemplateService.getAllReportTemplates(getCurrentSubscriberId(), reportTypeKey,
					ReportConstants.IS_NOT_ACTIVE);
		};
		return responseOK(actionProcess);

	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportTemplateId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ApiActionObjectProcess actionProcess = () -> {
			return reportTemplateService.findOne(reportTemplateId);
		};
		return responseOK(actionProcess, (x) -> x == null ? responseBadRequest() : null);

	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportTemplateId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return updateInternal(reportTemplateId, reportTemplate, reportTypeKey);
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportTemplateId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteAnyOneArch(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportTemplateId") long reportTemplateId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return deleteInternal(reportTemplateId);
	}

	/**
	 *
	 * @param reportTemplateId
	 * @return
	 */
	private ResponseEntity<?> deleteInternal(final long reportTemplateId) {

		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				reportTemplateService.deleteOne(reportTemplateId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @param reportType
	 * @return
	 */
	private ResponseEntity<?> updateInternal(Long reportTemplateId, ReportTemplate reportTemplate,
			ReportTypeKey reportType) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportTemplate);
		checkNotNull(reportTemplate.getId());
		checkNotNull(reportType);
		checkArgument(reportTemplate.getId().equals(reportTemplateId));
		checkArgument(reportType.getKeyname().equals(reportTemplate.getReportTypeKeyname()));

		ApiActionObjectProcess actionProcess = () -> {
			reportTemplate.setSubscriber(currentSubscriberService.getSubscriber());
			return reportTemplateService.updateOne(reportTemplate);
		};
		return responseUpdate(actionProcess);

	}

	/**
	 *
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/move", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> moveToArchive(
			@RequestParam(value = "reportTemplateId", required = true) final Long reportTemplateId) {

		checkNotNull(reportTemplateId);

		ApiActionObjectProcess actionProcess = () -> {
			return reportTemplateService.moveToArchive(reportTemplateId);
		};
		return responseUpdate(actionProcess,
				(x) -> x == null
						? ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
								.body(ApiResult.build(ApiResultCode.ERR_BRM_VALIDATION,
										"Report Template have active ReportParamset. Moving to archive is impossible"))
						: null);

		//		ApiAction action = new AbstractEntityApiAction<ReportTemplate>() {
		//			@Override
		//			public void process() {
		//				setResultEntity(reportTemplateService.moveToArchive(reportTemplateId));
		//			}
		//		};
		//
		//		ResponseEntity<?> responeResult = WebApiHelper.processResponceApiActionUpdate(action);
		//
		//		if (action.getResult() == null) {
		//			responeResult = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
		//					.body(ApiResult.build(ApiResultCode.ERR_BRM_VALIDATION,
		//							"Report Template have active ReportParamset. Moving to archive is impossible"));
		//		}
		//
		//		return responeResult;
	}

	/**
	 *
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/createByTemplate/{srcId}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(@PathVariable(value = "srcId") final Long srcId,
			@RequestBody final ReportTemplate reportTemplate, HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ApiActionProcess<ReportTemplate> actionProcess = () -> reportTemplateService.createByTemplate(srcId,
				reportTemplate, currentSubscriberService.getSubscriber());

		return responseCreate(actionProcess, () -> {
			return "/api/reportTemplate" + ReportConstants.getReportTypeURL(reportTemplate.getReportTypeKeyname());
		});

		//		ApiActionLocation action = new ApiActionEntityLocationAdapter<ReportTemplate, Long>(reportTemplate, request) {
		//
		//			@Override
		//			protected Long getLocationId() {
		//				return getResultEntity().getId();
		//			}
		//
		//			@Override
		//			public ReportTemplate processAndReturnResult() {
		//				return reportTemplateService.createByTemplate(srcId, entity, currentSubscriberService.getSubscriber());
		//			}
		//
		//			@Override
		//			public URI getLocation() {
		//				checkNotNull(getResultEntity());
		//
		//				return URI.create("/api/reportTemplate"
		//						+ ReportConstants.getReportTypeURL(getResultEntity().getReportTypeKeyname()) + "/"
		//						+ getLocationId());
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionCreate(action);

	}

}
