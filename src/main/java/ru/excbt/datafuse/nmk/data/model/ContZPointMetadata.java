package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ContZPointMetadata extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4254449825224132083L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_zpoint_id")
	private ContZPoint contZPoint;

	@Column(name = "cont_zpoint_id", insertable = false, updatable = false)
	private Long contZPointId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "device_metadata_type")
	private String deviceMetadataType;

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

	@Column(name = "meta_version")
	private Integer metaVersion;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "meta_name")
	private String metaName;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
