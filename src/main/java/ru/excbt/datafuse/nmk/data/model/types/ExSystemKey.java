package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum ExSystemKey implements KeynameObject {
	PORTAL, LERS, VZLET, MANUAL, DEVICE;

	@Override
	public String getKeyname() {
		return this.name();
	}

}
