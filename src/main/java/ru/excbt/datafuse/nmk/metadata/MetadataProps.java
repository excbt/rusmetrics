package ru.excbt.datafuse.nmk.metadata;

/**
 * Интерфейс для работы с записью метаданных
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.05.2015
 *
 */
public interface MetadataProps {

	public String getSrcProp();

	public void setSrcProp(String propVars);

	public String getDestProp();

	public void setDestProp(String propVars);
}
