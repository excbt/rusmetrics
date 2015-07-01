package ru.excbt.datafuse.nmk.data.constant;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;
import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;

public enum ContEventLevelColorKey implements KeynameObject, StatusColorObject {
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
	public static ContEventLevelColorKey findByKeyname(String keyname) {
		if (keyname == null) {
			return null;
		}
		ContEventLevelColorKey result = null;
		for (ContEventLevelColorKey v : ContEventLevelColorKey.values()) {
			if (v.keyname.equals(keyname)) {
				result = v;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param keynameObject
	 * @return
	 */
	public static ContEventLevelColorKey findByKeyname(
			KeynameObject keynameObject) {
		if (keynameObject == null) {
			return null;
		}
		return findByKeyname(keynameObject.getKeyname());
	}

	@Override
	public String getStatusColor() {
		return this.name();
	}

}
