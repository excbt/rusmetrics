package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceListService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaPriceListControllerTest extends RmaControllerTest {

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscribers() throws Exception {
		_testJsonGet("/api/rma/priceList/subscribers");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrPriceList() throws Exception {
		_testJsonGet(String.format("/api/rma/%d/priceList", EXCBT_RMA_SUBSCRIBER_ID));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMakeDraft() throws Exception {
		SubscrPriceList priceList = subscrPriceListService.findRmaActivePriceList(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(priceList);
		RequestExtraInitializer param = (builder) -> {
			builder.param("srcPriceListId", priceList.getId().toString());
		};
		_testJsonPost(String.format("/api/rma/%d/priceList", EXCBT_RMA_SUBSCRIBER_ID), param);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		List<SubscrPriceList> priceLists = subscrPriceListService.findDraftPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(priceLists);
		assertTrue(priceLists.size() > 0);
		SubscrPriceList priceList = priceLists.get(0);
		priceList.setPriceListName(priceList.getPriceListName() + "(mod)");
		_testJsonUpdate(String.format("/api/rma/%d/priceList/%d", EXCBT_RMA_SUBSCRIBER_ID, priceList.getId()),
				priceList);
	}
}
