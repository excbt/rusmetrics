/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.10.2016
 * 
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_type_group")
public class DeviceModelTypeGroup extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4372750344328506338L;

	@Column(name = "device_model_id")
	private Long deviceModelId;

	@Column(name = "device_model_type")
	private String deviceModelType;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(Long deviceModelId) {
		this.deviceModelId = deviceModelId;
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

	public String getDeviceModelType() {
		return deviceModelType;
	}

	public void setDeviceModelType(String deviceModelType) {
		this.deviceModelType = deviceModelType;
	}

}