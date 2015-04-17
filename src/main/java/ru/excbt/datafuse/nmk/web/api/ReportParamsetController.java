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
	@RequestMapping(value = "/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_REPORT, true,
						currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
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
	@RequestMapping(value = "/archive/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetListArchCommerce() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetListArchCons() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_REPORT, false,
						currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetListArchEvent() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.EVENT_REPORT,
						false, currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(reportParamsetList);
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
	private ResponseEntity<?> updateInternal(Long reportParamsetId,
			ReportParamset reportParamset) {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());

		ReportParamset resultEntity = null;
		try {
			resultEntity = reportParamsetService.updateOne(reportParamset);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity ReportParamset (id={}): {}",
					reportParamsetId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplate
	 * @param reportType
	 * @return
	 */
	private ResponseEntity<?> createInternal(Long reportTemplateId,
			ReportParamset reportParamset, HttpServletRequest request) {

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

		ReportParamset resultEntity = null;
		try {
			resultEntity = reportParamsetService.createOne(reportParamset);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save new entity ReportParamset: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create(request.getRequestURI() + "/"
				+ +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
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
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset);
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
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset);
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
			@RequestBody ReportParamset reportParamset) {
		return updateInternal(reportParamsetId, reportParamset);
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
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ReportParamset resultEntity = null;

		try {
			resultEntity = reportParamsetService.createByTemplate(srcId,
					reportParamset, currentSubscriberService.getSubscriber());
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
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, request);
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
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, request);
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
			@RequestBody ReportParamset reportParamset,
			HttpServletRequest request) {
		return createInternal(reportTemplateId, reportParamset, request);
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
				.selectParamsetContObjectUnits(reportParamsetId);

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
			@RequestParam(value = "reportParamsetId", required = true) Long reportParamsetId) {

		checkNotNull(reportParamsetId);

		ReportParamset resultEntity = null;

		try {
			resultEntity = reportParamsetService
					.moveToArchive(reportParamsetId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during move to archive entity ReportParamset (id={}): {}",
					reportParamsetId, e);
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

}
