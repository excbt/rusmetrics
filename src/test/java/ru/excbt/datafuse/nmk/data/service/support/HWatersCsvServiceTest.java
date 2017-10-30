/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.excbt.datafuse.nmk.service.utils.CsvUtil;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.01.2017
 *
 */
public class HWatersCsvServiceTest {


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCsvSeparatorFull() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("0,1,2,3,4,5,6,7");
		sb.append(System.lineSeparator());
		sb.append("0,1,2,3,4,5,6,7");
		boolean result = CsvUtil.checkByteCsvSeparators(sb.toString().getBytes());
		assertTrue(result);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCsvSeparatorEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("0,1,2,3,4,5,6,7");
		sb.append(System.lineSeparator());
		sb.append(",,,,,,,");
		boolean result = CsvUtil.checkByteCsvSeparators(sb.toString().getBytes());
		assertTrue(result);
	}
}
