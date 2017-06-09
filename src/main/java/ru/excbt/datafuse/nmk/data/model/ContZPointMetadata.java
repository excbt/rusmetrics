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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
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

	@Column(name = "src_prop_division")
	private BigDecimal srcPropDivision;

	@Column(name = "dest_prop_capacity")
	private BigDecimal destPropCapacity;

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

	public ContZPoint getContZPoint() {
		return contZPoint;
	}

	public void setContZPoint(ContZPoint contZPoint) {
		this.contZPoint = contZPoint;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public void setContZPointId(Long contZPointId) {
		this.contZPointId = contZPointId;
	}

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public String getDeviceMetadataType() {
		return deviceMetadataType;
	}

	public void setDeviceMetadataType(String deviceMetadataType) {
		this.deviceMetadataType = deviceMetadataType;
	}

	public String getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(String contServiceType) {
		this.contServiceType = contServiceType;
	}

	public String getSrcProp() {
		return srcProp;
	}

	public void setSrcProp(String srcProp) {
		this.srcProp = srcProp;
	}

	public String getDestProp() {
		return destProp;
	}

	public void setDestProp(String destProp) {
		this.destProp = destProp;
	}

	public Boolean getIsIntegrator() {
		return isIntegrator;
	}

	public void setIsIntegrator(Boolean isIntegrator) {
		this.isIntegrator = isIntegrator;
	}

	public BigDecimal getSrcPropDivision() {
		return srcPropDivision;
	}

	public void setSrcPropDivision(BigDecimal srcPropDivision) {
		this.srcPropDivision = srcPropDivision;
	}

	public BigDecimal getDestPropCapacity() {
		return destPropCapacity;
	}

	public void setDestPropCapacity(BigDecimal destPropCapacity) {
		this.destPropCapacity = destPropCapacity;
	}

	public String getSrcMeasureUnit() {
		return srcMeasureUnit;
	}

	public void setSrcMeasureUnit(String srcMeasureUnit) {
		this.srcMeasureUnit = srcMeasureUnit;
	}

	public String getDestMeasureUnit() {
		return destMeasureUnit;
	}

	public void setDestMeasureUnit(String destMeasureUnit) {
		this.destMeasureUnit = destMeasureUnit;
	}

	public Integer getMetaNumber() {
		return metaNumber;
	}

	public void setMetaNumber(Integer metaNumber) {
		this.metaNumber = metaNumber;
	}

	public Integer getMetaOrder() {
		return metaOrder;
	}

	public void setMetaOrder(Integer metaOrder) {
		this.metaOrder = metaOrder;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaComment() {
		return metaComment;
	}

	public void setMetaComment(String metaComment) {
		this.metaComment = metaComment;
	}

	public String getPropVars() {
		return propVars;
	}

	public void setPropVars(String propVars) {
		this.propVars = propVars;
	}

	public String getPropFunc() {
		return propFunc;
	}

	public void setPropFunc(String propFunc) {
		this.propFunc = propFunc;
	}

	public String getDestDbType() {
		return destDbType;
	}

	public void setDestDbType(String destDbType) {
		this.destDbType = destDbType;
	}

	public Integer getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(Integer metaVersion) {
		this.metaVersion = metaVersion;
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

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

}
