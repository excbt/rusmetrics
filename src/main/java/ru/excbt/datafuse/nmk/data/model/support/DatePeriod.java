package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.joda.time.DateTime;

import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Deprecated
public class DatePeriod implements DateInterval {

	public static class Builder {

		private DateTime dateTimeFrom = null;
		private DateTime dateTimeTo = null;

		private Builder() {

		}

		private Builder(DatePeriod src) {
			this.dateTimeFrom = src.dateTimeFrom;
			this.dateTimeTo = src.dateTimeTo;
		}

		public Builder dateFrom(DateTime d) {
			this.dateTimeFrom = d;
			return this;
		}

		public Builder dateTo(DateTime d) {
			this.dateTimeTo = d;
			return this;
		}

		public DatePeriod build() {
			return new DatePeriod(this);
		}

	}

	private final DateTime dateTimeFrom;
	private final DateTime dateTimeTo;

	protected DatePeriod() {
		this.dateTimeFrom = null;
		this.dateTimeTo = null;
	}

	protected DatePeriod(Builder b) {
		this.dateTimeFrom = b.dateTimeFrom;
		this.dateTimeTo = b.dateTimeTo;
	}

	public static DatePeriod emptyPeriod() {
		return new DatePeriod();
	}

	private static DateTime endOfDay(DateTime dateTime) {
		checkNotNull(dateTime);
		DateTime endOfDay = dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
				.withMillisOfSecond(999);
		return endOfDay;
	}

	private static DateTime startOfDay(DateTime dateTime) {
		checkNotNull(dateTime);
		DateTime endOfDay = dateTime.withMillisOfDay(0);
		return endOfDay;
	}

	public static DatePeriod lastWeek() {
		DatePeriod result = builder().dateFrom(startOfDay(DateTime.now().minusWeeks(1)))
				.dateTo(endOfDay(DateTime.now())).build();
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(DatePeriod src) {
		return new Builder(src);
	}

	public DateTime getDateTimeFrom() {
		return dateTimeFrom;
	}

	public DateTime getDateTimeTo() {
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
	public DatePeriod buildEndOfDay() {

		checkState(isValidEq(), "Can't build DatePeriod from invalid source");
		DateTime endOfDay = JodaTimeUtils.endOfDay(dateTimeTo);

		return DatePeriod.builder(this).dateTo(endOfDay).build();

	}

	@Override
	public String toString() {
		return "DatePeriod [dateTimeFrom=" + dateTimeFrom + ", dateTimeTo=" + dateTimeTo + "]";
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.DateInterval#getFromDate()
	 */
	@Override
	public Date getFromDate() {
		// TODO Auto-generated method stub
		return getDateFrom();
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.DateInterval#getToDate()
	 */
	@Override
	public Date getToDate() {
		// TODO Auto-generated method stub
		return getDateTo();
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.DateInterval#getFromDate()
	 */

}
