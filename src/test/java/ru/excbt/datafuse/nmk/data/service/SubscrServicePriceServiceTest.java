package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;

public class SubscrServicePriceServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServicePriceServiceTest.class);

	@Autowired
	private SubscrServicePriceService subscrServicePriceService;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPackPrice() throws Exception {
		List<SubscrServiceItem> subscrServiceItems = subscrServiceItemService.selectServiceItemList();
		assertTrue(subscrServiceItems.size() > 0);

		subscrServiceItems.forEach((i) -> {
			List<SubscrServicePrice> prices = subscrServicePriceService.selectItemPrice(i.getId());
			assertTrue(prices.size() > 0);
			prices.forEach((p) -> {
				logger.debug("ItemName:{} PriceValue:{}", i.getItemName(), p.getPriceValue());
				assertNotNull(p.getPriceValue());
			});

			prices = subscrServicePriceService.selectItemPriceByDate(i.getId(), LocalDate.now());
			assertTrue(prices.size() > 0);
			prices.forEach((p) -> {
				logger.debug("ItemName:{} PriceValueByDate:{}", i.getItemName(), p.getPriceValue());
				assertNotNull(p.getPriceValue());
			});
		});

	}

}
