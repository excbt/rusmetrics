/**
 *
 */
package ru.excbt.datafuse.nmk.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateUtils {

	/**
	 *
	 * @param localDate
	 * @return
	 */
	public static Date asDate(LocalDate localDate) {
		return localDate == null ? null : Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 *
	 * @param localDateTime
	 * @return
	 */
	public static Date asDate(LocalDateTime localDateTime) {
		return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate asLocalDate(Date date) {
		return date == null ? null :  Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime asLocalDateTime(Date date) {
		return date == null ? null :  Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
