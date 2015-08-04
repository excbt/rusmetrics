package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;
import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;

public enum ContEventLevelColorKey implements KeynameObject, StatusColorObject {
	GREEN(100), YELLOW(80), ORANGE(50), RED(0);

	private final String keyname;

	private final int colorRank;
	
	private ContEventLevelColorKey(int colorRank) {
		this.keyname = this.name().toUpperCase();
		this.colorRank = colorRank;
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

	public int getColorRank() {
		return colorRank;
	}

}
