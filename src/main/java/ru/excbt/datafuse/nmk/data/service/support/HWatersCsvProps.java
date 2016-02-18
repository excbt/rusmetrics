package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

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

}
