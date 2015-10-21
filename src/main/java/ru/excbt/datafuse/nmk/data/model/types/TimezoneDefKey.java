package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

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
