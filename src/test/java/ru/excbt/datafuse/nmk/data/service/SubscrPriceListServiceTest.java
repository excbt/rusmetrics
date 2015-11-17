package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Ignore
	public void testCreateRmaPriceList() throws Exception {
		SubscrPriceList nmcPriceList = subscrPriceListService.findRootPriceLists("TEST 1");
		assertNotNull(nmcPriceList);

		subscrPriceListService.deleteActivePriceList(EXCBT_RMA_SUBSCRIBER_ID);
		SubscrPriceList rmaSubscriberPriceList = subscrPriceListService.createRmaPriceList(nmcPriceList.getId(), true,
				EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(rmaSubscriberPriceList);

		subscrPriceListService.deleteActivePriceList(EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);
		SubscrPriceList rmaSpecialSubscriberPriceList = subscrPriceListService.createRmaPriceList(nmcPriceList.getId(),
				true, EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);
		assertNotNull(rmaSpecialSubscriberPriceList);

		List<SubscrPriceList> rmaPrices = subscrPriceListService.findRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(rmaPrices.size() > 0);

		// subscrPriceListService.deleteSubscrPriceList(rmaSubscriberPriceList);
		// ServicePriceList
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCreateRmaDraft() throws Exception {
		SubscrPriceList srcPriceList = subscrPriceListService.findActiveRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(srcPriceList);
		logger.info("Found scrPriceList (id={}): {}. master:{}", srcPriceList.getId(), srcPriceList.getPriceListName(),
				srcPriceList.getIsMaster());

		SubscrPriceList draft1 = subscrPriceListService.createDraftPriceList(srcPriceList.getId());
		assertNotNull(draft1);
		SubscrPriceList draft2 = subscrPriceListService.createDraftPriceList(draft1.getId());
		assertNotNull(draft2);
	}

	@Test
	@Ignore
	public void testCreateRmaDraftPriceList() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.findRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);

		assertTrue(rmaPriceLists.size() > 0);
		SubscrPriceList priceList = rmaPriceLists.get(0);
		SubscrPriceList newPriceList = subscrPriceListService.createDraftPriceList(priceList.getId());
		assertNotNull(newPriceList);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCreateSubscriberPriceList() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);
		if (rmaPriceLists.size() == 0) {
			rmaPriceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		}

		assertTrue(rmaPriceLists.size() > 0);
		SubscrPriceList srcPriceList = rmaPriceLists.get(0);
		subscrPriceListService.createSubscrPriceList(srcPriceList.getId(), EXCBT_SUBSCRIBER_ID, false);

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService
				.findSubscriberPriceLists(EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);
		logger.info("Size of price lists : {}", subscrPriceLists.size());

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testFindRmaSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.findRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testFindSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.findRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testFindActiveRmaPriceList() throws Exception {
		SubscrPriceList rmaPriceList = subscrPriceListService.findActiveRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(rmaPriceList);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testFindRmaDraftSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testSetInactiveSubscrPriceList() throws Exception {
		int setInactiveResult = subscrPriceListService.setInctiveSubscrPriceList(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);
		logger.info("Inactive Result: {}", setInactiveResult);
		// assertTrue(setInactiveResult == 1);
	}

	@Test
	@Ignore
	public void testSetActivePriceList() throws Exception {
		subscrPriceListService.setActiveSubscrPriceList(EXCBT_RMA_SUBSCRIBER_ID, 70318507L);
	}

}
