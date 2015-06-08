package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamSpecialType;

@Entity
@Table(name="report_meta_param_special")
public class ReportMetaParamSpecial extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4449509566250004761L;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeKey reportTypeKey;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "special_param_type")	
	private ReportMetaParamSpecialType specialParamType;
	
	@Column(name = "special_param_keyname")
	private String specialParamKeyname;
	
	@Column(name = "special_param_caption")
	private String specialParamCaption;
	
	@Column(name = "special_param_required")
	private Boolean specialParamRequired;

	public ReportTypeKey getReportTypeKey() {
		return reportTypeKey;
	}

	public void setReportTypeKey(ReportTypeKey reportTypeKey) {
		this.reportTypeKey = reportTypeKey;
	}

	public ReportMetaParamSpecialType getSpecialParamType() {
		return specialParamType;
	}

	public void setSpecialParamType(ReportMetaParamSpecialType specialParamType) {
		this.specialParamType = specialParamType;
	}

	public String getSpecialParamKeyname() {
		return specialParamKeyname;
	}

	public void setSpecialParamKeyname(String specialParamKeyname) {
		this.specialParamKeyname = specialParamKeyname;
	}

	public String getSpecialParamCaption() {
		return specialParamCaption;
	}

	public void setSpecialParamCaption(String specialParamCaption) {
		this.specialParamCaption = specialParamCaption;
	}

	public Boolean getSpecialParamRequired() {
		return specialParamRequired;
	}

	public void setSpecialParamRequired(Boolean specialParamRequired) {
		this.specialParamRequired = specialParamRequired;
	}
	
}
