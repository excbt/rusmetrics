package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_metadata_transform")
@Getter
@Setter
public class DeviceObjectMetadataTransform extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2174143740110324563L;

	@Column(name = "device_metadata_type")
	private String deviceMetadataType;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "cont_service_type")
	private String contServiceType;

	@Column(name = "src_prop")
	private String srcProp;

	@Column(name = "dest_prop")
	private String destProp;

	@Column(name = "is_integrator")
	private Boolean isIntegrator;

	@Column(name = "src_prop_division", columnDefinition = "numeric")
	private Double srcPropDivision;

	@Column(name = "dest_prop_capacity", columnDefinition = "numeric")
	private Double destPropCapacity;

	@Column(name = "src_measure_unit")
	private String srcMeasureUnit;

	@Column(name = "dest_measure_unit")
	private String destMeasureUnit;

	@Column(name = "meta_number")
	private Integer metaNumber;

	@Column(name = "meta_order")
	private Integer metaOrder;

	@Column(name = "meta_description")
	private String metaDescription;

	@Column(name = "meta_comment")
	private String metaComment;

	@Column(name = "prop_vars")
	private String propVars;

	@Column(name = "prop_func")
	private String propFunc;

	@Column(name = "dest_db_type")
	private String destDbType;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "meta_version")
	private Integer metaVersion;

	@Column(name = "device_object_metadata_id")
	private Long deviceObjectMetadataId;

}
