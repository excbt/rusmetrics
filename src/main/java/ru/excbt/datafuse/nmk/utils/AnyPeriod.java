/**
 *
 */
package ru.excbt.datafuse.nmk.utils;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.01.2018
 *
 */
public interface AnyPeriod<T extends Comparable> extends ValidPeriodCheckable {

	T getFrom();
	T getTo();


	default boolean isValid() {
		if (getFrom() == null || getTo() == null) {
			return false;
		}
		return getFrom().compareTo(getTo()) < 0;// isBefore();
	}

	default boolean isValidEq() {
        if (getFrom() == null || getTo() == null) {
			return false;
		}
		return getFrom().compareTo(getTo()) <= 0;//
        // isBefore(getTo()) || getFrom().isEqual(getTo());
	}

	default boolean isInvalid() {
	    return !isValid();
    }

	default boolean isInvalidEq() {
	    return !isValidEq();
    }

}
