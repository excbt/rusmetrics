package ru.excbt.datafuse.nmk.utils;

import java.util.Date;

import org.joda.time.LocalDateTime;

/**
 * Утилиты для работы с датой
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.07.2015
 *
 */
public class DateFormatUtils {

	public static final String DATE_FORMAT_STR_FULL = "dd-MM-yyyy HH:mm";
	public static final String DATE_FORMAT_STR_TRUNC = "dd-MM-yyyy";

	private DateFormatUtils() {

	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date) {
		if (date == null) {
			return null;
		}
		LocalDateTime ldt = new LocalDateTime(date);
		return ldt.toString("yyyy-MM-dd HH:mm");
	}

	/**
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String formatDateTime(Date date, String formatString) {
		if (date == null) {
			return null;
		}
		LocalDateTime ldt = new LocalDateTime(date);
		return ldt.toString(formatString);
	}
}
