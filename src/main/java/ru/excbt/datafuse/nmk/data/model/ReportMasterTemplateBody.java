package ru.excbt.datafuse.nmk.data.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Главный шаблон отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
@Entity
@Table(name = "report_master_template_body")
@DynamicUpdate
public class ReportMasterTemplateBody extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3004780889383448179L;

	/**
	 * 
	 */

	@Column(name = "report_type")
	private String reportTypeKeyname;

	@Column(name = "body")
	private byte[] body;

	@Column(name = "body_compiled")
	private byte[] bodyCompiled;

	@Column(name = "body_filename")
	private String bodyFilename;

	@Column(name = "body_compiled_filename")
	private String bodyCompiledFilename;

	@Version
	private int version;

	public byte[] getBody() {
		return body == null ? null : Arrays.copyOf(body, body.length);
	}

	public void setBody(byte[] reportTemplateBody) {
		if (reportTemplateBody == null) {
			this.body = null;
		} else {
			this.body = Arrays.copyOf(reportTemplateBody, reportTemplateBody.length);
		}

	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getBodyCompiled() {
		return bodyCompiled == null ? null : Arrays.copyOf(bodyCompiled, bodyCompiled.length);
	}

	public void setBodyCompiled(byte[] bodyCompiled) {
		if (bodyCompiled == null) {
			this.bodyCompiled = null;
		} else {
			this.bodyCompiled = Arrays.copyOf(bodyCompiled, bodyCompiled.length);
		}
	}

	public String getBodyFilename() {
		return bodyFilename;
	}

	public void setBodyFilename(String bodyFilename) {
		this.bodyFilename = bodyFilename;
	}

	public String getBodyCompiledFilename() {
		return bodyCompiledFilename;
	}

	public void setBodyCompiledFilename(String bodyCompiledFilename) {
		this.bodyCompiledFilename = bodyCompiledFilename;
	}

	public String getReportTypeKeyname() {
		return reportTypeKeyname;
	}

	public void setReportTypeKeyname(String reportTypeKeyname) {
		this.reportTypeKeyname = reportTypeKeyname;
	}

}
