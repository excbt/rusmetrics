package ru.excbt.datafuse.nmk.data.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Вариант отчета. Специальные параметры
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.06.2015
 *
 */
@Entity
@Table(name = "report_paramset_param_special")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReportParamsetParamSpecial extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -8266275292467053060L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_paramset_id")
	@JsonIgnore
	private ReportParamset reportParamset;

	@Column(name = "param_special_id")
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

	@Column(name = "directory_value")
	private String directoryValue;

	@Column(name = "bool_value")
	private Boolean boolValue;

	@Version
	private int version;

	// ************************************************************************
	//
	// ************************************************************************

	public static ReportParamsetParamSpecial newInstance(ReportMetaParamSpecial reportMetaParamSpecial) {
		checkNotNull(reportMetaParamSpecial);
		ReportParamsetParamSpecial result = new ReportParamsetParamSpecial();
		result.setReportMetaParamSpecialId(reportMetaParamSpecial.getId());
		return result;
	}

	/**
	 *
	 * @return
	 */
	@JsonIgnore
	public boolean isOneValueAssigned() {
		int i = 0;
		if (numericValue != null) {
			i++;
		}

		if (textValue != null) {
			i++;
		}

		if (startDateValue != null || endDateValue != null) {
			i++;
		}

		if (directoryValue != null) {
			i++;
		}

		if (boolValue != null) {
			i++;
		}
		return i == 1;
	}

	/**
	 *
	 * @return
	 */
	@JsonIgnore
	public boolean isAnyValueAssigned() {
		int i = 0;
		if (numericValue != null) {
			i++;
		}

		if (textValue != null) {
			i++;
		}

		if (startDateValue != null) {
			i++;
		}

		if (endDateValue != null) {
			i++;
		}

		if (directoryValue != null) {
			i++;
		}

		if (boolValue != null) {
			i++;
		}

		if (oneDateValue != null) {
			i++;
		}

		return i > 0;
	}

	@JsonIgnore
	public String getValuesAsString() {

		Map<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("textValue", textValue);
		valuesMap.put("numericValue", numericValue);
		valuesMap.put("oneDateValue", oneDateValue);
		valuesMap.put("startDateValue", startDateValue);
		valuesMap.put("endDateValue", endDateValue);
		valuesMap.put("directoryValue", directoryValue);
		valuesMap.put("boolValue", boolValue);

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> v : valuesMap.entrySet()) {
			if (v.getValue() != null) {
				sb.append(v.getKey());
				sb.append('=');
				sb.append(v.getValue().toString());
				sb.append("   ");
			}
		}
		return sb.toString();
	}

	/**
	 *
	 * @return
	 */
	@JsonIgnore
	public Map<String, Object> getValuesAsMap() {

		Map<String, Object> result = new HashMap<>();
		Map<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("textValue", getTextValue());
		valuesMap.put("numericValue", getNumericValue());
		valuesMap.put("oneDateValue", getOneDateValue());
		valuesMap.put("startDateValue", getStartDateValue());
		valuesMap.put("endDateValue", getEndDateValue());
		valuesMap.put("directoryValue", getDirectoryValue());
		valuesMap.put("boolValue", getBoolValue());

		for (Map.Entry<String, Object> v : valuesMap.entrySet()) {
			if (v.getValue() != null) {
				result.put(v.getKey(), v.getValue());
			}
		}
		return result;
	}

	/**
	 *
	 * @param src
	 * @return
	 */
	private Timestamp timestampFromDate(Date src) {
		if (src != null && src instanceof java.sql.Timestamp) {
			return (java.sql.Timestamp) src;
		}
		return src != null ? new java.sql.Timestamp(src.getTime()) : null;
	}

	// ************************************************************************
	//
	// ************************************************************************

	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	public void setReportParamset(ReportParamset reportParamset) {
		this.reportParamset = reportParamset;
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
		return timestampFromDate(oneDateValue);
	}

	public void setOneDateValue(Date oneDateValue) {
		this.oneDateValue = oneDateValue;
	}

	public Date getStartDateValue() {
		return timestampFromDate(startDateValue);
	}

	public void setStartDateValue(Date startDateValue) {
		this.startDateValue = startDateValue;
	}

	public Date getEndDateValue() {
		return timestampFromDate(endDateValue);
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

	public String getDirectoryValue() {
		return directoryValue;
	}

	public void setDirectoryValue(String directoryValue) {
		this.directoryValue = directoryValue;
	}

	public Boolean getBoolValue() {
		return boolValue;
	}

	public void setBoolValue(Boolean boolValue) {
		this.boolValue = boolValue;
	}

}
