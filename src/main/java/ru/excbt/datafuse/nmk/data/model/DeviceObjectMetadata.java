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

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(name = "device_object_metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
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

	public String getDeviceMetadataType() {
		return deviceMetadataType;
	}

	public void setDeviceMetadataType(String deviceMetadataType) {
		this.deviceMetadataType = deviceMetadataType;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}
