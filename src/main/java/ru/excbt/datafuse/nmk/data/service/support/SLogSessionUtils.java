package ru.excbt.datafuse.nmk.data.service.support;

import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.service.utils.CsvUtil;
import ru.excbt.datafuse.slogwriter.service.SLogSession;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by kovtonyk on 02.06.2017.
 */
public class SLogSessionUtils {

    private SLogSessionUtils() {
    }

    /**
     *
     * @param session
     * @param errorMessage
     * @param statusMessage
     */
    public static void failSession(SLogSession session, String errorMessage, String statusMessage) {
        if (errorMessage != null) {
            session.web().error(errorMessage);
        }
        session.status(SLogSessionStatuses.FAILURE.getKeyname(),statusMessage);
    }

    /**
     *
     * @param session
     * @param statusMsg
     */
    public static void completeSession(SLogSession session, String statusMsg) {
        session.web().info(statusMsg);
        session.status(SLogSessionStatuses.COMPLETE.getKeyname(), statusMsg);
    }


    /**
     *
     * @param session
     * @param fileImportInfo
     * @return
     */
    public static void checkCsvSeparators(SLogSession session, final FileImportInfo fileImportInfo) {

        String fileNameErrorTemplate = String.format(FileImportInfo.IMPORT_ERROR_TEMPLATE, fileImportInfo.getUserFileName());

        boolean checkCsvSeparators;
        try {
            checkCsvSeparators = CsvUtil.checkCsvSeparators(new File(fileImportInfo.getInternalFileName()));
        } catch (FileNotFoundException e1) {

            failSession(session,
                "Ошибка. Файл не может быть проверен", fileNameErrorTemplate);

            throw new IllegalArgumentException(String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE,
                "Check CSV separators error", fileImportInfo.getUserFileName()));
        } catch (IOException e1) {

            failSession(session,
                "Ошибка. Файл не может быть проверен", fileNameErrorTemplate);

            throw new IllegalArgumentException(String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE,
                "Check CSV separators error", fileImportInfo.getUserFileName()));
        }

        if (!checkCsvSeparators) {

            SLogSessionUtils.failSession(session,
                "Ошибка. Файл не содержит полных данных", fileNameErrorTemplate);

            throw new IllegalArgumentException(String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE,
                "Check CSV separators error", fileImportInfo.getUserFileName()));
        }

    }

}
