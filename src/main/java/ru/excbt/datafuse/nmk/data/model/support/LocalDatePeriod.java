package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.joda.time.LocalDateTime;

import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

public class LocalDatePeriod {

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

		public Builder dateTo(LocalDateTime d) {
			this.dateTimeTo = d;
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
		LocalDateTime endOfDay = dateTime.withHourOfDay(23)
				.withMinuteOfHour(59).withSecondOfMinute(59)
				.withMillisOfSecond(999);
		return endOfDay;
	}

	private static LocalDateTime startOfDay(LocalDateTime dateTime) {
		checkNotNull(dateTime);
		LocalDateTime endOfDay = dateTime.withMillisOfDay(0);
		return endOfDay;
	}

	public static LocalDatePeriod lastWeek() {
		LocalDatePeriod result = builder()
				.dateFrom(startOfDay(LocalDateTime.now().minusWeeks(1)))
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
		return dateTimeFrom.isBefore(dateTimeTo)
				|| dateTimeFrom.isEqual(dateTimeTo);
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

	@Override
	public String toString() {
		return "DatePeriod [dateTimeFrom=" + dateTimeFrom + ", dateTimeTo="
				+ dateTimeTo + "]";
	}

}
