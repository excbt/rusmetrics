package ru.excbt.datafuse.nmk.data.domain;

public interface KeynameObject {

	public String getKeyname();

	/**
	 * 
	 * @param value
	 * @return
	 */
	default public boolean isEquals(String value) {
		return getKeyname().equals(value);
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	default public boolean isNotEquals(String value) {
		return !isEquals(value);
	}

}
