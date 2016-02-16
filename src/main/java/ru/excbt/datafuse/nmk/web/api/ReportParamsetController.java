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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с набором параметров отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportParamset")
public class ReportParamsetController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetController.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

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

		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(reportTypeKey,
				ReportConstants.IS_ACTIVE, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ResponseEntity.ok(reportParamsetService.findOne(reportParamsetId));
	}

	/**
	 * 
	 * @param reportUrlName
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyArchList(@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(reportTypeKey,
				ReportConstants.IS_NOT_ACTIVE, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportParamsetId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyOneArch(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ResponseEntity.ok(reportParamsetService.findOne(reportParamsetId));
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset, HttpServletRequest request) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return createInternal(reportTemplateId, reportParamset, contObjectIds, request);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return updateInternal(reportParamsetId, reportParamset, fixContObjectIds);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportParamsetId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteAnyOneArch(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @param reportType
	 * @return
	 */
	private ResponseEntity<?> updateInternal(final Long reportParamsetId, final ReportParamset reportParamset,
			final Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());

		ApiAction action = new AbstractEntityApiAction<ReportParamset>(reportParamset) {
			@Override
			public void process() {
				setResultEntity(reportParamsetService.updateOne(entity, contObjectIds));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @param reportType
	 * @return
	 */
	private ResponseEntity<?> createInternal(final Long reportTemplateId, final ReportParamset reportParamset,
			final Long[] contObjectIds, HttpServletRequest request) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.isNew());

		ReportTemplate reportTemplate = reportTemplateService.findOne(reportTemplateId);

		if (reportTemplate == null) {
			return ResponseEntity.badRequest().build();
		}

		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());
		reportParamset.set_active(true);

		try {
			String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(reportParamset);
			logger.trace("ReportParamset JSON: {}", paramJson);

		} catch (JsonProcessingException e) {
		}

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ReportParamset, Long>(reportParamset, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public ReportParamset processAndReturnResult() {
				return reportParamsetService.createOne(reportParamset, contObjectIds);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 * 
	 * @param id
	 */
	private ResponseEntity<?> deleteInternal(final Long id) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				reportParamsetService.deleteOne(id);
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
	@RequestMapping(value = "/createByTemplate/{srcId}", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(@PathVariable(value = "srcId") Long srcId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset, HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ReportParamset, Long>(reportParamset, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public ReportParamset processAndReturnResult() {
				return reportParamsetService.createByTemplate(srcId, reportParamset, contObjectIds,
						currentSubscriberService.getSubscriber());
			}

			@Override
			public URI getLocation() {
				checkNotNull(getResultEntity());

				String keyname = getResultEntity().getReportTemplate().getReportTypeKeyname();
				ReportTypeKey reportType = ReportTypeKey.valueOf(keyname);

				return URI.create(
						"/api/reportParamset" + ReportConstants.getReportTypeURL(reportType) + "/" + getLocationId());
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectUnits(@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);
		List<ContObject> resultList = reportParamsetService.selectParamsetContObjects(reportParamsetId);

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/available", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectUnits(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);
		List<ContObject> resultList = reportParamsetService.selectParamsetAvailableContObjectUnits(reportParamsetId,
				currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> addReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectId", required = true) Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		if (!subscrContObjectService.checkContObjectSubscription(currentSubscriberService.getSubscriberId(),
				contObjectId)) {
			return ResponseEntity.badRequest().build();
		}

		ApiAction action = new EntityApiActionAdapter<ReportParamsetUnit>() {

			@Override
			public ReportParamsetUnit processAndReturnResult() {
				return reportParamsetService.addUnitToParamset(reportParamsetId, contObjectId);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

		//		ReportParamsetUnit resultEntity = null;
		//
		//		try {
		//			resultEntity = reportParamsetService.addUnitToParamset(reportParamsetId, contObjectId);
		//		} catch (AccessDeniedException e) {
		//			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		//		} catch (TransactionSystemException | PersistenceException e) {
		//			logger.error("Error during create entity ReportParamsetUnit by ReportParamset (id={}): {}",
		//					reportParamsetId, e);
		//			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		//		}
		//
		//		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/{contObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				reportParamsetService.deleteUnitFromParamset(reportParamsetId, contObjectId);

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);

		//		try {
		//			reportParamsetService.deleteUnitFromParamset(reportParamsetId, contObjectId);
		//		} catch (AccessDeniedException e) {
		//			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		//		} catch (TransactionSystemException | PersistenceException e) {
		//			logger.error("Can't delete ReportParamsetUnit. (reportParamsetId={}, contObjectId={}) : {}",
		//					reportParamsetId, contObjectId, e);
		//			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		//		}
		//
		//		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@RequestMapping(value = "/archive/move", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> moveToArchive(
			@RequestParam(value = "reportParamsetId", required = true) final Long reportParamsetId) {

		checkNotNull(reportParamsetId);

		ApiAction action = new AbstractEntityApiAction<ReportParamset>() {
			@Override
			public void process() {
				setResultEntity(reportParamsetService.moveToArchive(reportParamsetId));
			}
		};

		ResponseEntity<?> responeResult = WebApiHelper.processResponceApiActionOkBody(action);

		if (action.getResult() == null) {
			responeResult = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
					.body(ApiResult.build(ApiResultCode.ERR_UNCKNOWN));
		}

		return responeResult;
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObjects", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateReportParamsetContObjects(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = true) Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectIds);

		for (Long id : contObjectIds) {
			if (!subscrContObjectService.checkContObjectSubscription(currentSubscriberService.getSubscriberId(), id)) {
				return ResponseEntity.badRequest().build();
			}
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				reportParamsetService.updateUnitToParamset(reportParamsetId, contObjectIds);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

		//		try {
		//			reportParamsetService.updateUnitToParamset(reportParamsetId, contObjectIds);
		//		} catch (AccessDeniedException e) {
		//			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		//		} catch (TransactionSystemException | PersistenceException e) {
		//			logger.error("Error during create entity ReportParamsetUnit by ReportParamset (id={}): {}",
		//					reportParamsetId, e);
		//			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		//		}
		//
		//		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/menu/contextLaunch", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetContextLaunch() {
		List<ReportParamset> xList = reportParamsetService.selectReportParamsetContextLaunch(getCurrentSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(xList));
	}

}
