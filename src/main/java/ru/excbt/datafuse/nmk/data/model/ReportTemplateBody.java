package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

/**
 * Файл шаблона отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@Entity
@Table(name = "report_template_body")
@DynamicUpdate
@EntityListeners({ AuditingEntityListener.class })
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

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	@JsonIgnore
	private Long createdBy;

	@CreatedDate
	@Column(name = "created_date", insertable = false, updatable = false)
	@JsonIgnore
	private Instant createdDate = Instant.now();

	@LastModifiedBy
	@Column(name = "last_modified_by")
	@JsonIgnore
	private Long lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_date")
	@JsonIgnore
	private Instant lastModifiedDate = Instant.now();

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

	public boolean isNew() {
		return reportTemplateId == null;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant creationDate) {
		this.createdDate = creationDate;
	}

	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(Long lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
