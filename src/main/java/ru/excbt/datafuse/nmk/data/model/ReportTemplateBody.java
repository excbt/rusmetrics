package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "report_template_body")
public class ReportTemplateBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -290908119102051460L;

	@Id
	@Column(name = "report_template_id")
	private Long reportTemplateId;

	@Column(name = "report_template_body")
	private byte[] reportTemplateBody;

	@Column(name = "report_template_body_compiled")
	private byte[] reportTemplateBodyCompiled;

	@Version
	private int version;

	@Column(name = "report_template_body_filename")
	private String reportTemplateBodyFilename;

	@Column(name = "report_template_body_compiled_filename")
	private String reportTemplateBodyCompiledFilename;
	
	public Long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(Long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public byte[] getReportTemplateBody() {
		return reportTemplateBody == null ? null : Arrays.copyOf(
				reportTemplateBody, reportTemplateBody.length);
	}

	public void setReportTemplateBody(byte[] reportTemplateBody) {
		if (reportTemplateBody == null) {
			this.reportTemplateBody = null;
		} else {
			this.reportTemplateBody = Arrays.copyOf(reportTemplateBody,
					reportTemplateBody.length);
		}

	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getReportTemplateBodyCompiled() {
		return reportTemplateBodyCompiled == null ? null : Arrays.copyOf(
				reportTemplateBodyCompiled, reportTemplateBodyCompiled.length);
	}

	public void setReportTemplateBodyCompiled(byte[] reportTemplateBodyCompiled) {
		if (reportTemplateBodyCompiled == null) {
			this.reportTemplateBodyCompiled = null;
		} else {
			this.reportTemplateBodyCompiled = Arrays.copyOf(
					reportTemplateBodyCompiled,
					reportTemplateBodyCompiled.length);
		}
	}

	public String getReportTemplateBodyFilename() {
		return reportTemplateBodyFilename;
	}

	public void setReportTemplateBodyFilename(String reportTemplateBodyFilename) {
		this.reportTemplateBodyFilename = reportTemplateBodyFilename;
	}

	public String getReportTemplateBodyCompiledFilename() {
		return reportTemplateBodyCompiledFilename;
	}

	public void setReportTemplateBodyCompiledFilename(
			String reportTemplateBodyCompiledFilename) {
		this.reportTemplateBodyCompiledFilename = reportTemplateBodyCompiledFilename;
	}
}
