package ru.excbt.datafuse.nmk.data.service.support;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 01.06.2017.
 */
public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

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
     * @param multipartFiles
     * @return
     */
    public static List<CheckFileResult> checkCsvFiles(MultipartFile[] multipartFiles) {

        List<CheckFileResult> result = new ArrayList<>();


        for (MultipartFile multipartFile : multipartFiles) {

            CheckFileResult checkFilesResult = new CheckFileResult(multipartFile);

            String fileName = checkFilesResult.getFileName();
            logger.debug("Checking file to import {}", fileName);
            if (fileName == null || fileName.isEmpty()) {
                checkFilesResult.errorDesc = "Имя файла не указано";
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
     * @param fileName
     * @return
     */
    public static String extractFileName(String fileName) {
        return fileName != null ? FilenameUtils.getName(fileName) : "";
    }

    /**
     * @param fileName
     * @return
     */
    public static String extractFileExtention(String fileName) {
        return fileName != null ? FilenameUtils.getExtension(fileName) : "";
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean checkCsvSeparators(File file) throws IOException {
        boolean result;
        Charset charset = determineCharset(file);

        try (FileInputStream is = new FileInputStream(file)) {
            InputStreamReader isr = new InputStreamReader(is, charset);
            result = checkCsvSeparatorReader(isr);
        }
        return result;
    }

    /**
     * @param byteArray
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean checkByteCsvSeparators(byte[] byteArray) throws IOException {
        boolean result = true;

        try (InputStream is = new ByteArrayInputStream(byteArray)) {
            Charset charset = determineCharset(is);
            InputStreamReader isr = new InputStreamReader(is, charset);
            result = checkCsvSeparatorReader(isr);
        }
        return result;
    }

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    private static boolean checkCsvSeparatorReader(Reader reader) throws IOException {
        boolean result = true;
        try (BufferedReader br = new BufferedReader(reader)) {
            String header = br.readLine();
            int checkCnt = StringUtils.countOccurrencesOf(header, ",");
            String line;
            while (result && (line = br.readLine()) != null) {
                int lineCnt = StringUtils.countOccurrencesOf(line, ",");
                result = result & (lineCnt == checkCnt);
            }
        }
        return result;
    }


    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static Charset determineCharset(File file) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            return determineCharset(fis);
        }
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static Charset determineCharset(InputStream is) throws IOException {

        Charset charset;
        UniversalDetector detector = new UniversalDetector(null);

        byte[] buf = new byte[4096];
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();

        if (encoding != null) {
            charset = Charset.forName(encoding);
        } else
            charset = StandardCharsets.UTF_8;

        detector.reset();
        return charset;
    }

}
