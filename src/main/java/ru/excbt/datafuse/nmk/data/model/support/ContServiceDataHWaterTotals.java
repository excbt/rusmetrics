package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ContServiceDataHWaterTotals implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 616264830719948979L;

	private Long contZPointId;

	private Date beginDate;

	private Date endDate;

	private String timeDetailType;

	private BigDecimal m_in;

	private BigDecimal m_out;

	private BigDecimal m_delta;

	private BigDecimal v_in;

	private BigDecimal v_out;

	private BigDecimal v_delta;

	private BigDecimal h_in;

	private BigDecimal h_out;

	private BigDecimal h_delta;

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public void setTimeDetailType(String timeDetailType) {
		this.timeDetailType = timeDetailType;
	}

	public BigDecimal getM_in() {
		return m_in;
	}

	public void setM_in(BigDecimal m_in) {
		this.m_in = m_in;
	}

	public BigDecimal getM_out() {
		return m_out;
	}

	public void setM_out(BigDecimal m_out) {
		this.m_out = m_out;
	}

	public BigDecimal getM_delta() {
		return m_delta;
	}

	public void setM_delta(BigDecimal m_delta) {
		this.m_delta = m_delta;
	}

	public BigDecimal getH_in() {
		return h_in;
	}

	public void setH_in(BigDecimal h_in) {
		this.h_in = h_in;
	}

	public BigDecimal getH_out() {
		return h_out;
	}

	public void setH_out(BigDecimal h_out) {
		this.h_out = h_out;
	}

	public BigDecimal getH_delta() {
		return h_delta;
	}

	public void setH_delta(BigDecimal h_delta) {
		this.h_delta = h_delta;
	}

	public BigDecimal getV_in() {
		return v_in;
	}

	public void setV_in(BigDecimal v_in) {
		this.v_in = v_in;
	}

	public BigDecimal getV_out() {
		return v_out;
	}

	public void setV_out(BigDecimal v_out) {
		this.v_out = v_out;
	}

	public BigDecimal getV_delta() {
		return v_delta;
	}

	public void setV_delta(BigDecimal v_delta) {
		this.v_delta = v_delta;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public void setContZPointId(Long contZPointId) {
		this.contZPointId = contZPointId;
	}

}
