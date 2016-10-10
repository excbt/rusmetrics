/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.10.2016
 * 
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_service_data_impulse")
public class ContServiceDataImpulse extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -147661728148659523L;

	@Column(name = "data_date")
	private Date dataDate;

	@Column(name = "cont_zpoint_id")
	private Long contZpointId;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "time_detail_type")
	private String timeDetailType;

	@Column(name = "data_value")
	private BigDecimal dataValue;

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public Long getContZpointId() {
		return contZpointId;
	}

	public void setContZpointId(Long contZpointId) {
		this.contZpointId = contZpointId;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public void setTimeDetailType(String timeDetailType) {
		this.timeDetailType = timeDetailType;
	}

	public BigDecimal getDataValue() {
		return dataValue;
	}

	public void setDataValue(BigDecimal dataValue) {
		this.dataValue = dataValue;
	}

}