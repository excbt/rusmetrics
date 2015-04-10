package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="report_paramset_group")
public class ReportParamsetGroup extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 107534288349056624L;
	
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "report_paramset_id")
	@JsonIgnore
	private ReportParamset reportParamset;
	
	@Column(name = "object_id")
	private Long objectId;

	@Version
	private int version;

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	public void setReportParamset(ReportParamset reportParamset) {
		this.reportParamset = reportParamset;
	}
}
