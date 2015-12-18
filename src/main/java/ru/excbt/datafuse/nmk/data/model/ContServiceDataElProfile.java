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
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;

@Entity
@Table(name = "cont_service_data_el_profile")
public class ContServiceDataElProfile extends AbstractAuditableModel implements DataDateFormatter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7484466554217659824L;

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

	@JsonIgnore
	@Version
	private int version;

	@Column(name = "profile_interval")
	private Integer profileInterval;

	@Column(name = "p_ap")
	private BigDecimal p_Ap;

	@Column(name = "p_an")
	private BigDecimal p_An;

	@Column(name = "q_rp")
	private BigDecimal q_Rp;

	@Column(name = "q_rn")
	private BigDecimal q_Rn;

	@Override
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

	@Override
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

	public Integer getProfileInterval() {
		return profileInterval;
	}

	public void setProfileInterval(Integer profileInterval) {
		this.profileInterval = profileInterval;
	}

	public BigDecimal getP_Ap() {
		return p_Ap;
	}

	public void setP_Ap(BigDecimal p_Ap) {
		this.p_Ap = p_Ap;
	}

	public BigDecimal getP_An() {
		return p_An;
	}

	public void setP_An(BigDecimal p_An) {
		this.p_An = p_An;
	}

	public BigDecimal getQ_Rp() {
		return q_Rp;
	}

	public void setQ_Rp(BigDecimal q_Rp) {
		this.q_Rp = q_Rp;
	}

	public BigDecimal getQ_Rn() {
		return q_Rn;
	}

	public void setQ_Rn(BigDecimal q_Rn) {
		this.q_Rn = q_Rn;
	}

}
