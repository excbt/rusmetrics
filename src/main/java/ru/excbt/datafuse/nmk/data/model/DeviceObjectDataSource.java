package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;

/**
 * Источник данных прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
@Entity
@Table(name = "device_object_data_source")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeviceObjectDataSource extends AbstractAuditableModel implements ActiveObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -9218504365025332432L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	@JsonIgnore
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "is_active")
	private Boolean isActive;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subscr_data_source_id")
	private SubscrDataSource subscrDataSource;

	@Column(name = "subscr_data_source_id", insertable = false, updatable = false)
	private Long subscrDataSourceId;

	@Column(name = "subscr_data_source_addr")
	private String subscrDataSourceAddr;

	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "data_source_table")
	private String dataSourceTable;

	@Column(name = "data_source_table_1h")
	private String dataSourceTable1h;

	@Column(name = "data_source_table_24h")
	private String dataSourceTable24h;

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	@Override
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
	protected boolean deviceDataSourceEquals(DeviceObjectDataSource other) {
		if (other == null) {
			return false;
		}
		if (this.deviceObjectId == null || this.subscrDataSourceId == null) {
			return false;
		}
		return this.subscrDataSourceId.equals(other.subscrDataSourceId)
				&& this.subscrDataSourceId.equals(other.subscrDataSourceId);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDataSourceTable() {
		return dataSourceTable;
	}

	public void setDataSourceTable(String dataSourceTable) {
		this.dataSourceTable = dataSourceTable;
	}

	public String getDataSourceTable1h() {
		return dataSourceTable1h;
	}

	public void setDataSourceTable1h(String dataSourceTable1h) {
		this.dataSourceTable1h = dataSourceTable1h;
	}

	public String getDataSourceTable24h() {
		return dataSourceTable24h;
	}

	public void setDataSourceTable24h(String dataSourceTable24h) {
		this.dataSourceTable24h = dataSourceTable24h;
	}

}
