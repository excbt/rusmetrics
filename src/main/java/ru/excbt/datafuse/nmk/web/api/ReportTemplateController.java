package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.ReportWizardService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

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
	 * @param reportUrlName
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneAny(
			@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey
				.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError(
					"Report of type %s is not supported", reportUrlName));
		}

		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						reportTypeKey, ReportConstants.IS_ACTIVE);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	// @RequestMapping(value = "/cons", method = RequestMethod.GET, produces =
	// APPLICATION_JSON_UTF8)
	// public ResponseEntity<?> getReportTemplatesConsOld() {
	// List<ReportTemplate> result = reportTemplateService
	// .getAllReportTemplates(
	// currentSubscriberService.getSubscriberId(),
	// ReportTypeKey.CONS_T2_REPORT, ReportConstants.IS_ACTIVE);
	// return ResponseEntity.ok(result);
	// }

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneArchiveAny(
			@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey
				.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError(
					"Report of type %s is not supported", reportUrlName));
		}

		List<ReportTemplate> result = reportTemplateService
				.getAllReportTemplates(
						currentSubscriberService.getSubscriberId(),
						reportTypeKey, ReportConstants.IS_NOT_ACTIVE);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	// @RequestMapping(value = "/archive/cons", method = RequestMethod.GET,
	// produces = APPLICATION_JSON_UTF8)
	// public ResponseEntity<?> getReportTemplatesArchiveConsOld() {
	// List<ReportTemplate> result = reportTemplateService
	// .getAllReportTemplates(
	// currentSubscriberService.getSubscriberId(),
	// ReportTypeKey.CONS_T2_REPORT,
	// ReportConstants.IS_NOT_ACTIVE);
	// return ResponseEntity.ok(result);
	// }

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	private ResponseEntity<?> deleteInternal(final long reportTemplateId) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				reportTemplateService.deleteOne(reportTemplateId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportTemplateId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneArchiveAny(
			@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable("reportTemplateId") long reportTemplateId) {

		ReportTypeKey reportTypeKey = ReportTypeKey
				.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError(
					"Report of type %s is not supported", reportUrlName));
		}

		return deleteInternal(reportTemplateId);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportTemplateId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneAny(
			@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

		ReportTypeKey reportTypeKey = ReportTypeKey
				.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError(
					"Report of type %s is not supported", reportUrlName));
		}

		ReportTemplate result = reportTemplateService.findOne(reportTemplateId);
		if (result == null) {
			return responseBadRequest();
		}

		return responseOK(result);
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

		ApiAction action = new AbstractEntityApiAction<ReportTemplate>(
				reportTemplate) {
			@Override
			public void process() {
				setResultEntity(reportTemplateService.updateOne(entity));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportTemplateId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneAny(
			@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@RequestBody ReportTemplate reportTemplate) {

		ReportTypeKey reportTypeKey = ReportTypeKey
				.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError(
					"Report of type %s is not supported", reportUrlName));
		}

		return updateInternal(reportTemplateId, reportTemplate, reportTypeKey);
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

		ApiAction action = new AbstractEntityApiAction<ReportTemplate>() {
			@Override
			public void process() {
				setResultEntity(reportTemplateService
						.moveToArchive(reportTemplateId));
			}
		};

		ResponseEntity<?> responeResult = WebApiHelper
				.processResponceApiActionUpdate(action);

		if (action.getResult() == null) {
			responeResult = ResponseEntity
					.status(HttpStatus.FAILED_DEPENDENCY)
					.body(ApiResult
							.build(ApiResultCode.ERR_BRM_VALIDATION,
									"Report Template have active ReportParamset. Moving to archive is impossible"));
		}

		return responeResult;
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/createByTemplate/{srcId}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(
			@PathVariable(value = "srcId") final Long srcId,
			@RequestBody ReportTemplate reportTemplate,
			HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ApiActionLocation action = new AbstractEntityApiActionLocation<ReportTemplate, Long>(
				reportTemplate, request) {

			@Override
			public void process() {
				setResultEntity(reportTemplateService.createByTemplate(srcId,
						entity, currentSubscriberService.getSubscriber()));
			}

			@Override
			protected Long getLocationId() {
				checkNotNull(getResultEntity());
				return getResultEntity().getId();
			}

			@Override
			public URI getLocation() {
				checkNotNull(getResultEntity());
				return URI.create("/api/reportTemplate"
						+ ReportConstants.getReportTypeURL(getResultEntity()
								.getReportTypeKey()) + "/" + getLocationId());
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

}
