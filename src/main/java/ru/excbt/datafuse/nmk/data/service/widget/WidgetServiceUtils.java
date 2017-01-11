/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateTimeInterval;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.01.2017
 * 
 */
public class WidgetServiceUtils {

	private final static Map<String, TimeDetailKey> MODE_DETAIL_MAP;

	static {
		Map<String, TimeDetailKey> m = new HashMap<>();
		m.put("24H", TimeDetailKey.TYPE_1H);
		m.put("TODAY", TimeDetailKey.TYPE_1H);
		m.put("YESTERDAY", TimeDetailKey.TYPE_1H);
		m.put("WEEK", TimeDetailKey.TYPE_24H);
		m.put("MONTH", TimeDetailKey.TYPE_24H);
		m.put("YEAR", TimeDetailKey.TYPE_1MON);
		MODE_DETAIL_MAP = Collections.unmodifiableMap(m);
	}

	private WidgetServiceUtils() {

	}

	/**
	 * 
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	public static Pair<LocalDateTime, LocalDateTime> calculateModeDatePairs(ZonedDateTime dateTime, String mode) {

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

	/**
	 * 
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	public static DateInterval calculateModeDateInterval(ZonedDateTime dateTime, String mode) {
		Pair<LocalDateTime, LocalDateTime> preResult = calculateModeDatePairs(dateTime, mode);
		return preResult != null ? new LocalDateTimeInterval(preResult) : null;
	}

	/**
	 * 
	 * @param mode
	 * @return
	 */
	public static TimeDetailKey getDetailTypeKey(String mode) {
		TimeDetailKey result = MODE_DETAIL_MAP.get(mode);
		return result != null ? result : TimeDetailKey.TYPE_1H;
	}

}
