package ru.excbt.datafuse.nmk.data.model;

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
@Table(name = "device_object_loading_settings")
public class DeviceObjectLoadingSettings extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4737222596632225231L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "is_loading_auto")
	private Boolean isLoadingAuto;

	@Column(name = "loading_interval")
	private String loadingInterval;

	@Column(name = "loading_attempts")
	private Integer loadingAttempts;

	@Column(name = "loading_retry_interval")
	private String loadingRetryInterval;

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

	public Boolean getIsLoadingAuto() {
		return isLoadingAuto;
	}

	public void setIsLoadingAuto(Boolean isLoadingAuto) {
		this.isLoadingAuto = isLoadingAuto;
	}

	public String getLoadingInterval() {
		return loadingInterval;
	}

	public void setLoadingInterval(String loadingInterval) {
		this.loadingInterval = loadingInterval;
	}

	public Integer getLoadingAttempts() {
		return loadingAttempts;
	}

	public void setLoadingAttempts(Integer loadingAttempts) {
		this.loadingAttempts = loadingAttempts;
	}

	public String getLoadingRetryInterval() {
		return loadingRetryInterval;
	}

	public void setLoadingRetryInterval(String loadingRetryInterval) {
		this.loadingRetryInterval = loadingRetryInterval;
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
