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

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "cont_service_data_el_tech")
public class ContServiceDataElTech extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1354288648021141991L;

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

	@Column(name = "u_1")
	private BigDecimal u1;

	@Column(name = "f_1")
	private BigDecimal f1;

	@Column(name = "u_2")
	private BigDecimal u2;

	@Column(name = "f_2")
	private BigDecimal f2;

	@Column(name = "u_3")
	private BigDecimal u3;

	@Column(name = "f_3")
	private BigDecimal f3;

	@Column(name = "device_temp")
	private BigDecimal deviceTemp;

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

	public Long getContZPointId() {
		return contZPointId;
	}

	public void setContZPointId(Long contZPointId) {
		this.contZPointId = contZPointId;
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

	public BigDecimal getU1() {
		return u1;
	}

	public void setU1(BigDecimal u1) {
		this.u1 = u1;
	}

	public BigDecimal getF1() {
		return f1;
	}

	public void setF1(BigDecimal f1) {
		this.f1 = f1;
	}

	public BigDecimal getU2() {
		return u2;
	}

	public void setU2(BigDecimal u2) {
		this.u2 = u2;
	}

	public BigDecimal getF2() {
		return f2;
	}

	public void setF2(BigDecimal f2) {
		this.f2 = f2;
	}

	public BigDecimal getU3() {
		return u3;
	}

	public void setU3(BigDecimal u3) {
		this.u3 = u3;
	}

	public BigDecimal getF3() {
		return f3;
	}

	public void setF3(BigDecimal f3) {
		this.f3 = f3;
	}

	public BigDecimal getDeviceTemp() {
		return deviceTemp;
	}

	public void setDeviceTemp(BigDecimal deviceTemp) {
		this.deviceTemp = deviceTemp;
	}
}
