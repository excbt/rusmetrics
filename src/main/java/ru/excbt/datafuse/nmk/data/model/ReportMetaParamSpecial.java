package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamSpecialType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name="report_meta_param_special")
@JsonInclude(Include.NON_NULL)
public class ReportMetaParamSpecial extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4449509566250004761L;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeKey reportTypeKey;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	@JsonIgnore
	private ReportType reportType;	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "param_special_type")	
	private ReportMetaParamSpecialType paramSpecialType;
	
	@Column(name = "param_special_keyname")
	private String paramSpecialKeyname;
	
	@Column(name = "param_special_caption")
	private String paramSpecialCaption;
	
	@Column(name = "param_special_required")
	private Boolean paramSpecialRequired;

	@Column(name = "param_special_name")
	private String paramSpecialName;
	
	public ReportTypeKey getReportTypeKey() {
		return reportTypeKey;
	}

	public void setReportTypeKey(ReportTypeKey reportTypeKey) {
		this.reportTypeKey = reportTypeKey;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public ReportMetaParamSpecialType getParamSpecialType() {
		return paramSpecialType;
	}

	public void setParamSpecialType(ReportMetaParamSpecialType paramSpecialType) {
		this.paramSpecialType = paramSpecialType;
	}

	public String getParamSpecialKeyname() {
		return paramSpecialKeyname;
	}

	public void setParamSpecialKeyname(String paramSpecialKeyname) {
		this.paramSpecialKeyname = paramSpecialKeyname;
	}

	public String getParamSpecialCaption() {
		return paramSpecialCaption;
	}

	public void setParamSpecialCaption(String paramSpecialCaption) {
		this.paramSpecialCaption = paramSpecialCaption;
	}

	public Boolean getParamSpecialRequired() {
		return paramSpecialRequired;
	}

	public void setParamSpecialRequired(Boolean paramSpecialRequired) {
		this.paramSpecialRequired = paramSpecialRequired;
	}

	public String getParamSpecialName() {
		return paramSpecialName;
	}

	public void setParamSpecialName(String paramSpecialName) {
		this.paramSpecialName = paramSpecialName;
	}
	
}
