package ru.excbt.datafuse.nmk.data.model.support;

import java.time.LocalDate;

public class InstantPeriodLib {


    public static InstantPeriod month (int year, int month) {

        LocalDate startDay = LocalDate.of(year, month, 1);
        LocalDate endDay = startDay.plusMonths(1).minusDays(1);

        return null;
    }

}
