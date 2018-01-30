package ru.excbt.datafuse.nmk.data.model.support.time;

import lombok.Builder;
import ru.excbt.datafuse.nmk.utils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;



@Builder
public class LocalDatePeriod implements AnyPeriod<LocalDate>, DateInterval, TimeToString {

    public final static String DATE_TEMPLATE = "yyyy-MM-dd";
    public final static DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(DATE_TEMPLATE);


    private final LocalDate dateFrom;
    private final LocalDate dateTo;


	@Override
	public Date getFromDate() {
		return LocalDateUtils.asDate(getFrom());
	}

	@Override
	public Date getToDate() {
		return LocalDateUtils.asDate(getTo());
	}

    @Override
    public LocalDate getFrom() {
        return dateFrom;
    }

    @Override
    public LocalDate getTo() {
        return dateTo;
    }

    public static LocalDatePeriod currentMonth() {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        LocalDate startDay = d;
        LocalDate endDay = startDay.plusMonths(1).minusDays(1);
        return builder().dateFrom(startDay).dateTo(endDay).build();
    }

    public static LocalDatePeriod month (int year, int month) {
        LocalDate d = LocalDate.of(year, month, 1);
        LocalDate startDay = d;
        LocalDate endDay = startDay.plusMonths(1).minusDays(1);
        return builder().dateFrom(startDay).dateTo(endDay).build();
    }


    public LocalDateTimePeriod toLocalDateTimePeriod(boolean rightEndOfDay) {

	    if (isInvalid() || isInvalidEq()) {
	        throw new IllegalStateException("LocalDatePeriod is in illegal state: INVALID");
        }

	    LocalDateTime newFrom = dateFrom.atStartOfDay();
	    LocalDateTime newTo = rightEndOfDay ? dateTo.atStartOfDay().plusDays(1).minusSeconds(1) : dateTo.atStartOfDay();
        return LocalDateTimePeriod.builder().dateTimeFrom(newFrom).dateTimeTo(newTo).build();
    }

    public LocalDateTimePeriod toLocalDateTimePeriod() {
	    return toLocalDateTimePeriod(true);
    }

    @Override
    public String dateFromStr() {
        return dateFrom != null ? dateFrom.format(formatter) : null;
    }

    @Override
    public String dateToStr() {
        return dateTo != null ? dateTo.format(formatter) : null;
    }
}
