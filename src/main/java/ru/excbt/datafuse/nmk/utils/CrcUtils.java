package ru.excbt.datafuse.nmk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrcUtils {

	private static final Logger log = LoggerFactory.getLogger(CrcUtils.class);

	private CrcUtils() {

	}

	public static long calcCrcValue(Object[] objects) {

		byte[] bytes = null;
		long result = 0;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos)) {

			for (int idx = 0; idx < objects.length; idx++) {
				Object obj = objects[idx];
				try {
					if (obj != null) {
						out.writeObject(obj);
					}
				} catch (IOException e) {
					log.error("ObjectOutputStream error when writeObject: {}. index: {}",
							obj, idx);
				}

			}

			bytes = bos.toByteArray();

		} catch (IOException e1) {
			log.error("ObjectOutputStream error when writeObject.");
		}

		if (bytes != null) {
			Checksum checksum = new CRC32();
			checksum.update(bytes, 0, bytes.length);
			result = checksum.getValue();
		}
		return result;

	}

}
