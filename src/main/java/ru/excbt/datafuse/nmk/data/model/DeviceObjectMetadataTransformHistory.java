package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_metadata_transform_history")
public class DeviceObjectMetadataTransformHistory extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767887980617717137L;

	@Column(name = "device_object_metadata_transform_id", insertable = false, updatable = false)
	private Long deviceObjectMetadataTransformId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_metadata_transform_id")
	private DeviceObjectMetadataTransform deviceObjectMetadataTransform;

	@Column(name = "device_object_metadata_id", insertable = false, updatable = false)
	private Long deviceObjectMetadataId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_metadata_id")
	private DeviceObjectMetadata deviceObjectMetadata;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getDeviceObjectMetadataTransformId() {
		return deviceObjectMetadataTransformId;
	}

	public void setDeviceObjectMetadataTransformId(Long deviceObjectMetadataTransformId) {
		this.deviceObjectMetadataTransformId = deviceObjectMetadataTransformId;
	}

	public DeviceObjectMetadataTransform getDeviceObjectMetadataTransform() {
		return deviceObjectMetadataTransform;
	}

	public void setDeviceObjectMetadataTransform(DeviceObjectMetadataTransform deviceObjectMetadataTransform) {
		this.deviceObjectMetadataTransform = deviceObjectMetadataTransform;
	}

	public Long getDeviceObjectMetadataId() {
		return deviceObjectMetadataId;
	}

	public void setDeviceObjectMetadataId(Long deviceObjectMetadataId) {
		this.deviceObjectMetadataId = deviceObjectMetadataId;
	}

	public DeviceObjectMetadata getDeviceObjectMetadata() {
		return deviceObjectMetadata;
	}

	public void setDeviceObjectMetadata(DeviceObjectMetadata deviceObjectMetadata) {
		this.deviceObjectMetadata = deviceObjectMetadata;
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

}