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
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

/**
 * Электричество - потребление
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
@Entity
@Table(name = "cont_service_data_el_cons")
public class ContServiceDataElCons extends AbstractAuditableModel implements DataDateFormatter, DeletedMarker {

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

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	// 1	
	@Column(name = "p_ap_1")
	private BigDecimal p_Ap1;

	@Column(name = "p_an_1")
	private BigDecimal p_An1;

	@Column(name = "q_rp_1")
	private BigDecimal q_Rp1;

	@Column(name = "q_rn_1")
	private BigDecimal q_Rn1;

	// 2
	@Column(name = "p_ap_2")
	private BigDecimal p_Ap2;

	@Column(name = "p_an_2")
	private BigDecimal p_An2;

	@Column(name = "q_rp_2")
	private BigDecimal q_Rp2;

	@Column(name = "q_rn_2")
	private BigDecimal q_Rn2;

	// 3
	@Column(name = "p_ap_3")
	private BigDecimal p_Ap3;

	@Column(name = "p_an_3")
	private BigDecimal p_An3;

	@Column(name = "q_rp_3")
	private BigDecimal q_Rp3;

	@Column(name = "q_rn_3")
	private BigDecimal q_Rn3;

	// 4
	@Column(name = "p_ap_4")
	private BigDecimal p_Ap4;

	@Column(name = "p_an_4")
	private BigDecimal p_An4;

	@Column(name = "q_rp_4")
	private BigDecimal q_Rp4;

	@Column(name = "q_rn_4")
	private BigDecimal q_Rn4;

	// 5
	@Column(name = "p_ap_5")
	private BigDecimal p_Ap5;

	@Column(name = "p_an_5")
	private BigDecimal p_An5;

	@Column(name = "q_rp_5")
	private BigDecimal q_Rp5;

	@Column(name = "q_rn_5")
	private BigDecimal q_Rn5;

	// All
	@Column(name = "p_ap")
	private BigDecimal p_Ap;

	@Column(name = "p_an")
	private BigDecimal p_An;

	@Column(name = "q_rp")
	private BigDecimal q_Rp;

	@Column(name = "q_rn")
	private BigDecimal q_Rn;

	//	@Column(name = "crc32_value", insertable = false, updatable = false)
	//	private Integer crc32Value;

	@Column(name = "crc32_valid", insertable = false, updatable = false)
	private Boolean crc32Valid;

	@Column(name = "data_mstatus")
	private Short dataMstatus;

	@Column(name = "data_changed", insertable = false, updatable = false)
	private Boolean dataChanged;

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

	public BigDecimal getP_Ap1() {
		return p_Ap1;
	}

	public void setP_Ap1(BigDecimal p_Ap1) {
		this.p_Ap1 = p_Ap1;
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

	public BigDecimal getP_Ap2() {
		return p_Ap2;
	}

	public void setP_Ap2(BigDecimal p_Ap2) {
		this.p_Ap2 = p_Ap2;
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

	public BigDecimal getP_Ap3() {
		return p_Ap3;
	}

	public void setP_Ap3(BigDecimal p_Ap3) {
		this.p_Ap3 = p_Ap3;
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

	public BigDecimal getP_Ap4() {
		return p_Ap4;
	}

	public void setP_Ap4(BigDecimal p_Ap4) {
		this.p_Ap4 = p_Ap4;
	}

	public BigDecimal getP_An4() {
		return p_An4;
	}

	public void setP_An4(BigDecimal p_An4) {
		this.p_An4 = p_An4;
	}

	public BigDecimal getQ_Rp4() {
		return q_Rp4;
	}

	public void setQ_Rp4(BigDecimal q_Rp4) {
		this.q_Rp4 = q_Rp4;
	}

	public BigDecimal getQ_Rn4() {
		return q_Rn4;
	}

	public void setQ_Rn4(BigDecimal q_Rn4) {
		this.q_Rn4 = q_Rn4;
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

	public BigDecimal getP_Ap5() {
		return p_Ap5;
	}

	public void setP_Ap5(BigDecimal p_Ap5) {
		this.p_Ap5 = p_Ap5;
	}

	public BigDecimal getP_An5() {
		return p_An5;
	}

	public void setP_An5(BigDecimal p_An5) {
		this.p_An5 = p_An5;
	}

	public BigDecimal getQ_Rp5() {
		return q_Rp5;
	}

	public void setQ_Rp5(BigDecimal q_Rp5) {
		this.q_Rp5 = q_Rp5;
	}

	public BigDecimal getQ_Rn5() {
		return q_Rn5;
	}

	public void setQ_Rn5(BigDecimal q_Rn5) {
		this.q_Rn5 = q_Rn5;
	}

	public Boolean getCrc32Valid() {
		return crc32Valid;
	}

	public Short getDataMstatus() {
		return dataMstatus;
	}

	public Boolean getDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(Boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

}
