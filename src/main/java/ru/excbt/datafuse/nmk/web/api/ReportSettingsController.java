package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.vo.ReportTypeWithParamsVO;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportActionTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportSheduleTypeRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.ReportPeriodService;
import ru.excbt.datafuse.nmk.data.service.ReportTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceAccessService;
import ru.excbt.datafuse.nmk.web.ApiConst;
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
@RestController
@RequestMapping(value = "/api/reportSettings")
public class ReportSettingsController {

	private static final Logger logger = LoggerFactory.getLogger(ReportSettingsController.class);

	private final ReportTypeService reportTypeService;

	private final ReportPeriodService reportPeriodService;

	private final ReportSheduleTypeRepository reportSheduleTypeRepository;

	private final ReportActionTypeRepository reportActionTypeRepository;

	private final PortalUserIdsService portalUserIdsService;

	private final SubscrServiceAccessService subscrServiceAccessService;

    public ReportSettingsController(ReportTypeService reportTypeService, ReportPeriodService reportPeriodService, ReportSheduleTypeRepository reportSheduleTypeRepository, ReportActionTypeRepository reportActionTypeRepository, PortalUserIdsService portalUserIdsService, SubscrServiceAccessService subscrServiceAccessService) {
        this.reportTypeService = reportTypeService;
        this.reportPeriodService = reportPeriodService;
        this.reportSheduleTypeRepository = reportSheduleTypeRepository;
        this.reportActionTypeRepository = reportActionTypeRepository;
        this.portalUserIdsService = portalUserIdsService;
        this.subscrServiceAccessService = subscrServiceAccessService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportType() {

		ApiActionObjectProcess actionProcess = () -> {
			List<ReportType> resultReports = reportTypeService
					.findAllReportTypes(portalUserIdsService.isSystemUser());
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
					.findAllReportTypes(portalUserIdsService.isSystemUser());
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
		ApiActionObjectProcess actionProcess = reportPeriodService::selectReportPeriods;
		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportSheduleType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleTypeJson() {

		ApiActionObjectProcess actionProcess = reportActionTypeRepository::findAll;
		return ApiResponse.responseOK(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/reportActionType", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportActionTypeJson() {
		ApiActionObjectProcess actionProcess = () -> reportSheduleTypeRepository.findAll();
		return ApiResponse.responseOK(actionProcess);
	}

    protected <T> List<T> filterObjectAccess(List<T> objectList) {
        return subscrServiceAccessService.filterObjectAccess(objectList, portalUserIdsService.getCurrentIds());
    }

}
