package ru.excbt.datafuse.nmk.report.model;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	public void setReportColumnSettings(
			ReportColumnSettings reportColumnSettings) {
		this.reportColumnSettings = reportColumnSettings;
	}

}
