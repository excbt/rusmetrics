package ru.excbt.datafuse.nmk.utils;

import java.util.Date;

import org.joda.time.LocalDateTime;

public class DateFormatUtils {
	
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
