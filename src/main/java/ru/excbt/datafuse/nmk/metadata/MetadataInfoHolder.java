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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_integrator == null) ? 0 : _integrator.hashCode());
		result = prime
				* result
				+ ((destMeasureUnitKey == null) ? 0 : destMeasureUnitKey
						.hashCode());
		result = prime * result
				+ ((destProp == null) ? 0 : destProp.hashCode());
		result = prime
				* result
				+ ((destPropCapacity == null) ? 0 : destPropCapacity.hashCode());
		result = prime * result
				+ ((metaNumber == null) ? 0 : metaNumber.hashCode());
		result = prime * result
				+ ((metaOrder == null) ? 0 : metaOrder.hashCode());
		result = prime * result
				+ ((propFunc == null) ? 0 : propFunc.hashCode());
		result = prime * result
				+ ((propVars == null) ? 0 : propVars.hashCode());
		result = prime
				* result
				+ ((srcMeasureUnitKey == null) ? 0 : srcMeasureUnitKey
						.hashCode());
		result = prime * result + ((srcProp == null) ? 0 : srcProp.hashCode());
		result = prime * result
				+ ((srcPropDivision == null) ? 0 : srcPropDivision.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetadataInfoHolder other = (MetadataInfoHolder) obj;
		if (_integrator == null) {
			if (other._integrator != null)
				return false;
		} else if (!_integrator.equals(other._integrator))
			return false;
		if (destMeasureUnitKey == null) {
			if (other.destMeasureUnitKey != null)
				return false;
		} else if (!destMeasureUnitKey.equals(other.destMeasureUnitKey))
			return false;
		if (destProp == null) {
			if (other.destProp != null)
				return false;
		} else if (!destProp.equals(other.destProp))
			return false;
		if (destPropCapacity == null) {
			if (other.destPropCapacity != null)
				return false;
		} else if (!destPropCapacity.equals(other.destPropCapacity))
			return false;
		if (metaNumber == null) {
			if (other.metaNumber != null)
				return false;
		} else if (!metaNumber.equals(other.metaNumber))
			return false;
		if (metaOrder == null) {
			if (other.metaOrder != null)
				return false;
		} else if (!metaOrder.equals(other.metaOrder))
			return false;
		if (propFunc == null) {
			if (other.propFunc != null)
				return false;
		} else if (!propFunc.equals(other.propFunc))
			return false;
		if (propVars == null) {
			if (other.propVars != null)
				return false;
		} else if (!propVars.equals(other.propVars))
			return false;
		if (srcMeasureUnitKey == null) {
			if (other.srcMeasureUnitKey != null)
				return false;
		} else if (!srcMeasureUnitKey.equals(other.srcMeasureUnitKey))
			return false;
		if (srcProp == null) {
			if (other.srcProp != null)
				return false;
		} else if (!srcProp.equals(other.srcProp))
			return false;
		if (srcPropDivision == null) {
			if (other.srcPropDivision != null)
				return false;
		} else if (!srcPropDivision.equals(other.srcPropDivision))
			return false;
		return true;
	}

}
