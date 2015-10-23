package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamSpecialType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

@Entity
@Table(name = "report_meta_param_special")
@JsonInclude(Include.NON_NULL)
public class ReportMetaParamSpecial extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4449509566250004761L;

	@Column(name = "report_type")
	private String reportTypeKeyname;

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

	@Column(name = "param_special_name1")
	private String paramSpecialName1;

	@Column(name = "param_special_name2")
	private String paramSpecialName2;

	@Column(name = "param_special_default_value1")
	private String paramSpecialDefaultValue1;

	@Column(name = "param_special_default_value2")
	private String paramSpecialDefaultValue2;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	public ReportType getReportType() {
		return reportType;
	}

	public ReportMetaParamSpecialType getParamSpecialType() {
		return paramSpecialType;
	}

	public String getParamSpecialKeyname() {
		return paramSpecialKeyname;
	}

	public String getParamSpecialCaption() {
		return paramSpecialCaption;
	}

	public Boolean getParamSpecialRequired() {
		return paramSpecialRequired;
	}

	public String getParamSpecialName1() {
		return paramSpecialName1;
	}

	public String getParamSpecialName2() {
		return paramSpecialName2;
	}

	public String getParamSpecialDefaultValue1() {
		return paramSpecialDefaultValue1;
	}

	public String getParamSpecialDefaultValue2() {
		return paramSpecialDefaultValue2;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public String getReportTypeKeyname() {
		return reportTypeKeyname;
	}

}
