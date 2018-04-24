package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrServicePriceServiceTest extends PortalDataTest {

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

		subscrServiceItems.forEach(i -> {
			List<SubscrServicePrice> prices = subscrServicePriceService.selectItemPrice(i.getId());
			if (prices.size() == 0) {
				logger.warn("Prices for item (id={}) {} is not set", i.getId(), i.getKeyname());
			}

			prices.forEach((p) -> {
				logger.debug("ItemName:{} PriceValue:{}", i.getItemName(), p.getPriceValue());
				assertNotNull(p.getPriceValue());
			});

			prices = subscrServicePriceService.selectItemPriceByDate(i.getId(), LocalDate.now());
			if (prices.size() == 0) {
				logger.warn("Current Prices for item (id={}) {} is not set", i.getId(), i.getKeyname());
			}

			prices.forEach((p) -> {
				logger.debug("ItemName:{} PriceValueByDate:{}", i.getItemName(), p.getPriceValue());
				assertNotNull(p.getPriceValue());
			});
		});

	}

}
