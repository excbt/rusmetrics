package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_metadata_transform_history")
@Getter
@Setter
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

}
