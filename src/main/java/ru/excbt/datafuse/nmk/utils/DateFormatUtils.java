package ru.excbt.datafuse.nmk.utils;

import java.util.Date;

import org.joda.time.LocalDateTime;

public class DateFormatUtils {
	
	private DateFormatUtils() {

	}
	
	public static String formatDateTime(Date arg) {
		if (arg == null) {
			return null;
		}
		LocalDateTime ldt = new LocalDateTime(arg);
		return ldt.toString("yyyy-MM-dd HH:mm");
	}
}
