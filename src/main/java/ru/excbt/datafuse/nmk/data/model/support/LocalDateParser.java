package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.03.2016
 *
 */
public class LocalDateParser {

	public static class ParserArguments {
		public final String dateStr;

		private ParserArguments(String dateStr) {
			this.dateStr = dateStr;
		}

		private ParserArguments() {
			this.dateStr = null;
		}
	}

	private final ParserArguments parserArguments;
	private final boolean isEmpty;
	private final LocalDateTime dateValue;

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(LocalDatePeriod.DATE_TEMPLATE);

	public final static LocalDateParser EMPTY_PARSER = new LocalDateParser();

	/**
	 * 
	 * @param parserArguments
	 * @param dateValue
	 * @param isEmpty
	 */
	private LocalDateParser(ParserArguments parserArguments, LocalDateTime dateValue, boolean isEmpty) {
		this.parserArguments = parserArguments;
		this.isEmpty = isEmpty;
		this.dateValue = dateValue;
	}

	/**
	 * 
	 */
	private LocalDateParser() {
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
	public LocalDateTime getDateValue() {
		return dateValue;
	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 */
	public static LocalDateParser parse(String dateStr) {

		ParserArguments parserArguments = new ParserArguments(dateStr);

		LocalDateParser result = null;
		if (dateStr != null) {
			boolean emptyResult = false;
			LocalDateTime dateValue = null;
			try {
				dateValue = DATE_FORMATTER.parseLocalDateTime(dateStr);
				emptyResult = false;
			} catch (Exception e) {
				emptyResult = true;
			}
			if (!emptyResult) {
				result = new LocalDateParser(parserArguments, dateValue, emptyResult);
			}
		} else {
			result = EMPTY_PARSER;
		}

		checkNotNull(result.parserArguments);

		return result;
	}

}
