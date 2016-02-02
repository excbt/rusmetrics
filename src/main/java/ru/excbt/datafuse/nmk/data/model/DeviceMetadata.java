package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private Integer metaVersion = 1;

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}

	public Long getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(Long deviceModelId) {
		this.deviceModelId = deviceModelId;
	}

	public String getDeviceMetadataType() {
		return deviceMetadataType;
	}

	public void setDeviceMetadataType(String deviceMetadataType) {
		this.deviceMetadataType = deviceMetadataType;
	}

	@Override
	public String getSrcProp() {
		return srcProp;
	}

	@Override
	public void setSrcProp(String srcProp) {
		this.srcProp = srcProp;
	}

	@Override
	public String getDestProp() {
		return destProp;
	}

	@Override
	public void setDestProp(String destProp) {
		this.destProp = destProp;
	}

	@Override
	public Boolean getIsIntegrator() {
		return isIntegrator;
	}

	public void setIsIntegrator(Boolean isIntegrator) {
		this.isIntegrator = isIntegrator;
	}

	@Override
	public BigDecimal getSrcPropDivision() {
		return srcPropDivision;
	}

	public void setSrcPropDivision(BigDecimal srcPropDivision) {
		this.srcPropDivision = srcPropDivision;
	}

	@Override
	public BigDecimal getDestPropCapacity() {
		return destPropCapacity;
	}

	public void setDestPropCapacity(BigDecimal destPropCapacity) {
		this.destPropCapacity = destPropCapacity;
	}

	@Override
	public String getSrcMeasureUnit() {
		return srcMeasureUnit;
	}

	public void setSrcMeasureUnit(String srcMeasureUnit) {
		this.srcMeasureUnit = srcMeasureUnit;
	}

	@Override
	public String getDestMeasureUnit() {
		return destMeasureUnit;
	}

	public void setDestMeasureUnit(String destMeasureUnit) {
		this.destMeasureUnit = destMeasureUnit;
	}

	@Override
	public Integer getMetaNumber() {
		return metaNumber;
	}

	public void setMetaNumber(Integer metaNumber) {
		this.metaNumber = metaNumber;
	}

	@Override
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

	@Override
	public String getPropVars() {
		return propVars;
	}

	public void setPropVars(String propVars) {
		this.propVars = propVars;
	}

	@Override
	public String getPropFunc() {
		return propFunc;
	}

	public void setPropFunc(String propFunc) {
		this.propFunc = propFunc;
	}

	@Override
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

}
