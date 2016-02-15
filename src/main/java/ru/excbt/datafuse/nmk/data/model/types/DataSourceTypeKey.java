package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum DataSourceTypeKey implements KeynameObject {
	LERS, VZLET, DEVICE;

	/**
	 * 
	 */
	@Override
	public String getKeyname() {
		return this.name();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public boolean equalsKeyname(String value) {
		return this.getKeyname().equals(value);
	}

}
