package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для работы с файлами CSV
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.07.2015
 *
 */
public class HWatersCsvFileUtils {

	private static final Logger logger = LoggerFactory.getLogger(HWatersCsvFileUtils.class);

	public static final String MD5_EXT = ".md5";
	public static final String MD5 = "md5";

	public static final String CSV_EXT = ".csv";
	public static final String CSV = "csv";

	private HWatersCsvFileUtils() {

	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @return
	 */
	public static List<File> getOutFiles(HWatersCsvProps hWatersCsvProps, Long subscriberId) {

		return getDirectoryFiles(hWatersCsvProps.getHWatersCsvOutputDir(), subscriberId);
	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @param filename
	 * @return
	 */
	public static File getOutCsvFile(HWatersCsvProps hWatersCsvProps, Long subscriberId, String filename) {

		return getDirectoryFile(hWatersCsvProps.getHWatersCsvOutputDir(), subscriberId, filename, CSV_EXT);
	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @param filename
	 * @return
	 */
	public static File getOutMd5File(HWatersCsvProps hWatersCsvProps, Long subscriberId, String filename) {

		return getDirectoryFile(hWatersCsvProps.getHWatersCsvOutputDir(), subscriberId, filename, MD5_EXT);
	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @return
	 */
	public static List<File> getInFiles(HWatersCsvProps hWatersCsvProps, Long subscriberId) {

		return getDirectoryFiles(hWatersCsvProps.getHWatersCsvInputDir(), subscriberId);
	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @param filename
	 * @return
	 */
	public static File getInCsvFile(HWatersCsvProps hWatersCsvProps, Long subscriberId, String filename) {

		return getDirectoryFile(hWatersCsvProps.getHWatersCsvInputDir(), subscriberId, filename, CSV_EXT);
	}

	/**
	 * 
	 * @param hWatersCsvProps
	 * @param subscriberId
	 * @param filename
	 * @return
	 */
	public static File getInMd5File(HWatersCsvProps hWatersCsvProps, Long subscriberId, String filename) {

		return getDirectoryFile(hWatersCsvProps.getHWatersCsvInputDir(), subscriberId, filename, MD5_EXT);
	}

	/**
	 * 
	 * @param directory
	 * @param subscriberId
	 * @return
	 */
	private static List<File> getDirectoryFiles(String directory, Long subscriberId) {

		if (directory == null || subscriberId == null) {
			return Collections.<File> emptyList();
		}

		File dir = new File(getFullPath(directory, subscriberId, null));
		if (!dir.isDirectory()) {
			return Collections.<File> emptyList();
		}

		List<File> listFiles = Arrays.asList(dir.listFiles());
		List<File> resultFiles = listFiles.stream()
				.filter((i) -> i.isFile() && !isMD5File(i.getName()) && isCsvFile(i.getName()))
				.collect(Collectors.toList());

		return resultFiles;
	}

	/**
	 * 
	 * @param directory
	 * @param subscriberId
	 * @param filename
	 * @return
	 */
	private static File getDirectoryFile(String directory, Long subscriberId, String filename, String extention) {

		checkNotNull(extention);

		logger.debug("Fire getDirectoryFile. directory:{}, subscriberId:{}, filename:{}", directory, subscriberId,
				filename);

		if (directory == null || subscriberId == null || filename == null) {
			logger.warn("directory or subscriberId or filename is null");
			return null;
		}

		String requestedFile = filename;

		if (requestedFile.contains(File.separator)) {
			logger.warn("filename contains '{}'", File.separator);
			return null;
		}

		if (!extention.equals(FilenameUtils.getExtension(requestedFile))) {
			requestedFile = requestedFile + extention;
		}

		File result = new File(getFullPath(directory, subscriberId, requestedFile));
		if (result.isDirectory()) {
			logger.warn("filename is directory:{}", result.getAbsolutePath());
			return null;
		}

		return result;
	}

	/**
	 * 
	 * @param directory
	 * @param subscriberId
	 * @return
	 */
	private static String getFullPath(String directory, Long subscriberId, String filename) {
		StringBuilder sb = new StringBuilder();
		sb.append(directory);
		sb.append(File.separator);
		sb.append(subscriberId);
		if (filename != null) {
			sb.append(File.separator);
			sb.append(filename);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isMD5File(String filename) {
		checkNotNull(filename);
		return MD5.equals(FilenameUtils.getExtension(filename));
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isCsvFile(String filename) {
		checkNotNull(filename);
		return CSV.equals(FilenameUtils.getExtension(filename));
	}

}
