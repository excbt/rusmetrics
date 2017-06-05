package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.vo.ReportTypeWithParamsVO;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportActionTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportSheduleTypeRepository;
import ru.excbt.datafuse.nmk.data.service.ReportPeriodService;
import ru.excbt.datafuse.nmk.data.service.ReportTypeService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

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
public class ReportSettingsController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportSettingsController.class);

	@Autowired
	private ReportTypeService reportTypeService;

	@Autowired
	private ReportPeriodService reportPeriodService;

	@Autowired
	private ReportSheduleTypeRepository reportSheduleTypeRepository;

	@Autowired
	private ReportActionTypeRepository reportActionTypeRepository;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportType() {

		ApiActionObjectProcess actionProcess = () -> {
			List<ReportType> resultReports = reportTypeService
					.findAllReportTypes(currentSubscriberService.isSystemUser());
			resultReports = filterObjectAccess(resultReports);
			List<ReportTypeWithParamsVO> result = reportTypeService.makeReportTypeParams(resultReports);
			return result;
		};

		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportTypesParams", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTypeParams() {

		ApiActionObjectProcess actionProcess = () -> {
			List<ReportType> resultReports = reportTypeService
					.findAllReportTypes(currentSubscriberService.isSystemUser());
			resultReports = filterObjectAccess(resultReports);
			return resultReports;
		};
		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportPeriod", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportPeriods() {
		ApiActionObjectProcess actionProcess = () -> {
			return reportPeriodService.selectReportPeriods();
		};
		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportSheduleType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleTypeJson() {

		ApiActionObjectProcess actionProcess = () -> {
			return reportActionTypeRepository.findAll();
		};
		return ApiResponse.responseOK(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportActionType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportActionTypeJson() {
		ApiActionObjectProcess actionProcess = () -> {
			return reportSheduleTypeRepository.findAll();
		};
		return ApiResponse.responseOK(actionProcess);
	}

}
