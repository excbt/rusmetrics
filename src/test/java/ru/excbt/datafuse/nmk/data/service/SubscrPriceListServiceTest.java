package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrPriceListServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceListServiceTest.class);

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCreateRmaPriceList() throws Exception {
		SubscrPriceList nmcPriceList = subscrPriceListService.selectRootPriceLists("DEFAULT");
		assertNotNull(nmcPriceList);

		SubscrPriceList rmaSubscriberPriceList = subscrPriceListService.createRmaPriceList(nmcPriceList.getId(),
				subscriberService.selectSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID));
		assertNotNull(rmaSubscriberPriceList);

		SubscrPriceList rmaSpecialSubscriberPriceList = subscrPriceListService.createRmaPriceList(nmcPriceList.getId(),
				subscriberService.selectSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID));
		assertNotNull(rmaSpecialSubscriberPriceList);

		List<SubscrPriceList> rmaPrices = subscrPriceListService.selectRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
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
		List<SubscrPriceList> srcPriceList = subscrPriceListService.selectActiveRmaPriceList(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
				null);
		assertTrue(srcPriceList.size() > 0);
		logger.info("Found scrPriceList (id={}): {}. master:{}", srcPriceList.get(0).getId(),
				srcPriceList.get(0).getPriceListName(), srcPriceList.get(0).getIsMaster());

		SubscrPriceList draft1 = subscrPriceListService.createAnyDraftPriceList(srcPriceList.get(0).getId());
		assertNotNull(draft1);
		SubscrPriceList draft2 = subscrPriceListService.createAnyDraftPriceList(draft1.getId());
		assertNotNull(draft2);
	}

	@Test
	@Ignore
	public void testCreateRmaDraftPriceList() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.selectRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
            TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);

		assertTrue(rmaPriceLists.size() > 0);
		SubscrPriceList priceList = rmaPriceLists.get(0);
		SubscrPriceList newPriceList = subscrPriceListService.createAnyDraftPriceList(priceList.getId());
		assertNotNull(newPriceList);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCreateSubscriberPriceList() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
            TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
		if (rmaPriceLists.size() == 0) {
			rmaPriceLists = subscrPriceListService.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, null);
		}

		assertTrue(rmaPriceLists.size() > 0);
		SubscrPriceList srcPriceList = rmaPriceLists.get(0);
		subscrPriceListService.createSubscrPriceList(srcPriceList.getId(),
				subscriberService.selectSubscriber(TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID));

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService
				.selectSubscriberPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
		logger.info("Size of price lists : {}", subscrPriceLists.size());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testFindRmaSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.selectRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
            TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testFindSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.selectRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testFindActiveRmaPriceList() throws Exception {
		List<SubscrPriceList> rmaPriceList = subscrPriceListService.selectActiveRmaPriceList(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
				null);
		//assertTrue(rmaPriceList.size() > 0);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testFindRmaDraftSubscrPriceLists() throws Exception {
		List<SubscrPriceList> rmaPriceLists = subscrPriceListService.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
            TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
		assertTrue(rmaPriceLists.size() > 0);
		logger.info("Result count: {}", rmaPriceLists.size());
	}

	@Test
	public void testSetInactiveSubscrPriceList() throws Exception {
		int setInactiveResult = subscrPriceListService.deactiveOtherSubscrPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
            TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
		logger.info("Inactive Result: {}", setInactiveResult);
		// assertTrue(setInactiveResult == 1);
	}

	@Test
	@Ignore
	public void testSetActivePriceList() throws Exception {
		subscrPriceListService.activateSubscrPriceList(70318507L, LocalDate.now());
	}

	@Test
	public void testSelectActiveBySubscriber() throws Exception {
		int count = subscrPriceListService.selectSubscrActiveCount(67628679L);
		logger.info("Active: {}", count);
	}

}
