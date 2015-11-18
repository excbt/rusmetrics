package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceListService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class RmaPriceListControllerTest extends AnyControllerTest implements TestExcbtRmaIds {

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	@Autowired
	private SubscrPriceItemService subscrPriceItemService;

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
	public void testRmaList() throws Exception {
		_testJsonGet("/api/rma/priceList/rma");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRmaPriceList() throws Exception {
		_testJsonGet(String.format("/api/rma/%d/priceList", EXCBT_RMA_SUBSCRIBER_ID));
	}

	@Test
	public void testMasterPriceList() throws Exception {
		_testJsonGet(String.format("/api/rma/%d/priceList", 0));
	}

	@Test
	public void testSubscrPriceList() throws Exception {
		_testJsonGet(String.format("/api/rma/%d/priceList", EXCBT_SUBSCRIBER_ID));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMakeDraft() throws Exception {
		SubscrPriceList priceList = subscrPriceListService.findActiveRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID);
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
		List<SubscrPriceList> priceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		assertNotNull(priceLists);
		assertTrue(priceLists.size() > 0);
		SubscrPriceList priceList = priceLists.get(0);
		priceList.setPriceListName(priceList.getPriceListName() + "(mod)");
		_testJsonUpdate(String.format("/api/rma/%d/priceList/%d", EXCBT_RMA_SUBSCRIBER_ID, priceList.getId()),
				priceList);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateSubscrPriceLists() throws Exception {

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				EXCBT_SUBSCRIBER_ID);

		if (subscrPriceLists.size() == 0) {
			subscrPriceLists = subscrPriceListService.findDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID);
		}

		assertTrue(subscrPriceLists.size() > 0);

		Long priceListId = subscrPriceLists.get(0).getId();

		RequestExtraInitializer params = (builder) -> {
			builder.param("subscriberIds", arrayToString(new long[] { EXCBT_SUBSCRIBER_ID }));
			// builder.param("activeIds", arrayToString(new long[] {}));
		};
		_testJsonPost(String.format("/api/rma/%d/priceList/%d/subscr", EXCBT_RMA_SUBSCRIBER_ID, priceListId), params);
	}

	@Test
	@Ignore
	public void testSetActiveSubscrPriceList() throws Exception {
		_testJsonPut(String.format("/api/rma/%d/priceList/%d/activate", EXCBT_SUBSCRIBER_ID, 70318507L));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRmaPriceItemsVo() throws Exception {
		_testJsonGet(String.format("/api/rma/%d/priceList/%d/items", EXCBT_RMA_SUBSCRIBER_ID, 71270733L));
	}

	@Test
	public void testUpdateRmaPriceItemVOs() throws Exception {
		List<SubscrPriceItemVO> priceItemsVO = subscrPriceItemService.findPriceItemVOs(71270733L);

		SubscrPriceItemVO oldVO = priceItemsVO.get(0);

		oldVO.setValue(oldVO.getValue() != null ? oldVO.getValue().multiply(BigDecimal.valueOf(0.9)) : null);
		// oldVO.setValue(null);

		_testJsonUpdate(String.format("/api/rma/%d/priceList/%d/items", EXCBT_RMA_SUBSCRIBER_ID, 71270733L),
				priceItemsVO);
	}

}
