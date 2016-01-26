package ru.excbt.datafuse.nmk.metadata;

import java.math.BigDecimal;

public interface MetadataInfo extends MetadataProps {

	public String getPropVars();

	public String getPropFunc();

	public Boolean getIsIntegrator();

	public BigDecimal getSrcPropDivision();

	public BigDecimal getDestPropCapacity();

	public String getDestDbType();

	public String getSrcMeasureUnit();

	public String getDestMeasureUnit();

	public Integer getMetaNumber();

	public Integer getMetaOrder();

}
