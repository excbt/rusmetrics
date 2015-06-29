package ru.excbt.datafuse.nmk.data.constant;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum ContEventLevelColorKey implements KeynameObject {
	GREEN, YELLOW, ORANGE, RED;

	private final String keyname;

	private ContEventLevelColorKey() {
		this.keyname = this.name().toLowerCase();
	}

	@Override
	public String getKeyname() {
		return keyname;
	}

}
