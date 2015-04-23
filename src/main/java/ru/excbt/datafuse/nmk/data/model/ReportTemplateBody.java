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
	private byte[] body;

	@Column(name = "report_template_body_compiled")
	private byte[] bodyCompiled;

	@Version
	private int version;

	@Column(name = "report_template_body_filename")
	private String bodyFilename;

	@Column(name = "report_template_body_compiled_filename")
	private String bodyCompiledFilename;
	
	public Long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(Long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public byte[] getBody() {
		return body == null ? null : Arrays.copyOf(
				body, body.length);
	}

	public void setBody(byte[] reportTemplateBody) {
		if (reportTemplateBody == null) {
			this.body = null;
		} else {
			this.body = Arrays.copyOf(reportTemplateBody,
					reportTemplateBody.length);
		}

	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getBodyCompiled() {
		return bodyCompiled == null ? null : Arrays.copyOf(
				bodyCompiled, bodyCompiled.length);
	}

	public void setBodyCompiled(byte[] reportTemplateBodyCompiled) {
		if (reportTemplateBodyCompiled == null) {
			this.bodyCompiled = null;
		} else {
			this.bodyCompiled = Arrays.copyOf(
					reportTemplateBodyCompiled,
					reportTemplateBodyCompiled.length);
		}
	}

	public String getBodyFilename() {
		return bodyFilename;
	}

	public void setBodyFilename(String reportTemplateBodyFilename) {
		this.bodyFilename = reportTemplateBodyFilename;
	}

	public String getBodyCompiledFilename() {
		return bodyCompiledFilename;
	}

	public void setBodyCompiledFilename(
			String reportTemplateBodyCompiledFilename) {
		this.bodyCompiledFilename = reportTemplateBodyCompiledFilename;
	}
}
