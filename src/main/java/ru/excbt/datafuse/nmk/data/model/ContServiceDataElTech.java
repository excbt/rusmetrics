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
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

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

	@Column(name = "u_2")
	private BigDecimal u2;

	@Column(name = "u_3")
	private BigDecimal u3;

	@Column(name = "i_1")
	private BigDecimal i1;

	@Column(name = "i_2")
	private BigDecimal i2;

	@Column(name = "i_3")
	private BigDecimal i3;

	@Column(name = "phase_1")
	private BigDecimal phase1;

	@Column(name = "phase_2")
	private BigDecimal phase2;

	@Column(name = "phase_3")
	private BigDecimal phase3;

	@Column(name = "frequency")
	private BigDecimal frequency;

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

	public BigDecimal getU2() {
		return u2;
	}

	public void setU2(BigDecimal u2) {
		this.u2 = u2;
	}

	public BigDecimal getU3() {
		return u3;
	}

	public void setU3(BigDecimal u3) {
		this.u3 = u3;
	}

	public BigDecimal getI1() {
		return i1;
	}

	public void setI1(BigDecimal i1) {
		this.i1 = i1;
	}

	public BigDecimal getI2() {
		return i2;
	}

	public void setI2(BigDecimal i2) {
		this.i2 = i2;
	}

	public BigDecimal getI3() {
		return i3;
	}

	public void setI3(BigDecimal i3) {
		this.i3 = i3;
	}

	public BigDecimal getPhase1() {
		return phase1;
	}

	public void setPhase1(BigDecimal phase1) {
		this.phase1 = phase1;
	}

	public BigDecimal getPhase2() {
		return phase2;
	}

	public void setPhase2(BigDecimal phase2) {
		this.phase2 = phase2;
	}

	public BigDecimal getPhase3() {
		return phase3;
	}

	public void setPhase3(BigDecimal phase3) {
		this.phase3 = phase3;
	}

	public BigDecimal getFrequency() {
		return frequency;
	}

	public void setFrequency(BigDecimal frequency) {
		this.frequency = frequency;
	}

	public BigDecimal getDeviceTemp() {
		return deviceTemp;
	}

	public void setDeviceTemp(BigDecimal deviceTemp) {
		this.deviceTemp = deviceTemp;
	}

	/**
	 * 
	 * @return
	 */
	public String getDataDateString() {

		TimeDetailKey timeDetailKey = TimeDetailKey.searchKeyname(this.timeDetailType);
		if (timeDetailKey != null && timeDetailKey.isTruncDate()) {
			return DateFormatUtils.formatDateTime(this.dataDate, DateFormatUtils.DATE_FORMAT_STR_TRUNC);
		}

		return DateFormatUtils.formatDateTime(this.dataDate, DateFormatUtils.DATE_FORMAT_STR_FULL);
	}
}
