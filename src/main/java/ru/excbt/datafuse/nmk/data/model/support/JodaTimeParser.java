package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2016
 *
 */
public class JodaTimeParser<T> {

	public static class ParserArguments {
		public final String dateStr;

		protected ParserArguments(String dateStr) {
			this.dateStr = dateStr;
		}

		protected ParserArguments() {
			this.dateStr = null;
		}
	}

	protected final ParserArguments parserArguments;
	protected final boolean isEmpty;
	protected final T dateValue;

	public final static String DATE_TEMPLATE = "yyyy-MM-dd";
	public final static String DATE_TIME_TEMPLATE = "yyyy-MM-dd kk:mm:ss";

	public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
			.forPattern(LocalDatePeriod.DATE_TEMPLATE);

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_TEMPLATE);

	/**
	 *
	 * @param parserArguments
	 * @param dateValue
	 * @param isEmpty
	 */
	protected JodaTimeParser(ParserArguments parserArguments, T dateValue, boolean isEmpty) {
		this.parserArguments = parserArguments;
		this.isEmpty = isEmpty;
		this.dateValue = dateValue;
	}

	/**
	 *
	 */
	protected JodaTimeParser() {
		this.parserArguments = new ParserArguments();
		this.isEmpty = true;
		this.dateValue = null;
	}

	/**
	 *
	 * @return
	 */
	public ParserArguments getParserArguments() {
		return parserArguments;
	}

	/**
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 *
	 * @return
	 */
	public T getDateValue() {
		return dateValue;
	}

	/**
	 *
	 * @return
	 */
	public String getStringValue() {
		return this.parserArguments.dateStr;
	}

	/**
	 *
	 * @param dateStr
	 * @return
	 */
	public static JodaTimeParser<LocalDateTime> parseLocalDateTime(String dateStr) {

		ParserArguments parserArguments = new ParserArguments(dateStr);

		JodaTimeParser<LocalDateTime> result = null;
		if (dateStr != null) {
			boolean emptyResult = false;
			LocalDateTime dateValue = null;
			try {

				dateValue = DATE_TIME_FORMATTER.parseLocalDateTime(dateStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new JodaTimeParser<LocalDateTime>(parserArguments, dateValue, emptyResult);
			}
		} else {
			result = new JodaTimeParser<LocalDateTime>();
		}

		checkNotNull(result);
		checkNotNull(result.parserArguments);

		return result;
	}

	/**
	 *
	 * @param dateStr
	 * @return
	 */
	public static JodaTimeParser<LocalDate> parseLocalDate(String dateStr) {

		ParserArguments parserArguments = new ParserArguments(dateStr);

		JodaTimeParser<LocalDate> result = null;
		if (dateStr != null) {
			boolean emptyResult = false;
			LocalDate dateValue = null;
			try {

				dateValue = DATE_FORMATTER.parseLocalDate(dateStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new JodaTimeParser<LocalDate>(parserArguments, dateValue, emptyResult);
			}
		} else {
			result = new JodaTimeParser<LocalDate>();
		}

		checkNotNull(result);
		checkNotNull(result.parserArguments);

		return result;
	}

}
