package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "report_meta_param_common")
@JsonInclude(Include.NON_NULL)
public class ReportMetaParamCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -569411608287416733L;

	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	@JsonIgnore
	private ReportType reportType;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeKey reportTypeKey;

	@Column(name = "one_date_required")
	private Boolean oneDateRequired;

	@Column(name = "start_date_required")
	private Boolean startDateRequired;

	@Column(name = "end_date_required")
	private Boolean endDateRequired;

	@Column(name = "one_cont_object_required")
	private Boolean oneContObjectRequired;

	@Column(name = "many_cont_objects_required")
	private Boolean manyContObjectsRequired;

	@Column(name = "many_cont_objects_zip_only")
	private Boolean manyContObjectsZipOnly;

	@Version
	private int version;

	public ReportTypeKey getReportTypeKey() {
		return reportTypeKey;
	}

	public void setReportTypeKey(ReportTypeKey reportTypeKey) {
		this.reportTypeKey = reportTypeKey;
	}

	public Boolean getOneDateRequired() {
		return oneDateRequired;
	}

	public void setOneDateRequired(Boolean oneDateRequired) {
		this.oneDateRequired = oneDateRequired;
	}

	public Boolean getStartDateRequired() {
		return startDateRequired;
	}

	public void setStartDateRequired(Boolean startDateRequired) {
		this.startDateRequired = startDateRequired;
	}

	public Boolean getEndDateRequired() {
		return endDateRequired;
	}

	public void setEndDateRequired(Boolean endDateRequired) {
		this.endDateRequired = endDateRequired;
	}

	public Boolean getOneContObjectRequired() {
		return oneContObjectRequired;
	}

	public void setOneContObjectRequired(Boolean oneContObjectRequired) {
		this.oneContObjectRequired = oneContObjectRequired;
	}

	public Boolean getManyContObjectsZipOnly() {
		return manyContObjectsZipOnly;
	}

	public void setManyContObjectsZipOnly(Boolean manyContObjectsZipOnly) {
		this.manyContObjectsZipOnly = manyContObjectsZipOnly;
	}

	public Boolean getManyContObjectsRequired() {
		return manyContObjectsRequired;
	}

	public void setManyContObjectsRequired(Boolean manyContObjectsRequired) {
		this.manyContObjectsRequired = manyContObjectsRequired;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endDateRequired == null) ? 0 : endDateRequired.hashCode());
		result = prime
				* result
				+ ((manyContObjectsRequired == null) ? 0
						: manyContObjectsRequired.hashCode());
		result = prime
				* result
				+ ((manyContObjectsZipOnly == null) ? 0
						: manyContObjectsZipOnly.hashCode());
		result = prime
				* result
				+ ((oneContObjectRequired == null) ? 0 : oneContObjectRequired
						.hashCode());
		result = prime * result
				+ ((oneDateRequired == null) ? 0 : oneDateRequired.hashCode());
		result = prime * result
				+ ((reportType == null) ? 0 : reportType.hashCode());
		result = prime * result
				+ ((reportTypeKey == null) ? 0 : reportTypeKey.hashCode());
		result = prime
				* result
				+ ((startDateRequired == null) ? 0 : startDateRequired
						.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportMetaParamCommon other = (ReportMetaParamCommon) obj;
		if (endDateRequired == null) {
			if (other.endDateRequired != null)
				return false;
		} else if (!endDateRequired.equals(other.endDateRequired))
			return false;
		if (manyContObjectsRequired == null) {
			if (other.manyContObjectsRequired != null)
				return false;
		} else if (!manyContObjectsRequired
				.equals(other.manyContObjectsRequired))
			return false;
		if (manyContObjectsZipOnly == null) {
			if (other.manyContObjectsZipOnly != null)
				return false;
		} else if (!manyContObjectsZipOnly.equals(other.manyContObjectsZipOnly))
			return false;
		if (oneContObjectRequired == null) {
			if (other.oneContObjectRequired != null)
				return false;
		} else if (!oneContObjectRequired.equals(other.oneContObjectRequired))
			return false;
		if (oneDateRequired == null) {
			if (other.oneDateRequired != null)
				return false;
		} else if (!oneDateRequired.equals(other.oneDateRequired))
			return false;
		if (reportType == null) {
			if (other.reportType != null)
				return false;
		} else if (!reportType.equals(other.reportType))
			return false;
		if (reportTypeKey != other.reportTypeKey)
			return false;
		if (startDateRequired == null) {
			if (other.startDateRequired != null)
				return false;
		} else if (!startDateRequired.equals(other.startDateRequired))
			return false;
		if (version != other.version)
			return false;
		return true;
	}



}
