package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

/**
 * Метаданные прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2016
 *
 */
@Entity
@Table(name = "device_object_metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class DeviceObjectMetadata extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = 7749595409266133751L;

	@Column(name = "device_metadata_type", updatable = false)
	private String deviceMetadataType;

	@Column(name = "device_object_id", updatable = false)
	private Long deviceObjectId;

	@Column(name = "cont_service_type", updatable = false)
	private String contServiceType;

	@Column(name = "src_prop")
	private String srcProp;

	@Column(name = "dest_prop")
	private String destProp;

	@Column(name = "is_integrator", updatable = false)
	private Boolean isIntegrator;

	@Column(name = "src_prop_division")
	private BigDecimal srcPropDivision;

	@Column(name = "dest_prop_capacity")
	private BigDecimal destPropCapacity;

	@Column(name = "src_measure_unit")
	private String srcMeasureUnit;

	@Column(name = "dest_measure_unit")
	private String destMeasureUnit;

	@Column(name = "meta_number", updatable = false)
	private Integer metaNumber;

	@Column(name = "meta_order", updatable = false)
	private Integer metaOrder;

	@Column(name = "meta_description", updatable = false)
	private String metaDescription;

	@Column(name = "meta_comment")
	private String metaComment;

	@Column(name = "prop_vars", updatable = false)
	private String propVars;

	@Column(name = "prop_func", updatable = false)
	private String propFunc;

	@Column(name = "dest_db_type", updatable = false)
	private String destDbType;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "meta_version")
	private Integer metaVersion = 1;

	@Column(name = "is_transformed")
	private Boolean isTransformed;

	@Column(name = "meta_name")
	private String metaName;

}
