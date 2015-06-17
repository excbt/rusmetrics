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
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

@Controller
@RequestMapping(value = "/api/reportParamset")
public class ReportParamsetController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetController.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCommerceReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCommerceReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t1", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsT1ReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T1_REPORT,
						true, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsT1ReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t2", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsT2ReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T2_REPORT,
						true, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsT2ReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T2_REPORT,
						true, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}	
		
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getEventReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.EVENT_REPORT, true,
						currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getEventReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}	
	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchCommerceReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchCommerceReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}		
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t1", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsT1ReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T1_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t1/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsT1ReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}		
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t2", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsT2ReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T2_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t2/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsT2ReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}		
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_T2_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}			
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchEventReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.EVENT_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchEventReportParamsetGet(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return ResponseEntity.ok(reportParamsetService
				.findOne(reportParamsetId));
	}	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetList(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);

		ReportParamset result = reportParamsetService.findOne(reportParamsetId);
		if (result == null) {
			return ResponseEntity.badRequest().build();
		}

		if (result.getReportTemplate() == null) {
			return ResponseEntity.badRequest().build();
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
	private ResponseEntity<?> updateInternal(final Long reportParamsetId,
			final ReportParamset reportParamset, final Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());

		ApiAction action = new AbstractEntityApiAction<ReportParamset>(
				reportParamset) {
			@Override
			public void process() {
				setResultEntity(reportParamsetService.updateOne(entity,
						contObjectIds));
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
	private ResponseEntity<?> createInternal(final Long reportTemplateId,
			final ReportParamset reportParamset, final Long[] contObjectIds,
			HttpServletRequest request) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.isNew());

		ReportTemplate reportTemplate = reportTemplateService
				.findOne(reportTemplateId);

		if (reportTemplate == null) {
			return ResponseEntity.badRequest().build();
		}

		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());
		reportParamset.set_active(true);

		ApiActionLocation action = new AbstractEntityApiActionLocation<ReportParamset, Long>(
				reportParamset, request) {

			@Override
			public void process() {
				setResultEntity(reportParamsetService.createOne(reportParamset,
						contObjectIds));
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneCommerce(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset, contObjectIds);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneConsT1(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset, contObjectIds);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneConsT2(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset, contObjectIds);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneCons(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset, contObjectIds);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/event/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneEvent(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset, contObjectIds);
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
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ReportParamset resultEntity = null;

		try {
			resultEntity = reportParamsetService.createByTemplate(srcId,
					reportParamset, contObjectIds,
					currentSubscriberService.getSubscriber());
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity by Reportparamset (id={}): {}",
					srcId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create("/api/reportParamset"
				+ ReportConstants.getReportTypeURL(resultEntity
						.getReportTemplate().getReportTypeKey()) + "/"
				+ +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneCommerce(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, contObjectIds,
				request);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t1", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneConsT1(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, contObjectIds,
				request);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t2", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneConsT2(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, contObjectIds,
				request);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneCons(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, contObjectIds,
				request);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/event", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneEvent(
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, contObjectIds,
				request);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectUnits(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);
		List<ContObject> resultList = reportParamsetService
				.selectParamsetContObjects(reportParamsetId);

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/available", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectUnits(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);
		List<ContObject> resultList = reportParamsetService
				.selectParamsetAvailableContObjectUnits(reportParamsetId,
						currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> addReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectId", required = true) Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		if (!subscriberService.checkContObjectSubscription(
				currentSubscriberService.getSubscriberId(), contObjectId)) {
			return ResponseEntity.badRequest().build();
		}

		ReportParamsetUnit resultEntity = null;

		try {
			resultEntity = reportParamsetService.addUnitToParamset(
					reportParamsetId, contObjectId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity ReportParamsetUnit by ReportParamset (id={}): {}",
					reportParamsetId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/{contObjectId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		try {
			reportParamsetService.deleteUnitFromParamset(reportParamsetId,
					contObjectId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Can't delete ReportParamsetUnit. (reportParamsetId={}, contObjectId={}) : {}",
					reportParamsetId, contObjectId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.ok().build();
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
				setResultEntity(reportParamsetService
						.moveToArchive(reportParamsetId));
			}
		};

		ResponseEntity<?> responeResult = WebApiHelper
				.processResponceApiActionOkBody(action);

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
	@RequestMapping(value = "/{reportParamsetId}/contObjects", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateReportParamsetContObjects(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = true) Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectIds);

		for (Long id : contObjectIds) {
			if (!subscriberService.checkContObjectSubscription(
					currentSubscriberService.getSubscriberId(), id)) {
				return ResponseEntity.badRequest().build();
			}
		}

		try {
			reportParamsetService.updateUnitToParamset(reportParamsetId,
					contObjectIds);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity ReportParamsetUnit by ReportParamset (id={}): {}",
					reportParamsetId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneCommerce(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneCommerceArchive(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t1/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneConsT1(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t1/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneConsT1Archive(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons_t2/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneConsT2(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/archive/cons_t2/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneConsT2Archive(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/cons/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneCons(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/archive/cons/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneConsArchive(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/event/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneEvent(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/archive/event/{reportParamsetId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneEventArchive(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		return deleteInternal(reportParamsetId);
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

}
