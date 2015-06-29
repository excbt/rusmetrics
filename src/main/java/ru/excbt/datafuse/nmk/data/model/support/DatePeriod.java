package ru.excbt.datafuse.nmk.data.model.support;

import java.util.Date;

import org.joda.time.DateTime;

public class DatePeriod {

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
	
	
}
