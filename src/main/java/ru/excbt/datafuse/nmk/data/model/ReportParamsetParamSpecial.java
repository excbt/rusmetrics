package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "report_paramset_param_special")
@SQLDelete(sql = "UPDATE report_paramset_param_special SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause = "deleted <> 1")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportParamsetParamSpecial extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8266275292467053060L;

	@ManyToOne
	@JoinColumn(name = "report_paramset_id")
	@JsonIgnore
	private ReportParamset reportParamset;

	@ManyToOne
	@JoinColumn(name = "param_special_id")
	private ReportMetaParamSpecial reportMetaParamSpecial;

	@Column(name = "param_special_id", insertable = false, updatable = false)
	private Long reportMetaParamSpecialId;

	@Column(name = "text_value")
	private String textValue;

	@Column(name = "numeric_value")
	private BigDecimal numericValue;

	@Column(name = "one_date_value")
	@Temporal(TemporalType.TIMESTAMP)
	private Date oneDateValue;

	@Column(name = "start_date_value")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDateValue;

	@Column(name = "end_date_value")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDateValue;

	@Version
	private int version;

	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	public void setReportParamset(ReportParamset reportParamset) {
		this.reportParamset = reportParamset;
	}

	public ReportMetaParamSpecial getReportMetaParamSpecial() {
		return reportMetaParamSpecial;
	}

	public void setReportMetaParamSpecial(
			ReportMetaParamSpecial reportMetaParamSpecial) {
		this.reportMetaParamSpecial = reportMetaParamSpecial;
	}

	public Long getReportMetaParamSpecialId() {
		return reportMetaParamSpecialId;
	}

	public void setReportMetaParamSpecialId(Long reportMetaParamSpecialId) {
		this.reportMetaParamSpecialId = reportMetaParamSpecialId;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public BigDecimal getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(BigDecimal numericValue) {
		this.numericValue = numericValue;
	}

	public Date getOneDateValue() {
		return oneDateValue;
	}

	public void setOneDateValue(Date oneDateValue) {
		this.oneDateValue = oneDateValue;
	}

	public Date getStartDateValue() {
		return startDateValue;
	}

	public void setStartDateValue(Date startDateValue) {
		this.startDateValue = startDateValue;
	}

	public Date getEndDateValue() {
		return endDateValue;
	}

	public void setEndDateValue(Date endDateValue) {
		this.endDateValue = endDateValue;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
