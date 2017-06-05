package ru.excbt.datafuse.nmk.web.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.excbt.datafuse.nmk.data.service.ReportService;

/**
 * Created by kovtonyk on 05.06.2017.
 */
public final class ApiJodaDateFormatter {

    private ApiJodaDateFormatter() {
    }

    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(ReportService.DATE_TEMPLATE);
}
