package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDatePeriodParser {

	public static class ParserArguments {
		public final String dateFromStr;
		public final String dateToStr;

		private ParserArguments(String fromDateStr, String toDateStr) {
			this.dateFromStr = fromDateStr;
			this.dateToStr = toDateStr;
		}

		private ParserArguments() {
			this.dateFromStr = null;
			this.dateToStr = null;
		}
	}

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(LocalDatePeriod.DATE_TEMPLATE);

	public final static LocalDatePeriodParser EMPTY_PARSER = new LocalDatePeriodParser();

	private final boolean isEmpty;

	private final LocalDatePeriod datePeriod;

	private final ParserArguments parserArguments;

	/**
	 *
	 */
	public LocalDatePeriodParser() {
		isEmpty = true;
		datePeriod = LocalDatePeriod.emptyPeriod();
		this.parserArguments = new ParserArguments();
	}

//	private LocalDatePeriodParser(LocalDateTime fromDate, LocalDateTime toDate,
//			boolean isEmpty) {
//		this.parserArguments = new ParserArguments();
//		this.isEmpty = isEmpty;
//		this.datePeriod = LocalDatePeriod.builder().dateFrom(fromDate)
//				.dateTo(toDate).build();
//	}

	private LocalDatePeriodParser(ParserArguments parserArguments,
			LocalDateTime dateFrom, LocalDateTime dateTo, boolean isEmpty) {
		this.parserArguments = parserArguments;
		this.isEmpty = isEmpty;
		this.datePeriod = LocalDatePeriod.builder().dateFrom(dateFrom)
				.dateTo(dateTo).build();
	}

	/**
	 *
	 * @param dateFromStr
	 * @param dateToStr
	 */
	public static LocalDatePeriodParser parse(String dateFromStr,
			String dateToStr) {

		ParserArguments parserArguments = new ParserArguments(dateFromStr,
				dateToStr);

		LocalDatePeriodParser result = null;
		if (dateFromStr != null && dateToStr != null) {
			boolean emptyResult = false;
			LocalDateTime fromDate = null;
			LocalDateTime toDate = null;
			try {
				fromDate = DATE_FORMATTER.parseLocalDateTime(dateFromStr);
				toDate = DATE_FORMATTER.parseLocalDateTime(dateToStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new LocalDatePeriodParser(parserArguments, fromDate,
						toDate, emptyResult);
			}
		} else {
			result = EMPTY_PARSER;
		}

		checkNotNull(result);
		checkNotNull(result.parserArguments);

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

	public ParserArguments getParserArguments() {
		return parserArguments;
	}

}
