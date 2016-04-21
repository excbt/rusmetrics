package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_last_info")
public class DeviceObjectLastInfo extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5353859385235208349L;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id", insertable = false, updatable = false)
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "device_last_time", insertable = false, updatable = false)
	private Date deviceLastTime;

	@Column(name = "driver_last_time", insertable = false, updatable = false)
	private Date driverLastTime;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public Date getDeviceLastTime() {
		return deviceLastTime;
	}

	public void setDeviceLastTime(Date deviceLastTime) {
		this.deviceLastTime = deviceLastTime;
	}

	public Date getDriverLastTime() {
		return driverLastTime;
	}

	public void setDriverLastTime(Date driverLastTime) {
		this.driverLastTime = driverLastTime;
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

	public String getDeviceLastTimeStr() {
		return deviceLastTime == null ? null
				: DateFormatUtils.formatDateTime(deviceLastTime, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

	public String getDriverLastTimeStr() {
		return driverLastTime == null ? null
				: DateFormatUtils.formatDateTime(driverLastTime, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

}