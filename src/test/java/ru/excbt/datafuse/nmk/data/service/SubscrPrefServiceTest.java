package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;

public class SubscrPrefServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrPrefService subscrPrefService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefByType() throws Exception {
		List<SubscrPref> subscrPrefList = subscrPrefService.selectSubscrPrefsBySubscrType("RMA");
		assertNotNull(subscrPrefList);
		assertTrue(subscrPrefList.size() > 0);
	}

}
