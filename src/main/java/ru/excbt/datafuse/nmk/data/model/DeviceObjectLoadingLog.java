package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "device_object_loading_log")
public class DeviceObjectLoadingLog extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7350434737316386194L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "last_loading_time")
	private Date lastLoadingTime;

	@Column(name = "last_attempt_time")
	private Date lastAttemptTime;

	@Column(name = "retry_attempts")
	private Integer retryAttempts;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted", updatable = false)
	private int deleted;

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public Date getLastLoadingTime() {
		return lastLoadingTime;
	}

	public void setLastLoadingTime(Date lastLoadingTime) {
		this.lastLoadingTime = lastLoadingTime;
	}

	public Date getLastAttemptTime() {
		return lastAttemptTime;
	}

	public void setLastAttemptTime(Date lastAttemptTime) {
		this.lastAttemptTime = lastAttemptTime;
	}

	public Integer getRetryAttempts() {
		return retryAttempts;
	}

	public void setRetryAttempts(Integer retryAttempts) {
		this.retryAttempts = retryAttempts;
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

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

}