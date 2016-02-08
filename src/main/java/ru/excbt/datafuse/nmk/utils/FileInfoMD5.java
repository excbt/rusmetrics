package ru.excbt.datafuse.nmk.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

/**
 * Работа с файлом MD5
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 21.07.2015
 *
 */
public class FileInfoMD5 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1795815285696961218L;

	public static final String MD5_EXT = ".md5";
	public static final String MD5 = "md5";

	private final String filename;
	private final String md5file;

	public FileInfoMD5(String filename) {
		checkNotNull(filename);
		this.filename = filename;
		this.md5file = FilenameUtils.removeExtension(filename) + MD5_EXT;
	}

	public FileInfoMD5(File file) {
		checkNotNull(file);
		this.filename = file.getName();
		this.md5file = FilenameUtils.removeExtension(file.getName()) + MD5_EXT;
	}

	public String getFilename() {
		return filename;
	}

	public String getMd5file() {
		return md5file;
	}

}
