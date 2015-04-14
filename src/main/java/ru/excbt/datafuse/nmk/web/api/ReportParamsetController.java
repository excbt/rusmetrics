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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportParamset")
public class ReportParamsetController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetController.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getCommerceReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT,
						true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getConsReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_REPORT, true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getEventReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.EVENT_REPORT, true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/commerce", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchCommerceReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT,
						true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/cons", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchConsReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.CONS_REPORT, true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/archive/event", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getArchEventReportParamsetList() {

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportTypeParamsetList(ReportTypeKey.EVENT_REPORT, true);

		return ResponseEntity.ok(reportParamsetList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamset(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamsetId);

		ReportParamset result = reportParamsetService.findOne(reportParamsetId);
		if (result == null) {
			return ResponseEntity.badRequest().build();
		}

		if (result.getReportTemplate() == null) {
			return ResponseEntity.badRequest().build();
		}
		if (!result.getReportTemplate().getId().equals(reportTemplateId)) {
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
	public ResponseEntity<?> updateInternal(Long reportParamsetId,
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
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/commerce/{reportParamsetId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateCommerceOne(
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
	public ResponseEntity<?> updateConsOne(
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
	public ResponseEntity<?> updateEventOne(
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
					reportParamset);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during create entity by Reportparamset (id={}): {}",
					srcId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create(request.getServletPath()
				+ ReportConstants.getReportTypeURL(resultEntity
						.getReportTemplate().getReportType()) + "/"
				+ +resultEntity.getId());

		return ResponseEntity.created(location).body(resultEntity);
	}

}
