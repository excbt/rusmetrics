/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterImport_CsvFormat;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public class ContServiceDataHWaterImportTest {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterImportTest.class);

	private String CSV_PROPS_HEADER = "date,detail_type,work_time,failTime,h_delta,h_in,h_out,m_delta,m_in,m_out,p_delta,p_in,p_out,t_cold,t_in,t_out,t_outdoor,v_delta,v_in,v_out";

	@Test
	public void testCSV() throws Exception {
		ContServiceDataHWaterImport data = new ContServiceDataHWaterImport();

		CsvMapper mapper = new CsvMapper();

		mapper.addMixIn(ContServiceDataHWaterImport.class, ContServiceDataHWaterImport_CsvFormat.class);

		mapper.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterImport.class).withHeader();

		String csv = mapper.writer(schema).writeValueAsString(Arrays.asList(data));

		String[] lines = csv.split("\\r?\\n");

		logger.info("Actial CSV: {}", csv);
		assertTrue(lines.length > 0);
		logger.info("Actial CSV props: {}", lines[0]);

		assertEquals(CSV_PROPS_HEADER, lines[0]);

	}

}
