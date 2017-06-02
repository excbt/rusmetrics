package ru.excbt.datafuse.nmk.data.service.support;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 01.06.2017.
 */
public class CsvUtils {


    private static final Logger logger = LoggerFactory.getLogger(CsvUtils.class);

    private CsvUtils() {
    }

    @Getter
    public static class CheckFileResult {
        private final MultipartFile multipartFile;
        private String errorDesc;
        boolean passed = false;

        private CheckFileResult(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
            this.passed = multipartFile != null;
        }

        public String getFileName() {
            return multipartFile != null ? FilenameUtils.getName(multipartFile.getOriginalFilename()) : "";
        }


        public String getExtension() {
            return multipartFile != null ? FilenameUtils.getExtension(multipartFile.getOriginalFilename()) : "";
        }

    }


    /**
     *
     * @param multipartFiles
     * @return
     */
    public static List<CheckFileResult> checkCsvFiles (MultipartFile[] multipartFiles) {

        List<CheckFileResult> result = new ArrayList<>();


        for (MultipartFile multipartFile : multipartFiles) {

            CheckFileResult checkFilesResult = new CheckFileResult(multipartFile);

            String fileName = checkFilesResult.getFileName();
            logger.debug("Checking file to import {}", fileName);
            if (fileName == null || fileName.isEmpty()) {
                checkFilesResult.errorDesc =  "Имя файла не указано";
                continue;
            }

            if (!"csv".equalsIgnoreCase(checkFilesResult.getExtension())) {
                checkFilesResult.errorDesc = "Некоррекный тип файла: " + fileName + ". Ожидается расширение CSV";
                continue;
            }
            checkFilesResult.passed = true;
            result.add(checkFilesResult);
        }

        return result;

    }


    /**
     *
     * @param fileName
     * @return
     */
    public static String extractFileName(String fileName) {
        return fileName != null ? FilenameUtils.getName(fileName) : "";
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String extractFileExtention(String fileName) {
        return fileName != null ? FilenameUtils.getExtension(fileName) : "";
    }

}
