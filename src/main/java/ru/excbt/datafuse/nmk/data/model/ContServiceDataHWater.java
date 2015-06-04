package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "cont_service_data_hwater")
public class ContServiceDataHWater extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6897555657365451006L;

	@Column(name = "data_date")
	private Date dataDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	@JsonIgnore
	private DeviceObject deviceObject;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_zpoint_id", insertable = false, updatable = false)
	@JsonIgnore
	private ContZPoint contZPoint;
	
	@Column(name = "cont_zpoint_id")
	@JsonIgnore
	private Long contZPointId;

	@Column(name = "time_detail_type")
	private String timeDetailType;

	@Version
	private int version;

	@Column
	private BigDecimal t_in;

	@Column
	private BigDecimal t_out;

	@Column
	private BigDecimal t_cold;

	@Column
	private BigDecimal t_outdoor;

	@Column
	private BigDecimal m_in;

	@Column
	private BigDecimal m_out;

	@Column
	private BigDecimal m_delta;

	@Column
	private BigDecimal v_in;

	@Column
	private BigDecimal v_out;

	@Column
	private BigDecimal v_delta;

	@Column
	private BigDecimal h_in;

	@Column
	private BigDecimal h_out;

	@Column
	private BigDecimal h_delta;

	@Column
	private BigDecimal p_in;

	@Column
	private BigDecimal p_out;

	@Column
	private BigDecimal p_delta;

	@Column(name = "work_time")
	private BigDecimal workTime;

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public ContZPoint getContZPoint() {
		return contZPoint;
	}

	public void setContZPoint(ContZPoint contZPoint) {
		this.contZPoint = contZPoint;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public void setTimeDetailType(String timeDetailType) {
		this.timeDetailType = timeDetailType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public BigDecimal getT_in() {
		return t_in;
	}

	public void setT_in(BigDecimal t_in) {
		this.t_in = t_in;
	}

	public BigDecimal getT_out() {
		return t_out;
	}

	public void setT_out(BigDecimal t_out) {
		this.t_out = t_out;
	}

	public BigDecimal getT_cold() {
		return t_cold;
	}

	public void setT_cold(BigDecimal t_cold) {
		this.t_cold = t_cold;
	}

	public BigDecimal getT_outdoor() {
		return t_outdoor;
	}

	public void setT_outdoor(BigDecimal t_outdoor) {
		this.t_outdoor = t_outdoor;
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

	public BigDecimal getP_in() {
		return p_in;
	}

	public void setP_in(BigDecimal p_in) {
		this.p_in = p_in;
	}

	public BigDecimal getP_out() {
		return p_out;
	}

	public void setP_out(BigDecimal p_out) {
		this.p_out = p_out;
	}

	public BigDecimal getP_delta() {
		return p_delta;
	}

	public void setP_delta(BigDecimal p_delta) {
		this.p_delta = p_delta;
	}

	public BigDecimal getWorkTime() {
		return workTime;
	}

	public void setWorkTime(BigDecimal workTime) {
		this.workTime = workTime;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public void setContZPointId(Long contZPointId) {
		this.contZPointId = contZPointId;
	}

}
