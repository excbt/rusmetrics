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
@Table(name = "cont_service_data_el_cons")
public class ContServiceDataElCons extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2765914644167033303L;

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

	// 1	
	@Column(name = "p_ap_1")
	private BigDecimal p_Ar1;

	@Column(name = "p_an_1")
	private BigDecimal p_An1;

	@Column(name = "q_rp_1")
	private BigDecimal q_Rp1;

	@Column(name = "q_rn_1")
	private BigDecimal q_Rn1;

	// 2
	@Column(name = "p_ap_2")
	private BigDecimal p_Ar2;

	@Column(name = "p_an_2")
	private BigDecimal p_An2;

	@Column(name = "q_rp_2")
	private BigDecimal q_Rp2;

	@Column(name = "q_rn_2")
	private BigDecimal q_Rn2;

	// 3
	@Column(name = "p_ap_3")
	private BigDecimal p_Ar3;

	@Column(name = "p_an_3")
	private BigDecimal p_An3;

	@Column(name = "q_rp_3")
	private BigDecimal q_Rp3;

	@Column(name = "q_rn_3")
	private BigDecimal q_Rn3;

	// 4
	@Column(name = "p_ap_4")
	private BigDecimal p_Ar4;

	@Column(name = "p_an_4")
	private BigDecimal p_An4;

	@Column(name = "q_rp_4")
	private BigDecimal q_Ap4;

	@Column(name = "q_rn_4")
	private BigDecimal q_An4;

	// All
	@Column(name = "p_ap")
	private BigDecimal p_Ar;

	@Column(name = "p_an")
	private BigDecimal p_An;

	@Column(name = "q_rp")
	private BigDecimal q_Rp;

	@Column(name = "q_rn")
	private BigDecimal q_Rn;

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

	public BigDecimal getP_Ar1() {
		return p_Ar1;
	}

	public void setP_Ar1(BigDecimal p_Ar1) {
		this.p_Ar1 = p_Ar1;
	}

	public BigDecimal getP_An1() {
		return p_An1;
	}

	public void setP_An1(BigDecimal p_An1) {
		this.p_An1 = p_An1;
	}

	public BigDecimal getQ_Rp1() {
		return q_Rp1;
	}

	public void setQ_Rp1(BigDecimal q_Rp1) {
		this.q_Rp1 = q_Rp1;
	}

	public BigDecimal getQ_Rn1() {
		return q_Rn1;
	}

	public void setQ_Rn1(BigDecimal q_Rn1) {
		this.q_Rn1 = q_Rn1;
	}

	public BigDecimal getP_Ar2() {
		return p_Ar2;
	}

	public void setP_Ar2(BigDecimal p_Ar2) {
		this.p_Ar2 = p_Ar2;
	}

	public BigDecimal getP_An2() {
		return p_An2;
	}

	public void setP_An2(BigDecimal p_An2) {
		this.p_An2 = p_An2;
	}

	public BigDecimal getQ_Rp2() {
		return q_Rp2;
	}

	public void setQ_Rp2(BigDecimal q_Rp2) {
		this.q_Rp2 = q_Rp2;
	}

	public BigDecimal getQ_Rn2() {
		return q_Rn2;
	}

	public void setQ_Rn2(BigDecimal q_Rn2) {
		this.q_Rn2 = q_Rn2;
	}

	public BigDecimal getP_Ar3() {
		return p_Ar3;
	}

	public void setP_Ar3(BigDecimal p_Ar3) {
		this.p_Ar3 = p_Ar3;
	}

	public BigDecimal getP_An3() {
		return p_An3;
	}

	public void setP_An3(BigDecimal p_An3) {
		this.p_An3 = p_An3;
	}

	public BigDecimal getQ_Rp3() {
		return q_Rp3;
	}

	public void setQ_Rp3(BigDecimal q_Rp3) {
		this.q_Rp3 = q_Rp3;
	}

	public BigDecimal getQ_Rn3() {
		return q_Rn3;
	}

	public void setQ_Rn3(BigDecimal q_Rn3) {
		this.q_Rn3 = q_Rn3;
	}

	public BigDecimal getP_Ar4() {
		return p_Ar4;
	}

	public void setP_Ar4(BigDecimal p_Ar4) {
		this.p_Ar4 = p_Ar4;
	}

	public BigDecimal getP_An4() {
		return p_An4;
	}

	public void setP_An4(BigDecimal p_An4) {
		this.p_An4 = p_An4;
	}

	public BigDecimal getQ_Ap4() {
		return q_Ap4;
	}

	public void setQ_Ap4(BigDecimal q_Ap4) {
		this.q_Ap4 = q_Ap4;
	}

	public BigDecimal getQ_An4() {
		return q_An4;
	}

	public void setQ_An4(BigDecimal q_An4) {
		this.q_An4 = q_An4;
	}

	public BigDecimal getP_Ar() {
		return p_Ar;
	}

	public void setP_Ar(BigDecimal p_Ar) {
		this.p_Ar = p_Ar;
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
