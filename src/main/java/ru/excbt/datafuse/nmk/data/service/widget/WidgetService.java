/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.AbstractService;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateTimeInterval;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 *
 */
public abstract class WidgetService extends AbstractService {

	public enum MODES {
		DAY, TODAY, YESTERDAY, WEEK, MONTH, YEAR
	}

	@SuppressWarnings("serial")
	final static Map<MODES, TimeDetailKey> MODES_DETAIL_MAPS = Collections
			.unmodifiableMap(new HashMap<MODES, TimeDetailKey>() {
				{
					put(MODES.DAY, TimeDetailKey.TYPE_1H);
					put(MODES.TODAY, TimeDetailKey.TYPE_1H);
					put(MODES.YESTERDAY, TimeDetailKey.TYPE_1H);
					put(MODES.WEEK, TimeDetailKey.TYPE_24H);
					put(MODES.MONTH, TimeDetailKey.TYPE_24H);
					put(MODES.YEAR, TimeDetailKey.TYPE_1MON);
				}
			});

	/**
	 *
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	public static Pair<LocalDateTime, LocalDateTime> calculateModeDatePairs(ZonedDateTime dateTime, String mode) {

		ZonedDateTime endOfDay = dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1);

		if (MODES.DAY.name().equals(mode)) {
			return Pair.of(dateTime.minusDays(1).toLocalDateTime(), endOfDay.toLocalDateTime());
		}

		if (MODES.TODAY.name().equals(mode)) {
			return Pair.of(dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime(), endOfDay.toLocalDateTime());
		}

		if (MODES.YESTERDAY.name().equals(mode)) {
			return Pair.of(dateTime.minusDays(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.minusDays(1).toLocalDateTime());
		}

		if (MODES.WEEK.name().equals(mode)) {
			return Pair.of(dateTime.minusDays(7).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.toLocalDateTime());
		}

		if (MODES.MONTH.name().equals(mode)) {
			// Last Month
			if (dateTime.getDayOfMonth() < 15) {
				return Pair.of(dateTime.minusMonths(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
						endOfDay.toLocalDateTime());
				// Current Month
			} else {
				return Pair.of(dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
						endOfDay.toLocalDateTime());
			}
		}

		if (MODES.YEAR.name().equals(mode)) {
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
	protected TimeDetailKey getDetailTypeKey(String mode) {
		TimeDetailKey result = MODES_DETAIL_MAPS.get(MODES.valueOf(mode));
		return result != null ? result : TimeDetailKey.TYPE_1H;
	}

	/**
	 *
	 * @return
	 */
	public abstract Collection<MODES> getAvailableModes();

	/**
	 *
	 * @param mode
	 * @return
	 */
	public boolean isModeSupported(String mode) {
		return getAvailableModes().stream().map(MODES::name).filter(i -> i.equals(mode.toUpperCase())).findAny()
				.isPresent();
	}

}
