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
public interface DateInterval extends ValidPeriodCheckable {
	Date getFromDate();

	Date getToDate();


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
