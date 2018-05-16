package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

@Deprecated
public class LocalDatePeriod implements DateInterval {

	public final static String DATE_TEMPLATE = "yyyy-MM-dd";
	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_TEMPLATE);

	public static class Builder {

		private LocalDateTime dateTimeFrom = null;
		private LocalDateTime dateTimeTo = null;

		private Builder() {

		}

		private Builder(LocalDatePeriod src) {
			this.dateTimeFrom = src.dateTimeFrom;
			this.dateTimeTo = src.dateTimeTo;
		}

		public Builder dateFrom(LocalDateTime d) {
			this.dateTimeFrom = d;
			return this;
		}

		public Builder dateFrom(java.time.LocalDateTime d) {
			this.dateTimeFrom = new LocalDateTime(LocalDateUtils.asDate(d));
			return this;
		}

		public Builder dateFrom(String s) {
			this.dateTimeFrom = LocalDateTime.parse(s, DATE_FORMATTER);
			return this;
		}

		public Builder dateTo(LocalDateTime d) {
			this.dateTimeTo = d;
			return this;
		}

		public Builder dateTo(java.time.LocalDateTime d) {
			this.dateTimeTo = new LocalDateTime(LocalDateUtils.asDate(d));
			return this;
		}

		public Builder dateTo(String s) {
			this.dateTimeTo = LocalDateTime.parse(s, DATE_FORMATTER);
			return this;
		}

		public LocalDatePeriod build() {
			return new LocalDatePeriod(this);
		}

	}

	private final LocalDateTime dateTimeFrom;
	private final LocalDateTime dateTimeTo;

	protected LocalDatePeriod() {
		this.dateTimeFrom = null;
		this.dateTimeTo = null;
	}

	protected LocalDatePeriod(Builder b) {
		this.dateTimeFrom = b.dateTimeFrom;
		this.dateTimeTo = b.dateTimeTo;
	}

	public static LocalDatePeriod emptyPeriod() {
		return new LocalDatePeriod();
	}

	private static LocalDateTime endOfDay(LocalDateTime dateTime) {
		checkNotNull(dateTime);
		LocalDateTime endOfDay = dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
				.withMillisOfSecond(999);
		return endOfDay;
	}

	private static LocalDateTime startOfDay(LocalDateTime dateTime) {
		checkNotNull(dateTime);
		LocalDateTime endOfDay = dateTime.withMillisOfDay(0);
		return endOfDay;
	}

	public static LocalDatePeriod lastDay() {
		LocalDatePeriod result = builder().dateFrom(startOfDay(LocalDateTime.now().minusDays(1)))
				.dateTo(endOfDay(LocalDateTime.now())).build();
		return result;
	}

	public static LocalDatePeriod lastWeek() {
		LocalDatePeriod result = builder().dateFrom(startOfDay(LocalDateTime.now().minusWeeks(1)))
				.dateTo(endOfDay(LocalDateTime.now())).build();
		return result;
	}

	public static LocalDatePeriod lastMonth() {
		LocalDatePeriod result = builder().dateFrom(startOfDay(LocalDateTime.now().minusMonths(1)))
				.dateTo(endOfDay(LocalDateTime.now())).build();
		return result;
	}

	public static LocalDatePeriod lastYear() {
		LocalDatePeriod result = builder().dateFrom(startOfDay(LocalDateTime.now().minusYears(1)))
				.dateTo(endOfDay(LocalDateTime.now())).build();
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(LocalDatePeriod src) {
		return new Builder(src);
	}

	public LocalDateTime getDateTimeFrom() {
		return dateTimeFrom;
	}

	public LocalDateTime getDateTimeTo() {
		return dateTimeTo;
	}

	public Date getDateFrom() {
		return dateTimeFrom == null ? null : dateTimeFrom.toDate();
	}

	public Date getDateTo() {
		return dateTimeTo == null ? null : dateTimeTo.toDate();
	}

	public boolean isValidEq() {
		if (dateTimeFrom == null || dateTimeTo == null) {
			return false;
		}
		return dateTimeFrom.isBefore(dateTimeTo) || dateTimeFrom.isEqual(dateTimeTo);
	}

	public boolean isValid() {
		if (dateTimeFrom == null || dateTimeTo == null) {
			return false;
		}
		return dateTimeFrom.isBefore(dateTimeTo);
	}

	public boolean isInvalid() {
		return !isValid();
	}

	public boolean isInvalidEq() {
		return !isValidEq();
	}

	/**
	 *
	 * @return
	 */
	public LocalDatePeriod buildEndOfDay() {

		checkState(isValidEq(), "Can't build DatePeriod from invalid source");
		LocalDateTime endOfDay = JodaTimeUtils.endOfDay(dateTimeTo);

		return LocalDatePeriod.builder(this).dateTo(endOfDay).build();

	}

	/**
	 *
	 * @return
	 */
	public LocalDatePeriod buildDateToNextMonth() {
		LocalDateTime startOfNextMonth = dateTimeTo.withMillisOfDay(0).withDayOfMonth(1).plusMonths(1);
		return LocalDatePeriod.builder(this).dateTo(startOfNextMonth).build();
	}

	/**
	 *
	 * @return
	 */
	public LocalDatePeriod buildDateToNextDay() {
		LocalDateTime startOfNextMonth = dateTimeTo.withMillisOfDay(0).plusDays(1);
		return LocalDatePeriod.builder(this).dateTo(startOfNextMonth).build();
	}

	@Override
	public String toString() {
		return "DatePeriod [dateTimeFrom=" + dateTimeFrom + ", dateTimeTo=" + dateTimeTo + "]";
	}

	public String getDateFromStr() {
		return dateTimeTo == null ? null : dateTimeFrom.toString(DATE_TEMPLATE);
	}

	public String getDateToStr() {
		return dateTimeTo == null ? null : dateTimeTo.toString(DATE_TEMPLATE);
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.DateInterval#getFromDate()
	 */
	@Override
	public Date getFromDate() {
		return getDateFrom();
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.DateInterval#getToDate()
	 */
	@Override
	public Date getToDate() {
		return getDateTo();
	}

}
