package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "device_object_data_source")
public class DeviceObjectDataSource extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9218504365025332432L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id", insertable = false, updatable = false)
	private DeviceObject deviceObject;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "is_active")
	private Boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_data_source_id", insertable = false, updatable = false)
	private SubscrDataSource subscrDataSource;

	@Column(name = "subscr_data_source_id")
	private Long subscrDataSourceId;

	@Column(name = "subscr_data_source_addr")
	private String subscrDataSourceAddr;

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public SubscrDataSource getSubscrDataSource() {
		return subscrDataSource;
	}

	public void setSubscrDataSource(SubscrDataSource subscrDataSource) {
		this.subscrDataSource = subscrDataSource;
	}

	public String getSubscrDataSourceAddr() {
		return subscrDataSourceAddr;
	}

	public void setSubscrDataSourceAddr(String subscrDataSourceAddr) {
		this.subscrDataSourceAddr = subscrDataSourceAddr;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public Long getSubscrDataSourceId() {
		return subscrDataSourceId;
	}

	public void setSubscrDataSourceId(Long subscrDataSourceId) {
		this.subscrDataSourceId = subscrDataSourceId;
	}

	/**
	 * 
	 * @param other
	 * @return
	 */
	public boolean deviceDataSourceEquals(DeviceObjectDataSource other) {
		if (other == null) {
			return false;
		}
		if (this.deviceObjectId == null || this.subscrDataSourceId == null) {
			return false;
		}
		return this.subscrDataSourceId.equals(other.subscrDataSourceId)
				&& this.subscrDataSourceId.equals(other.subscrDataSourceId);
	}

}
