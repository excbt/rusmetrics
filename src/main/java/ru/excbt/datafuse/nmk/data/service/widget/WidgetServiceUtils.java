/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.01.2017
 * 
 */
public class WidgetServiceUtils {

	private WidgetServiceUtils() {

	}

	/**
	 * 
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	public static Pair<LocalDateTime, LocalDateTime> calculateDatePairs(ZonedDateTime dateTime, String mode) {

		ZonedDateTime endOfDay = dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1);

		if ("TODAY".equals(mode)) {
			return Pair.of(dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime(), endOfDay.toLocalDateTime());
		}

		if ("YESTERDAY".equals(mode)) {
			return Pair.of(dateTime.minusDays(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.minusDays(1).toLocalDateTime());
		}

		if ("WEEK".equals(mode)) {
			return Pair.of(dateTime.minusDays(7).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.toLocalDateTime());
		}

		return null;
	}

}
