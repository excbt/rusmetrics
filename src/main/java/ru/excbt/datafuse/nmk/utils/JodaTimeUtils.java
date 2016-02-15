package ru.excbt.datafuse.nmk.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

/**
 * Утилиты для работы с JODA Time
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.05.2015
 *
 */
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
		LocalDateTime result = dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
				.withMillisOfSecond(999);

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
		DateTime result = dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
				.withMillisOfSecond(999);

		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static DateTime endOfDay(Date date) {
		if (date == null) {
			return null;
		}

		return endOfDay(new DateTime(date));
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime startOfDay(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		DateTime result = dateTime.withMillisOfDay(0);

		return result;
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime startOfDay(Date dateTime) {
		if (dateTime == null) {
			return null;
		}
		DateTime result = new DateTime(dateTime).withMillisOfDay(0);

		return result;
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static LocalDateTime startOfDay(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		LocalDateTime result = dateTime.withMillisOfDay(0);

		return result;
	}

}
