package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип данных: часовые пояса
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 21.10.2015
 *
 */
public enum TimezoneDefKey implements KeynameObject {
	MSK("MSK"), IZHVSK("IZHVSK"), GMT_M3("GMT-3");

	private final String keyname;

	private TimezoneDefKey(String keyname) {
		this.keyname = keyname;
	}

	@Override
	public String getKeyname() {
		return keyname;
	}
}
