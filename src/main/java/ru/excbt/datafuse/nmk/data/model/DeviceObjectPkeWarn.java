package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_warn")
@JsonInclude(Include.NON_NULL)
public class DeviceObjectPkeWarn extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4032736746402754373L;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "device_object_pke_type")
	private String deviceObjectPkeTypeKeyname;

	@Column(name = "warn_start_date")
	private Date warnStartDate;

	@Column(name = "warn_end_date")
	private Date warnEndDate;

	@Column(name = "warn_value")
	private BigDecimal warnValue;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public String getDeviceObjectPkeTypeKeyname() {
		return deviceObjectPkeTypeKeyname;
	}

	public void setDeviceObjectPkeTypeKeyname(String deviceObjectPkeTypeKeyname) {
		this.deviceObjectPkeTypeKeyname = deviceObjectPkeTypeKeyname;
	}

	public Date getWarnStartDate() {
		return warnStartDate;
	}

	public void setWarnStartDate(Date warnStartDate) {
		this.warnStartDate = warnStartDate;
	}

	public Date getWarnEndDate() {
		return warnEndDate;
	}

	public void setWarnEndDate(Date warnEndDate) {
		this.warnEndDate = warnEndDate;
	}

	public BigDecimal getWarnValue() {
		return warnValue;
	}

	public void setWarnValue(BigDecimal warnValue) {
		this.warnValue = warnValue;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getWarnStartDateStr() {
		return DateFormatUtils.formatDateTime(warnStartDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

	public String getWarnEndDateStr() {
		return DateFormatUtils.formatDateTime(warnEndDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}
}
