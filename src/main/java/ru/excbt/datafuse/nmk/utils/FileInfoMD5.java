package ru.excbt.datafuse.nmk.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

public class FileInfoMD5 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1795815285696961218L;

	public static final String MD5_EXT = ".md5";
	public static final String MD5 = "md5";

	public static final String CSV_EXT = ".csv";
	public static final String CSV = "csv";

	private final String filename;
	private final String md5file;

	public FileInfoMD5(String filename) {
		checkNotNull(filename);
		this.filename = filename;
		this.md5file = FilenameUtils.removeExtension(filename) + MD5_EXT;
	}

	public String getFilename() {
		return filename;
	}

	public String getMd5file() {
		return md5file;
	}

	public static boolean isMD5File(String filename) {
		checkNotNull(filename);
		return MD5.equals(FilenameUtils.getExtension(filename));
	}

	public static boolean isCsvFile(String filename) {
		checkNotNull(filename);
		return CSV.equals(FilenameUtils.getExtension(filename));
	}
}
