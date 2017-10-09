package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;

/**
 * Класс для работы с каталогами CSV
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.07.2015
 *
 */
public interface HWatersCsvProps {

	public final static String HWATERS_CSV_IN = "app.hwaters.csv.in";
	public final static String HWATERS_CSV_OUT = "app.hwaters.csv.out";

	public final static String CSV_EXT = ".csv";
	public final static String MD5_EXT = ".md5";

	public String getHWatersCsvOutputDir();

	public String getHWatersCsvInputDir();

	/**
	 *
	 * @param subscriberId
	 * @param subscrUserId
	 * @return
	 */
	public default String getSubscriberCsvFilename(Long subscriberId, Long subscrUserId) {

		checkNotNull(subscriberId);

		DateTime fileDateTime = DateTime.now();

		StringBuilder sb = new StringBuilder();
		// directory
		sb.append(File.separator);
		sb.append(subscriberId);

		// Filename
		sb.append(File.separator);
		sb.append("s");
		sb.append(subscriberId);
		sb.append("_u");
		sb.append(subscrUserId != null ? subscrUserId : "xxx");
		sb.append("_");
		sb.append(fileDateTime.toString("yyyy-MM-dd_HHmmssZ"));
		sb.append("_r");
		sb.append(RandomStringUtils.randomNumeric(5));
		sb.append(CSV_EXT);
		return sb.toString();
	}

	/**
	 *
	 * @param subscriberId
	 * @param subscrUserId
	 * @param filename
	 * @return
	 */
	public default String getSubscriberCsvPath(Long subscriberId, Long subscrUserId, String filename) {
		DateTime fileDateTime = DateTime.now();
		return new StringBuilder().append(getHWatersCsvInputDir()).append(File.separator).append("s_")
				.append(subscriberId).append(File.separator).append("u_").append(subscrUserId).append(File.separator)
				.append(FilenameUtils.getBaseName(filename)).append("_").append(fileDateTime.toString("yyyyMMddHHmmss"))
				.append('.').append(FilenameUtils.getExtension(filename)).toString();
	}

}
