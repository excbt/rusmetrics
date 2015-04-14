package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;

@Controller
@RequestMapping(value = "/api/reportSettings/{reportTemplateId}/reportParamset")
public class ReportParamsetController extends WebApiController {

	@Autowired
	private ReportParamsetService reportParamsetService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetList(
			@PathVariable(value = "reportTemplateId") Long reportTemplateId) {

		return ResponseEntity.ok(reportParamsetService
				.findReportParamsetList(reportTemplateId));
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

}
