package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.excbt.datafuse.nmk.data.service.ReportService;

public class DatePeriodParser {

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	private final DateTime fromDate;
	private final DateTime toDate;
	private final boolean isEmpty;

	private final DatePeriod datePeriod;

	/**
	 * 
	 */
	public DatePeriodParser() {
		fromDate = null;
		toDate = null;
		isEmpty = true;
		datePeriod = DatePeriod.emptyPeriod();
	}

	private DatePeriodParser(DateTime fromDate, DateTime toDate, boolean isEmpty) {
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.isEmpty = isEmpty;
		this.datePeriod = DatePeriod.builder().dateFrom(fromDate).dateTo(toDate)
				.build();
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 */
	public static DatePeriodParser parse(String fromDateStr, String toDateStr) {
		DatePeriodParser result = null;
		if (fromDateStr != null && toDateStr != null) {
			boolean emptyResult = false;
			DateTime fromDate = null;
			DateTime toDate = null;
			try {
				fromDate = DATE_FORMATTER.parseDateTime(fromDateStr);
				toDate = DATE_FORMATTER.parseDateTime(toDateStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new DatePeriodParser(fromDate, toDate, emptyResult);
			}
		} else {
			result = new DatePeriodParser();
		}
		return result;
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime endOfDay(DateTime dateTime) {
		checkNotNull(dateTime);
		DateTime endOfDay = dateTime.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);
		return endOfDay;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public boolean isOk() {
		return !isEmpty;
	}


	public DatePeriod getDatePeriod() {
		return datePeriod;
	}

}
