package ru.excbt.datafuse.nmk.metadata;

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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

	private Double srcPropDivision;

	private Double destPropCapacity;

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

}
