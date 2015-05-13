package ru.excbt.datafuse.nmk.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

public class JodaTimeUtils {

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static LocalDateTime endOfDay(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		LocalDateTime result = dateTime.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);

		return result;
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime endOfDay(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		DateTime result = dateTime.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);
		
		return result;
	}

}
