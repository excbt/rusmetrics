package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "report_template_body")
@DynamicUpdate
@EntityListeners({ AuditingEntityListener.class })
public class ReportTemplateBody implements Serializable,
		Auditable<AuditUser, Long> {

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

	@ManyToOne
	@JoinColumn(name = "created_by", updatable = false)
	@JsonIgnore
	private AuditUser createdBy;

	@Column(name = "created_date", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdDate;

	@ManyToOne
	@JoinColumn(name = "last_modified_by")
	@JsonIgnore
	private AuditUser lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_modified_date")
	@JsonIgnore
	private Date lastModifiedDate;

	public Long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(Long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public byte[] getBody() {
		return body == null ? null : Arrays.copyOf(body, body.length);
	}

	public void setBody(byte[] body) {
		if (body == null) {
			this.body = null;
		} else {
			this.body = Arrays.copyOf(body, body.length);
		}

	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getBodyCompiled() {
		return bodyCompiled == null ? null : Arrays.copyOf(bodyCompiled,
				bodyCompiled.length);
	}

	public void setBodyCompiled(byte[] bodyCompiled) {
		if (bodyCompiled == null) {
			this.bodyCompiled = null;
		} else {
			this.bodyCompiled = Arrays
					.copyOf(bodyCompiled, bodyCompiled.length);
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

	@Override
	public Long getId() {
		return reportTemplateId;
	}

	@Override
	public boolean isNew() {
		return reportTemplateId == null;
	}

	@Override
	public AuditUser getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(AuditUser createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public DateTime getCreatedDate() {
		return new DateTime(createdDate);
	}

	@Override
	public void setCreatedDate(DateTime creationDate) {
		this.createdDate = creationDate != null ? creationDate.toDate() : null;
	}

	@Override
	public AuditUser getLastModifiedBy() {
		return lastModifiedBy;
	}

	@Override
	public void setLastModifiedBy(AuditUser lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public DateTime getLastModifiedDate() {
		return new DateTime(lastModifiedDate);
	}

	@Override
	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate != null ? lastModifiedDate
				.toDate() : null;
	}
}
