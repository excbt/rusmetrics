package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDateTime;

import ru.excbt.datafuse.nmk.report.ReportPeriodKey;

/**
 * Утилиты для работы набором параметров отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.04.2015
 *
 */
public class ReportParamsetDateUtil {

	/**
	 *
	 * @param reportDate
	 * @param reportPeriodKey
	 * @return
	 */
	private static LocalDateTime getStartDate2(LocalDateTime reportDate, ReportPeriodKey reportPeriodKey) {
		checkNotNull(reportDate);
		checkNotNull(reportPeriodKey);
		LocalDateTime result = null;
		switch (reportPeriodKey) {
		case TODAY: {
			result = reportDate.withMillisOfDay(0);
			break;
		}
		case YESTERDAY: {
			result = reportDate.minusDays(1).withMillisOfDay(0);
			break;
		}
		case CURRENT_MONTH: {
			result = reportDate.withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case SETTLEMENT_MONTH: {
			result = reportDate.withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case LAST_MONTH: {
			result = reportDate.minusMonths(1).withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case INTERVAL: {
			break;
		}

		default:
			break;

		}

		return result;
	}

	/**
	 *
	 * @param reportDate
	 * @param reportPeriodKey
	 * @return
	 */
	private static LocalDateTime getEndDate2(LocalDateTime reportDate, ReportPeriodKey reportPeriodKey) {
		checkNotNull(reportDate);
		checkNotNull(reportPeriodKey);
		LocalDateTime result = null;
		switch (reportPeriodKey) {
		case TODAY: {
			result = reportDate.withMillisOfDay(0);
			break;
		}
		case YESTERDAY: {
			result = reportDate.minusDays(1).withMillisOfDay(0);
			break;
		}
		case CURRENT_MONTH: {
			result = reportDate.withMillisOfDay(0).plusMonths(1).withDayOfMonth(1).minusDays(1);
			;
			break;
		}
		case LAST_MONTH: {
			result = reportDate.withMillisOfDay(0).withDayOfMonth(1).minusDays(1);

			break;
		}
		case INTERVAL: {
			break;
		}

		default:
			break;

		}

		return result;
	}

	/**
	 *
	 * @param reportDate
	 * @param reportPeriodKey
	 * @return
	 */
	public static LocalDateTime getStartDateTime(LocalDateTime reportDate, ReportPeriodKey reportPeriodKey) {
		checkNotNull(reportDate);
		checkNotNull(reportPeriodKey);
		LocalDateTime result = null;
		switch (reportPeriodKey) {
		case TODAY: {
			result = reportDate.withMillisOfDay(0);
			break;
		}
		case YESTERDAY: {
			result = reportDate.minusDays(1).withMillisOfDay(0);
			break;
		}
		case CURRENT_MONTH: {
			result = reportDate.withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case SETTLEMENT_MONTH: {
			result = reportDate.withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case LAST_MONTH: {
			result = reportDate.minusMonths(1).withDayOfMonth(1).withMillisOfDay(0);
			break;
		}
		case INTERVAL: {
			break;
		}

		default:
			break;

		}

		return result;
	}

	/**
	 *
	 * @param reportDate
	 * @param reportPeriodKey
	 * @return
	 */
	public static LocalDateTime getEndDateTime(LocalDateTime reportDate, ReportPeriodKey reportPeriodKey) {
		checkNotNull(reportDate);
		checkNotNull(reportPeriodKey);
		LocalDateTime result = null;
		switch (reportPeriodKey) {
		case TODAY: {
			result = reportDate.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
			break;
		}
		case YESTERDAY: {
			result = reportDate.minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
					.withMillisOfSecond(999);
			break;
		}
		case CURRENT_MONTH: {
			result = reportDate.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999)
					.plusMonths(1).withDayOfMonth(1).minusDays(1);
			break;
		}
		case SETTLEMENT_MONTH: {
			result = reportDate.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999)
					.plusMonths(1).withDayOfMonth(1).minusDays(1);
			break;
		}
		case LAST_MONTH: {
			result = reportDate.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999)
					.withDayOfMonth(1).minusDays(1);

			break;
		}
		case INTERVAL: {
			break;
		}

		default:
			break;

		}

		return result;
	}

}
