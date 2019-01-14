package ru.excbt.datafuse.nmk.data.model.support.time;


import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class LocalDatePeriodParser {

	public static class ParserArguments {
		public final String fromDateStr;
		public final String toDateStr;

		private ParserArguments(String fromDateStr, String toDateStr) {
			this.fromDateStr = fromDateStr;
			this.toDateStr = toDateStr;
		}

		private ParserArguments() {
			this.fromDateStr = null;
			this.toDateStr = null;
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

    public LocalDatePeriodParser(String dateFrom, String dateTo) {
		this.parserArguments = new ParserArguments(dateFrom, dateTo);
		this.isEmpty = false;
	}

	public boolean checkNulls() {
        return (parserArguments.fromDateStr == null) == (parserArguments.toDateStr == null);
    }

    public Optional<LocalDatePeriod> parse() {
        return LocalDatePeriodParser.parse(parserArguments.fromDateStr, parserArguments.toDateStr);
    }

    public Optional<LocalDatePeriod> parse(LocalDatePeriod orValue) {
        Objects.requireNonNull(orValue);
	    if (!checkNulls()) {
	        return Optional.of(orValue);
        }
        return LocalDatePeriodParser.parse(parserArguments.fromDateStr, parserArguments.toDateStr);
    }

	/**
	 *
	 * @param dateFromStr
	 * @param dateToStr
	 */
	public static Optional<LocalDatePeriod> parse(String dateFromStr,
                                 String dateToStr) {

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
