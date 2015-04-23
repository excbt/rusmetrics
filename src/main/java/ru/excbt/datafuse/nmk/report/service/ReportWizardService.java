package ru.excbt.datafuse.nmk.report.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.ReportMasterTemplateBodyService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.report.model.ReportColumn;
import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.nmk.reports.ColumnElement;
import ru.excbt.nmk.reports.ReportConvert;

@Service
public class ReportWizardService implements SecuredRoles {

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;

	@Autowired
	private ReportTemplateBodyRepository reportTemplateBodyRepository;

	@Autowired
	private ReportTemplateService reportTemplateService;

	private void loadColumnHeader() {
		// ReportConvert.getHeaderColumn()
	}

	/**
	 * 
	 * @return
	 */
	public List<ReportColumn> getReportColumns() {
		List<ReportColumn> resultList = new ArrayList<>();
		ColumnElement[] elements = ReportConvert.getHeaderColumn();
		checkNotNull(elements);
		for (ColumnElement ce : elements) {
			resultList.add(new ReportColumn(ce));
		}
		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	public ReportColumnSettings getReportColumnSettings() {
		ReportColumnSettings result = new ReportColumnSettings();
		ColumnElement[] elements = ReportConvert.getHeaderColumn();
		checkNotNull(elements);

		for (ColumnElement ce : elements) {
			if (ce.n == 0) {
				result.getAllTsList().add(new ReportColumn(ce));
				continue;
			}

			if (ce.n != 0 && ce.nSystem == 1) {
				result.getTs1List().add(new ReportColumn(ce));
			}

			if (ce.n != 0 && ce.nSystem == 2) {
				result.getTs2List().add(new ReportColumn(ce));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate createCommerceWizard(ReportTemplate reportTemplate,
			ReportColumnSettings reportColumnSettings, Subscriber subscriber) {

		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());
		checkNotNull(reportColumnSettings);

		checkNotNull(reportColumnSettings.getAllTsList());
		checkArgument(reportColumnSettings.getAllTsList().size() == 2);

		checkNotNull(reportColumnSettings.getTs1List());
		checkNotNull(reportColumnSettings.getTs2List());

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		if (reportTemplate.getIntegratorIncluded() == null) {
			reportTemplate.setIntegratorIncluded(false);
		}

		reportTemplate.setSubscriber(subscriber);
		reportTemplate.setReportTypeKey(ReportTypeKey.COMMERCE_REPORT);

		ReportMasterTemplateBody masterBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT);

		if (masterBody == null) {
			throw new PersistenceException(String.format(
					"ReportMasterTemplate for %s not found",
					ReportTypeKey.COMMERCE_REPORT.name()));
		}

		ReportTemplate resultEntity = reportTemplateRepository
				.save(reportTemplate);

		reportTemplateService.saveReportTemplateBodyCompiled(
				resultEntity.getId(), masterBody.getBodyCompiled(),
				masterBody.getBodyFilename());

		return resultEntity;
	}

}
