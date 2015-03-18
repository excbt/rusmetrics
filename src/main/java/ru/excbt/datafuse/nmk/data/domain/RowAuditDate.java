package ru.excbt.datafuse.nmk.data.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import org.joda.time.DateTime;

@Embeddable
public class RowAuditDate {

	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "last_modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@Column(name = "created_by")
	@Min (0L)
	private Long createdBy;

	@Column(name = "last_modified_by")
	@Min (0L)	
	private Long lastModifiedBy;

	public DateTime getCreatedDate() {
		return new DateTime(createdDate);
	}

	public DateTime getLastModifiedDate() {
		return new DateTime(lastModifiedDate);
	}

	public void setLastModifiedAt(final DateTime arg) {
		this.lastModifiedDate = arg.toDate();
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(final Long arg) {
		this.lastModifiedBy = arg;
	}

	public static RowAuditDate newInstanceNow() {
		RowAuditDate result = new RowAuditDate();
		result.createdDate = new Date();
		return result;
	}

	public static RowAuditDate newInstanceNow(Long createdBy) {
		RowAuditDate result = new RowAuditDate();
		result.createdDate = new Date();
		result.createdBy = createdBy;
		return result;
	}
	
}
