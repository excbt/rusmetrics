package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import net.sf.jasperreports.engine.JRException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.report.model.ReportColumn;
import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.nmk.reports.ColumnElement;
import ru.excbt.nmk.reports.ReportConvert;

@Service
public class ReportWizardService implements SecuredRoles {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ReportWizardService.class);
	
	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;

	@Autowired
	private ReportTemplateBodyRepository reportTemplateBodyRepository;

	@Autowired
	private ReportTemplateService reportTemplateService;

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
	 * @param src
	 * @return
	 */
	private ColumnElement columnElementFactoiry(ReportColumn src) {
		return new ColumnElement(src.getSystemNumber(), src.getColumnNumber(),
				src.getColumnHeader());
	}

	/**
	 * 
	 * @param reportColumnSettings
	 * @return
	 */
	public ColumnElement[] makeColumnElement(
			ReportColumnSettings reportColumnSettings) {
		checkNotNull(reportColumnSettings);
		checkNotNull(reportColumnSettings.getAllTsList());
		checkNotNull(reportColumnSettings.getTs1List());
		checkNotNull(reportColumnSettings.getTs2List());

		checkArgument(reportColumnSettings.getAllTsList().size() == 2);

		List<ColumnElement> elementList = new ArrayList<>();

		for (ReportColumn rc : reportColumnSettings.getAllTsList()) {
			elementList.add(columnElementFactoiry(rc));
		}

		for (ReportColumn rc : reportColumnSettings.getTs1List()) {
			elementList.add(columnElementFactoiry(rc));
		}

		for (ReportColumn rc : reportColumnSettings.getTs2List()) {
			elementList.add(columnElementFactoiry(rc));
		}
		return elementList.toArray(new ColumnElement[0]);
	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ SUBSCR_ROLE_USER, SUBSCR_ROLE_ADMIN })
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

		checkState(masterBody.getBody() != null
				&& masterBody.getBody().length > 0);

		boolean showIntegrators = reportTemplate.getIntegratorIncluded() == null ? false
				: reportTemplate.getIntegratorIncluded();

		byte[] resultBodyCompiled = null;

		try {
			logger.trace("Starting prepare data for ReportConvert");
			ColumnElement[] elements = makeColumnElement(reportColumnSettings);
			ByteArrayInputStream is = new ByteArrayInputStream(
					masterBody.getBody());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			logger.trace("Starting convertJRXmlReport");
			ReportConvert.convertJRXmlReport(is, elements, showIntegrators, os);
			resultBodyCompiled = os.toByteArray();
			logger.trace("Complete convertJRXmlReport");
		} catch (IOException | JRException e) {
			logger.error("ERROR During covert JRXmlReport: {}", e);
			
			throw new PersistenceException(String.format(
					"Can't parse ReportMasterTemplateBody (id=%d)",
					masterBody.getId()));
		}

		checkNotNull(resultBodyCompiled);

		ReportTemplate resultEntity = reportTemplateRepository
				.save(reportTemplate);

		reportTemplateService.saveReportTemplateBodyCompiled(
				resultEntity.getId(), resultBodyCompiled,
				masterBody.getBodyFilename());

		return resultEntity;
	}

}
