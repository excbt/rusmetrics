package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип данных: режим работы
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.07.2015
 *
 */
public enum ContObjectCurrentSettingTypeKey implements KeynameObject {
	SUMMER, WINTER;

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isSupported(String key) {
		return SUMMER.getKeyname().equals(key) || WINTER.getKeyname().equals(key);
	}

	/**
	 * 
	 */
	@Override
	public String getKeyname() {
		return this.name().toLowerCase();
	}

}
