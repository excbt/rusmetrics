package ru.excbt.datafuse.nmk.web;

/**
 * Created by kovtonyk on 05.06.2017.
 */
public final class ApiConst {

    private ApiConst() {
    }

    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    public final static String MIME_ZIP = "application/zip";
    public final static String MIME_PDF = "application/pdf";
    public final static String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public final static String MIME_XLS = "application/vnd.ms-excel";

    public final static String MIME_CSV = "text/csv";
    public final static String MIME_TEXT = "text/html";

    public final static String FILE_CSV_EXT = ".csv";

    public static final int DEFAULT_PAGE_SIZE = 100;
}
