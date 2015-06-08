package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;

@Entity
@Table(name="report_meta_param_common")
public class ReportMetaParamCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -569411608287416733L;

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeKey reportTypeKey;
	
	@Column(name="one_date_required")
	private Boolean oneDateRequired;
	
	@Column(name="start_date_required")
	private Boolean startDateRequired;
	
	@Column(name="end_date_required")
	private Boolean endDateRequired;
	
	@Column(name="one_cont_object_required")
	private Boolean oneContObjectRequired;
	
	@Column(name="many_cont_objects_required")
	private Boolean manyContObjectsRequired;
	
	@Column(name="many_cont_objects_zip_only")
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
	
}
