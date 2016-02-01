package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип данных: источники внешних данных
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.07.2015
 *
 */
public enum ExSystemKey implements KeynameObject {
	PORTAL, LERS, VZLET, MANUAL, DEVICE;

	@Override
	public String getKeyname() {
		return this.name();
	}

}
