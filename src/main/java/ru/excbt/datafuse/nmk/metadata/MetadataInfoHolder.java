package ru.excbt.datafuse.nmk.metadata;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Реализация объекта для записи метаданных
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.05.2015
 *
 */
public class MetadataInfoHolder implements MetadataInfo, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5458040579900921608L;

	private String srcProp;

	private String destProp;

	private String propVars;

	private String propFunc;

	private Boolean isIntegrator;

	private BigDecimal srcPropDivision;

	private BigDecimal destPropCapacity;

	private String srcMeasureUnit;

	private String destMeasureUnit;

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
		this.isIntegrator = metadataInfo.getIsIntegrator();
		this.srcPropDivision = metadataInfo.getSrcPropDivision();
		this.destPropCapacity = metadataInfo.getDestPropCapacity();
		this.srcMeasureUnit = metadataInfo.getSrcMeasureUnit();
		this.destMeasureUnit = metadataInfo.getDestMeasureUnit();
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

	@Override
	public String getDestDbType() {
		return destDbType;
	}

	public void setDestDbType(String destDbType) {
		this.destDbType = destDbType;
	}
}
