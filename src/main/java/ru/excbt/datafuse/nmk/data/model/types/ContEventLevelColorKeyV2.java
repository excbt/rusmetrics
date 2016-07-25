package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;

/**
 * Тип данных: цвет уровня событий
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.06.2015
 *
 */
public enum ContEventLevelColorKeyV2 implements KeynameObject, StatusColorObject {
	GREEN(100), YELLOW(50), RED(0);

	private final String keyname;

	private final int colorRank;

	private ContEventLevelColorKeyV2(int colorRank) {
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
	public static ContEventLevelColorKeyV2 findByKeyname(String keyname) {
		if (keyname == null) {
			return null;
		}
		ContEventLevelColorKeyV2 result = null;
		for (ContEventLevelColorKeyV2 v : ContEventLevelColorKeyV2.values()) {
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
	public static ContEventLevelColorKeyV2 findByKeyname(KeynameObject keynameObject) {
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
