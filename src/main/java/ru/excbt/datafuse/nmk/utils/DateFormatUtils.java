package ru.excbt.datafuse.nmk.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
	public static final DateTimeFormatter DATE_FORMAT_STR_FULL2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	public static final String DATE_FORMAT_STR_FULL_SEC = "dd-MM-yyyy HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMAT_STR_FULL_SEC2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static final String DATE_FORMAT_STR_TRUNC = "dd-MM-yyyy";
    public static final DateTimeFormatter DATE_FORMAT_STR_TRUNC2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	private DateFormatUtils() {

	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date) {
		return formatDateTime(date, "yyyy-MM-dd HH:mm");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(formatter);
	}

    /**
     *
     * @param instant
     * @param formatString
     * @return
     */
	public static String formatDateTime(Instant instant, String formatString) {
		if (instant == null) {
			return null;
		}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
	}

}
