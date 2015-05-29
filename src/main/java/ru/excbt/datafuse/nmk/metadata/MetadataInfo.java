package ru.excbt.datafuse.nmk.metadata;

import java.math.BigDecimal;

public interface MetadataInfo extends MetadataProps {

	public String getPropVars();

	public String getPropFunc();

	public Boolean get_integrator();

	public BigDecimal getSrcPropDivision();

	public BigDecimal getDestPropCapacity();

	public String getSrcMeasureUnitKey();

	public String getDestMeasureUnitKey();

	public Integer getMetaNumber();

	public Integer getMetaOrder();
}
