package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

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
	
	@Version
	private int version;

	public Long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(Long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public byte[] getReportTemplateBody() {
		return reportTemplateBody;
	}

	public void setReportTemplateBody(byte[] reportTemplateBody) {
		this.reportTemplateBody = reportTemplateBody;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
