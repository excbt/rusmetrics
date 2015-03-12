package ru.excbt.datafuse.nmk.data.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import org.joda.time.DateTime;

@Embeddable
public class RowAudit {

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "modified_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt;

	@Column(name = "created_by")
	@Min (0L)
	private Long createdBy;

	@Column(name = "modified_by")
	@Min (0L)	
	private Long modifiedBy;

	public DateTime getCreatedAt() {
		return new DateTime(createdAt);
	}

	public DateTime getModifiedAt() {
		return new DateTime(modifiedAt);
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt.toDate();
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public static RowAudit newInstanceNow() {
		RowAudit result = new RowAudit();
		result.createdAt = new Date();
		return result;
	}

	public static RowAudit newInstanceNow(Long createdBy) {
		RowAudit result = new RowAudit();
		result.createdAt = new Date();
		result.createdBy = createdBy;
		return result;
	}
	
}
