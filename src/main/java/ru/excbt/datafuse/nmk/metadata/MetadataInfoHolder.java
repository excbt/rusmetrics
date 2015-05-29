package ru.excbt.datafuse.nmk.metadata;

import java.io.Serializable;
import java.math.BigDecimal;

public class MetadataInfoHolder implements MetadataInfo, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5458040579900921608L;

	private String srcProp;

	private String destProp;

	private String propVars;

	private String propFunc;

	private Boolean _integrator;

	private BigDecimal srcPropDivision;

	private BigDecimal destPropCapacity;

	
	private String srcMeasureUnitKey;

	private String destMeasureUnitKey;

	private Integer metaNumber;

	private Integer metaOrder;

	private String destDbType;	
	
	public MetadataInfoHolder() {

	}

	public MetadataInfoHolder(MetadataInfo metadataInfo) {
		this.srcProp = metadataInfo.getSrcProp();
		this.destProp = metadataInfo.getDestProp();
		this.propVars = metadataInfo.getPropVars();
		this.propFunc = metadataInfo.getPropFunc();
		this._integrator = metadataInfo.get_integrator();
		this.srcPropDivision = metadataInfo.getSrcPropDivision();
		this.destPropCapacity = metadataInfo.getDestPropCapacity();
		this.srcMeasureUnitKey = metadataInfo.getSrcMeasureUnitKey();
		this.destMeasureUnitKey = metadataInfo.getDestMeasureUnitKey();
		this.metaNumber = metadataInfo.getMetaNumber();
		this.metaOrder = metadataInfo.getMetaOrder();
		this.destDbType = metadataInfo.getDestDbType();
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
	
	@Override
	public String getDestDbType() {
		return destDbType;
	}

	public void setDestDbType(String destDbType) {
		this.destDbType = destDbType;
	}
	

}
