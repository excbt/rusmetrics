/**
 *
 */
package ru.excbt.datafuse.nmk.utils;

import java.util.Date;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 *
 */
public interface DateInterval {
	Date getFromDate();

	Date getToDate();

	/**
	 *
	 * @return
	 */
	boolean isValid();

	/**
	 *
	 * @return
	 */
	boolean isValidEq();

	/**
	 *
	 * @return
	 */
	default boolean isInvalid() {
		return !isValid();
	}

	/**
	 *
	 * @return
	 */
	default boolean isInvalidEq() {
		return !isValidEq();
	}

	/**
	 *
	 * @return
	 */

	default String getFromDateStr() {
		return DateFormatUtils.formatDateTime(getFromDate());
	}

	/**
	 *
	 * @return
	 */
	default String getToDateStr() {
		return DateFormatUtils.formatDateTime(getToDate());
	}
}
