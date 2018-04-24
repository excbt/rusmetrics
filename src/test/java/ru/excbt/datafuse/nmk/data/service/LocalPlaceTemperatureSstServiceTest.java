package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

@RunWith(SpringRunner.class)
public class LocalPlaceTemperatureSstServiceTest extends PortalDataTest {

	@Autowired
	private LocalPlaceTemperatureSstService localPlaceTemperatureSstService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testInitSst() throws Exception {
		Long localPlaceId = 490041190L;
		localPlaceTemperatureSstService.initMonth(localPlaceId, LocalDate.now());
	}

}
