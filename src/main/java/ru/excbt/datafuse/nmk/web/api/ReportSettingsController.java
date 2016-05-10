package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportActionTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportSheduleTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportTypeService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с настройками отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportSettings")
public class ReportSettingsController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReportSettingsController.class);

	@Autowired
	private ReportTypeService reportTypeService;

	@Autowired
	private ReportPeriodRepository reportPeriodRepository;

	@Autowired
	private ReportSheduleTypeRepository reportSheduleTypeRepository;

	@Autowired
	private ReportActionTypeRepository reportActionTypeRepository;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportType", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportType() {

		List<ReportType> resultReports = reportTypeService.findAllReportTypes(currentSubscriberService.isSystemUser());

		resultReports = filterObjectAccess(resultReports);

		return ResponseEntity.ok(resultReports);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportPeriod", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportPeriodJson() {
		return ResponseEntity.ok(reportPeriodRepository.findAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportSheduleType", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleTypeJson() {
		return ResponseEntity.ok(reportActionTypeRepository.findAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reportActionType", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportActionTypeJson() {
		return ResponseEntity.ok(reportSheduleTypeRepository.findAll());
	}

}
