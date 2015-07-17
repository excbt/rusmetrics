package ru.excbt.datafuse.nmk.utils;

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

public class FileWriterUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(FileWriterUtils.class);

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFilename
	 * @param writeMD5
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream,
			String outputFilename, boolean writeMD5) throws IOException {

		File f = new File(outputFilename);
		File dir = f.getParentFile();

		if (!dir.exists()) {
			logger.info("Making dir: {}", dir.getAbsolutePath());
			dir.mkdirs();
		}

		try (FileOutputStream fos = new FileOutputStream(outputFilename)) {
			IOUtils.copy(uploadedInputStream, fos);
		}

		String digestMD5 = null;
		try (FileInputStream fis = new FileInputStream(f)) {
			digestMD5 = DigestUtils.md5Hex(fis);
		}

		if (writeMD5) {
			String md5File = FilenameUtils.removeExtension(outputFilename)
					+ ".md5";

			try (PrintStream out = new PrintStream(
					new FileOutputStream(md5File))) {
				out.print(digestMD5);
			}
		}

		logger.info("Writed file: {}. Size:{} . MD5: {}", outputFilename,
				f.length(), digestMD5);

		return digestMD5;
	}

	/**
	 * 
	 * @param uploadedInputStream
	 * @param outputFilename
	 * @return
	 * @throws IOException
	 */
	public static String writeFile(InputStream uploadedInputStream,
			String outputFilename) throws IOException {
		return writeFile(uploadedInputStream, outputFilename, true);
	}

}
