package ru.excbt.datafuse.nmk.data.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

@Entity
@Table(name = "report_template_body_master")
public class ReportTemplateBodyMaster extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3004780889383448179L;

	/**
	 * 
	 */

	@Column(name = "report_type")
	@Enumerated(EnumType.STRING)
	private ReportTypeKey reportTypeKey;
	
	@Column(name = "report_template_body")
	private byte[] body;

	@Column(name = "report_template_body_compiled")
	private byte[] bodyCompiled;

	@Column(name = "report_template_body_filename")
	private String bodyFilename;

	@Column(name = "report_template_body_compiled_filename")
	private String bodyCompiledFilename;
	
	@Version
	private int version;
	
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

	public ReportTypeKey getReportTypeKey() {
		return reportTypeKey;
	}

	public void setReportTypeKey(ReportTypeKey reportTypeKey) {
		this.reportTypeKey = reportTypeKey;
	}
}
