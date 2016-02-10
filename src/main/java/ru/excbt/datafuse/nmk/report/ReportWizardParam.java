package ru.excbt.datafuse.nmk.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

/**
 * Параметр конструктора отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.04.2015
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportWizardParam {

	private ReportTemplate reportTemplate = new ReportTemplate();

	private ReportColumnSettings reportColumnSettings = new ReportColumnSettings();

	public ReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public ReportColumnSettings getReportColumnSettings() {
		return reportColumnSettings;
	}

	public void setReportColumnSettings(ReportColumnSettings reportColumnSettings) {
		this.reportColumnSettings = reportColumnSettings;
	}

}
