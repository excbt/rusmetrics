package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum ContObjectCurrentSettingTypeKey implements KeynameObject {
	SUMMER, WINTER;

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isValid(String key) {
		return SUMMER.getKeyname().equals(key)
				|| WINTER.getKeyname().equals(key);
	}

	/**
	 * 
	 */
	@Override
	public String getKeyname() {
		return this.name().toLowerCase();
	}

}
