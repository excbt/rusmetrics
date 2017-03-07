package ru.excbt.datafuse.nmk.data.model.markers;

public interface KeynameObject {

	String getKeyname();

    /**
     *
     * @return getKeyname()
     */
	default String keyName() {
	    return getKeyname();
    }

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
