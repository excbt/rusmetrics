package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrPriceListServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	@Test
	public void testName() throws Exception {
		SubscrPriceList nmcPriceList = subscrPriceListService.findRootPriceLists("TEST 1");
		assertNotNull(nmcPriceList);

		subscrPriceListService.deleteActivePriceList(EXCBT_RMA_SUBSCRIBER_ID);
		SubscrPriceList rmaSubscriberPriceList = subscrPriceListService.makeRmaPriceList(nmcPriceList.getId(), true,
				EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(rmaSubscriberPriceList);

		subscrPriceListService.deleteActivePriceList(EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);
		SubscrPriceList rmaSpecialSubscriberPriceList = subscrPriceListService.makeRmaPriceList(nmcPriceList.getId(),
				true, EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);
		assertNotNull(rmaSpecialSubscriberPriceList);

		List<SubscrPriceList> rmaPrices = subscrPriceListService.findRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(rmaPrices.size() > 0);

		// subscrPriceListService.deleteSubcrPriceList(rmaSubscriberPriceList);
		// ServicePriceList
	}
}
