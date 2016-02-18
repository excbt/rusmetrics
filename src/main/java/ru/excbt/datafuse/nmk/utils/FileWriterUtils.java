package ru.excbt.datafuse.nmk.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилиты для записи файла
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.07.2015
 *
 */
public class FileWriterUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileWriterUtils.class);

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFilename
	 * @param writeMD5
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream, String outputFilename, boolean writeMD5)
			throws IOException {

		File outputFile = new File(outputFilename);

		return writeFile(uploadedInputStream, outputFile, writeMD5);
	}

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFile
	 * @param writeMD5
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream, File outputFile, boolean writeMD5)
			throws IOException {

		checkNotNull(outputFile);
		File dir = outputFile.getParentFile();

		if (!dir.exists()) {
			logger.info("Making dir: {}", dir.getAbsolutePath());
			dir.mkdirs();
		}

		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			IOUtils.copy(uploadedInputStream, fos);
		}

		String digestMD5 = null;
		try (FileInputStream fis = new FileInputStream(outputFile)) {
			digestMD5 = DigestUtils.md5Hex(fis);
		}

		if (writeMD5) {
			String md5File = FilenameUtils.removeExtension(outputFile.getAbsolutePath()) + ".md5";

			try (PrintStream out = new PrintStream(new FileOutputStream(md5File))) {
				out.print(digestMD5);
			}
		}

		logger.info("Writed file: {}. Size:{} . MD5: {}", outputFile.getAbsolutePath(), outputFile.length(), digestMD5);

		return digestMD5;
	}

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFilename
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream, String outputFilename) throws IOException {
		return writeFile(uploadedInputStream, outputFilename, true);
	}

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFile
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream, File outputFile) throws IOException {
		return writeFile(uploadedInputStream, outputFile, true);
	}

}
