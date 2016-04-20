package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;

public class SubscrObjectTreeContObjectServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetContObjectIds() throws Exception {
		List<Long> contObjectIds = subscrObjectTreeContObjectService.selectRmaTreeContObjectIdAllLevels(64166466L, 512111663L);
		assertNotNull(contObjectIds);

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjectsExcludingIds(64166466L,
				contObjectIds);

		assertNotNull(contObjects);

	}
}
