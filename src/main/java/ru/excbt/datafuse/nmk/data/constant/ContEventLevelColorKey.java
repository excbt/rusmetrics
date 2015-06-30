package ru.excbt.datafuse.nmk.data.constant;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum ContEventLevelColorKey implements KeynameObject {
	GREEN, YELLOW, ORANGE, RED;

	private final String keyname;

	private ContEventLevelColorKey() {
		this.keyname = this.name().toUpperCase();
	}

	@Override
	public String getKeyname() {
		return keyname;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static ContEventLevelColorKey findByKey(String keyname) {
		ContEventLevelColorKey result = null;
		for (ContEventLevelColorKey v : ContEventLevelColorKey.values()) {
			if (v.keyname.equals(keyname)) {
				result = v;
				break;
			}
		}
		return result;
	}

}
