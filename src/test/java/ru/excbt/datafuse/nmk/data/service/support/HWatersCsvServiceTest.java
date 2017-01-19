/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.01.2017
 * 
 */
public class HWatersCsvServiceTest extends JpaConfigTest {

	@Inject
	private HWatersCsvService service;

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
		boolean result = service.checkByteCsvSeparators(sb.toString().getBytes());
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
		boolean result = service.checkByteCsvSeparators(sb.toString().getBytes());
		assertTrue(result);
	}
}
