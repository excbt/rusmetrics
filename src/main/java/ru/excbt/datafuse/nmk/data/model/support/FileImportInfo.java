package ru.excbt.datafuse.nmk.data.model.support;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kovtonyk on 02.06.2017.
 */
@Getter
@Builder
public class FileImportInfo {

    public final static String IMPORT_ERROR_TEMPLATE = "Ошибка импорта данных. Файл %s";
    public final static String IMPORT_COMPLETE_TEMPLATE = "Данные из файла %s успешно загружены";
    public final static String IMPORT_EXCEPTION_TEMPLATE = "Data Import Error. %s. File: %s";


    private final String internalFileName;

    private final String userFileName;

    public FileImportInfo(String internalFileName, String userFileName) {
        this.internalFileName = internalFileName;
        this.userFileName = userFileName;
    }
}
