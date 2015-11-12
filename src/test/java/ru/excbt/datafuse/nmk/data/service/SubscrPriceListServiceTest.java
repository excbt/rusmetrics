package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrPriceListServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceListServiceTest.class);

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@JsonIgnore
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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@JsonIgnore
	public void testCreateRmaDraft() throws Exception {
		SubscrPriceList srcPriceList = subscrPriceListService.findRmaActivePriceList(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(srcPriceList);
		logger.info("Found scrPriceList (id={}): {}. master:{}", srcPriceList.getId(), srcPriceList.getPriceListName(),
				srcPriceList.getIsMaster());

		SubscrPriceList draft1 = subscrPriceListService.makeSubscrPriceListDraft(srcPriceList.getId());
		assertNotNull(draft1);
		SubscrPriceList draft2 = subscrPriceListService.makeSubscrPriceListDraft(draft1.getId());
		assertNotNull(draft2);
	}

}
