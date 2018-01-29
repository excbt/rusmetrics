package ru.excbt.datafuse.nmk.data.model.support.time;


import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

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

	private final boolean isEmpty;


	private final ParserArguments parserArguments;

	/**
	 *
	 */
	private LocalDatePeriodParser() {
		isEmpty = true;
		this.parserArguments = new ParserArguments();
	}

    private LocalDatePeriodParser(String dateFrom, String dateTo) {
		this.parserArguments = new ParserArguments(dateFrom, dateTo);
		this.isEmpty = false;
	}

	/**
	 *
	 * @param dateFromStr
	 * @param dateToStr
	 */
	public static Optional<LocalDatePeriod> parse(String dateFromStr,
                                 String dateToStr) {

		ParserArguments parserArguments = new ParserArguments(dateFromStr,
				dateToStr);

        Optional<LocalDatePeriod> result = Optional.empty();
		if (dateFromStr != null && dateToStr != null) {
			boolean emptyResult = false;
			LocalDate fromDate = null;
			LocalDate toDate = null;
			try {
				fromDate = LocalDate.parse(dateFromStr, LocalDatePeriod.formatter);
				toDate = LocalDate.parse(dateToStr, LocalDatePeriod.formatter);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
                result = Optional.of(LocalDatePeriod.builder().dateFrom(fromDate).dateTo(toDate).build());
			}
		} else {
			result = Optional.empty();
		}

		return result;
	}

}
