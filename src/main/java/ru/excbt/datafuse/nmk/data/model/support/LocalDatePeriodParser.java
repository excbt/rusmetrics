package ru.excbt.datafuse.nmk.data.model.support;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDatePeriodParser {

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(LocalDatePeriod.DATE_TEMPLATE);

	public final static LocalDatePeriodParser EMPTY_PARSER = new LocalDatePeriodParser();

	private final boolean isEmpty;

	private final LocalDatePeriod datePeriod;

	/**
	 * 
	 */
	public LocalDatePeriodParser() {
		isEmpty = true;
		datePeriod = LocalDatePeriod.emptyPeriod();
	}

	private LocalDatePeriodParser(LocalDateTime fromDate, LocalDateTime toDate,
			boolean isEmpty) {
		this.isEmpty = isEmpty;
		this.datePeriod = LocalDatePeriod.builder().dateFrom(fromDate)
				.dateTo(toDate).build();
	}

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 */
	public static LocalDatePeriodParser parse(String fromDateStr,
			String toDateStr) {
		LocalDatePeriodParser result = null;
		if (fromDateStr != null && toDateStr != null) {
			boolean emptyResult = false;
			LocalDateTime fromDate = null;
			LocalDateTime toDate = null;
			try {
				fromDate = DATE_FORMATTER.parseLocalDateTime(fromDateStr);
				toDate = DATE_FORMATTER.parseLocalDateTime(toDateStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new LocalDatePeriodParser(fromDate, toDate,
						emptyResult);
			}
		} else {
			result = EMPTY_PARSER;
		}
		return result;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public boolean isOk() {
		return !isEmpty && datePeriod != null;
	}

	public LocalDatePeriod getLocalDatePeriod() {
		return datePeriod;
	}

}
