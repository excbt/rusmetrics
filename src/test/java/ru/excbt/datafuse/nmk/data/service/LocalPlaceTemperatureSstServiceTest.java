package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class LocalPlaceTemperatureSstServiceTest extends JpaSupportTest {

	@Autowired
	private LocalPlaceTemperatureSstService localPlaceTemperatureSstService;

	/**
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testInitSst() throws Exception {
		Long localPlaceId = 490041190L;
		localPlaceTemperatureSstService.initMonth(localPlaceId, LocalDate.now());
	}

}
