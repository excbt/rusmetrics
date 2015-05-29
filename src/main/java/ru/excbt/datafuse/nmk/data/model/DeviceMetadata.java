package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.metadata.MetadataInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="device_metadata")
public class DeviceMetadata extends AbstractPersistableEntity<Long> implements MetadataInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1235600431881773268L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_model_id")
	@JsonIgnore
	private DeviceModel deviceModel;	
	
	@Column(name = "device_metadata_type")
	private String deviceMetadataTypeKey;
	
	@Column(name = "src_prop")
	private String srcProp;

	@Column(name = "dest_prop")
	private String destProp;

	@Column(name = "prop_vars")
	private String propVars;

	@Column(name = "prop_func")
	private String propFunc;

	@Column(name = "is_integrator")
	private Boolean _integrator;

	@Column(name = "src_prop_division")
	private BigDecimal srcPropDivision;

	@Column(name = "dest_prop_capacity")
	private BigDecimal destPropCapacity;

	@Column(name = "src_measure_unit")
	private String srcMeasureUnitKey;

	@Column(name = "dest_measure_unit")
	private String destMeasureUnitKey;

	@Column(name = "meta_number")
	private Integer metaNumber;

	@Column(name = "meta_order")
	private Integer metaOrder;

	@Column(name = "meta_description")
	private String metaDescription;

	@Column(name = "meta_comment")
	private String metaComment;

	@Column(name = "dest_db_type")
	private String destDbType;	
	
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceMetadataTypeKey() {
		return deviceMetadataTypeKey;
	}

	public void setDeviceMetadataTypeKey(String deviceMetadataTypeKey) {
		this.deviceMetadataTypeKey = deviceMetadataTypeKey;
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
	public Boolean get_integrator() {
		return _integrator;
	}

	public void set_integrator(Boolean _integrator) {
		this._integrator = _integrator;
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
	public String getSrcMeasureUnitKey() {
		return srcMeasureUnitKey;
	}

	public void setSrcMeasureUnitKey(String srcMeasureUnitKey) {
		this.srcMeasureUnitKey = srcMeasureUnitKey;
	}

	@Override
	public String getDestMeasureUnitKey() {
		return destMeasureUnitKey;
	}

	public void setDestMeasureUnitKey(String destMeasureUnitKey) {
		this.destMeasureUnitKey = destMeasureUnitKey;
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
	public String getDestDbType() {
		return destDbType;
	}

	public void setDestDbType(String destDbType) {
		this.destDbType = destDbType;
	}
	
	
}
