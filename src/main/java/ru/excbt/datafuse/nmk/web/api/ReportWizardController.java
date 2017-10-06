package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportWizardService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.ReportWizardParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с конструктором отчетов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportWizard")
public class ReportWizardController {

	private static final Logger logger = LoggerFactory.getLogger(ReportWizardController.class);

	@Autowired
	private ReportWizardService reportWizardService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/columnSettings/commerce", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportSheduleActive() {
		ReportColumnSettings result = reportWizardService.getReportColumnSettings();
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/commerce", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createWizardCommerceReport(@RequestBody final ReportWizardParam reportWizardParam) {

		checkNotNull(reportWizardParam);
		checkNotNull(reportWizardParam.getReportTemplate());
		checkNotNull(reportWizardParam.getReportColumnSettings());

		if (!reportWizardParam.getReportTemplate().isNew()) {
			return ResponseEntity.badRequest().build();
		}

		ApiAction action = new AbstractEntityApiAction<ReportTemplate>() {
			@Override
			public void process() {
				setResultEntity(reportWizardService.createCommerceWizard(reportWizardParam.getReportTemplate(),
						reportWizardParam.getReportColumnSettings(), currentSubscriberService.getSubscriber()));
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

}
