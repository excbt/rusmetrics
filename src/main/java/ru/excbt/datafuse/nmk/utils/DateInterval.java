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
	public Date getFromDate();

	public Date getToDate();

	/**
	 * 
	 * @return
	 */
	public boolean isValid();

	/**
	 * 
	 * @return
	 */
	public boolean isValidEq();

	/**
	 * 
	 * @return
	 */
	public default boolean isInvalid() {
		return !isValid();
	}

	/**
	 * 
	 * @return
	 */
	public default boolean isInvalidEq() {
		return !isValidEq();
	}

	/**
	 * 
	 * @return
	 */

	public default String getFromDateStr() {
		return DateFormatUtils.formatDateTime(getFromDate());
	}

	/**
	 * 
	 * @return
	 */
	public default String getToDateStr() {
		return DateFormatUtils.formatDateTime(getToDate());
	}
}
