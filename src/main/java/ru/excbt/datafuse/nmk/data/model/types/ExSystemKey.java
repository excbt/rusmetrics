package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum ExSystemKey implements KeynameObject {
	PORTAL, LERS, VZLET, MANUAL;

	@Override
	public String getKeyname() {
		return this.name();
	}
	
}
