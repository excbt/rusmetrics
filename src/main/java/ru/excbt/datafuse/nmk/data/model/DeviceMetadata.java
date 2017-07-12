package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.metadata.MetadataInfo;

/**
 * Метаданные прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.05.2015
 *
 */
@Entity
@Table(name = "device_metadata")
@Getter
@Setter
public class DeviceMetadata extends AbstractPersistableEntity<Long> implements MetadataInfo {

	/**
	 *
	 */
	private static final long serialVersionUID = 1235600431881773268L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_model_id")
	@JsonIgnore
	private DeviceModel deviceModel;

	@Column(name = "device_model_id", insertable = false, updatable = false)
	private Long deviceModelId;

	@Column(name = "device_metadata_type")
	private String deviceMetadataType;

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

	@Column(name = "meta_version")
	private Integer metaVersion = 1;

	@Column(name = "meta_name")
	private String metaName;

	@Column(name = "device_mapping")
	private String deviceMapping;

	@Column(name = "device_mapping_info")
	private String deviceMappingInfo;

}
