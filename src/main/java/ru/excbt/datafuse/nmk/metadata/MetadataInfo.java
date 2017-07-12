package ru.excbt.datafuse.nmk.metadata;

import java.math.BigDecimal;

/**
 * Расширенный интерфейс для работы с записью метаданных
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.05.2015
 *
 */
public interface MetadataInfo extends MetadataProps {

	String getPropVars();

	String getPropFunc();

	Boolean getIsIntegrator();

	Double getSrcPropDivision();

	Double getDestPropCapacity();

	String getDestDbType();

	String getSrcMeasureUnit();

	String getDestMeasureUnit();

	Integer getMetaNumber();

	Integer getMetaOrder();

}
