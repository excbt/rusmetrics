package ru.excbt.datafuse.nmk.data.model.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@AllArgsConstructor
@Getter
public class InstantPeriod implements DateInterval {
    public final static String DATE_TEMPLATE = "yyyy-MM-dd";
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_TEMPLATE);

    private final Instant dateTimeFrom;

    private final Instant dateTimeTo;

    @Override
    public Date getFromDate() {
        return Date.from(dateTimeFrom);
    }

    @Override
    public Date getToDate() {
        return Date.from(dateTimeTo);
    }

    @Override
    public boolean isValid() {
        if (dateTimeFrom == null || dateTimeTo == null) return false;

        return dateTimeFrom.isBefore(dateTimeTo);
    }

    @Override
    public boolean isValidEq() {
        if (dateTimeFrom == null || dateTimeTo == null) return false;

        return dateTimeFrom.compareTo(dateTimeTo) >= 0;
    }
}
