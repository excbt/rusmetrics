package ru.excbt.datafuse.nmk.utils;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.math.BigDecimal;

public class CrcUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrcUtilsTest.class);

	@Test
	public void tectCrc32() throws Exception {

		Object[] objects = new Object[9];

		objects[0] = Long.valueOf(1024);
		objects[1] = "String value";
		objects[2] = Integer.valueOf(1024);
		objects[3] = BigDecimal.TEN;
		objects[4] = Boolean.TRUE;
		objects[5] = new Date();
		objects[6] = UUID.randomUUID();
		objects[7] = Double.MAX_VALUE;
		objects[8] = null;

		long crc = CrcUtils.calcCrcValue(objects);

		LOGGER.info("CRC:{}", crc);
	}
}
