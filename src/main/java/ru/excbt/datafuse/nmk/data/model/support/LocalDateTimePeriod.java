package ru.excbt.datafuse.nmk.data.model.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.excbt.datafuse.nmk.utils.AnyPeriod;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@AllArgsConstructor
public class LocalDateTimePeriod implements AnyPeriod<LocalDateTime>, DateInterval {
    public final static String DATE_TEMPLATE = "yyyy-MM-dd";
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_TEMPLATE);

    private final LocalDateTime dateTimeFrom;

    private final LocalDateTime dateTimeTo;

    @Override
    public Date getFromDate() {
        return LocalDateUtils.asDate(dateTimeFrom);
    }

    @Override
    public Date getToDate() {
        return LocalDateUtils.asDate(dateTimeTo);
    }

    @Override
    public LocalDateTime getFrom() {
        return dateTimeFrom;
    }

    @Override
    public LocalDateTime getTo() {
        return dateTimeTo;
    }

    @Override
    public boolean isValid() {
        if (dateTimeFrom == null || dateTimeTo == null) return false;

        return dateTimeFrom.isBefore(dateTimeTo);
    }

    @Override
    public boolean isValidEq() {
        if (dateTimeFrom == null || dateTimeTo == null) return false;

        return dateTimeFrom.isBefore(dateTimeTo) || dateTimeFrom.isEqual(dateTimeTo);
    }


    public static LocalDateTimePeriod month (int year, int month) {
        LocalDate d = LocalDate.of(year, month, 1);
        LocalDateTime startDay = d.atStartOfDay();
        LocalDateTime endDay = startDay.plusMonths(1).minusSeconds(1);
        return builder().dateTimeFrom(startDay).dateTimeTo(endDay).build();
    }

    public static LocalDateTimePeriod year (int year) {
        LocalDate d = LocalDate.of(year, 1, 1);
        LocalDateTime startDay = d.atStartOfDay();
        LocalDateTime endDay = startDay.plusYears(1).minusSeconds(1);
        return builder().dateTimeFrom(startDay).dateTimeTo(endDay).build();
    }

    public static LocalDateTimePeriod day (int year, int month, int day) {
        LocalDate d = LocalDate.of(year, month, day);
        LocalDateTime startDay = d.atStartOfDay();
        LocalDateTime endDay = startDay.plusDays(1).minusSeconds(1);
        return builder().dateTimeFrom(startDay).dateTimeTo(endDay).build();
    }


    public static LocalDateTimePeriod fromPair(Pair<LocalDateTime, LocalDateTime> datePairs) {
        return builder().dateTimeFrom(datePairs.getLeft()).dateTimeTo(datePairs.getRight()).build();
    }

    public static LocalDateTimePeriod currentMonth() {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startDay = d.atStartOfDay();
        LocalDateTime endDay = startDay.plusMonths(1).minusSeconds(1);
        return builder().dateTimeFrom(startDay).dateTimeTo(endDay).build();
    }

}
