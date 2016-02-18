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
		_testGetJson("/api/rma/priceList/subscribers");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRmaList() throws Exception {
		_testGetJson("/api/rma/priceList/rma");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRmaPriceList() throws Exception {
		_testGetJson(String.format("/api/rma/%d/priceList", EXCBT_RMA_SUBSCRIBER_ID));
	}

	@Test
	public void testMasterPriceList() throws Exception {
		_testGetJson(String.format("/api/rma/%d/priceList", 0));
	}

	@Test
	public void testSubscrPriceList() throws Exception {
		_testGetJson(String.format("/api/rma/%d/priceList", EXCBT_SUBSCRIBER_ID));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMakeDraft() throws Exception {
		List<SubscrPriceList> priceList = subscrPriceListService.selectActiveRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID,
				null);
		assertTrue(priceList.size() > 0);
		RequestExtraInitializer param = (builder) -> {
			builder.param("srcPriceListId", priceList.get(0).getId().toString());
		};
		_testPostJson(String.format("/api/rma/%d/priceList", EXCBT_RMA_SUBSCRIBER_ID), param);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		List<SubscrPriceList> priceLists = subscrPriceListService.selectDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID,
				null);
		assertNotNull(priceLists);
		assertTrue(priceLists.size() > 0);
		SubscrPriceList priceList = priceLists.get(0);
		priceList.setPriceListName(priceList.getPriceListName() + "(mod)");
		_testUpdateJson(String.format("/api/rma/%d/priceList/%d", EXCBT_RMA_SUBSCRIBER_ID, priceList.getId()),
				priceList);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateSubscrPriceLists() throws Exception {

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService
				.selectDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID, EXCBT_SUBSCRIBER_ID);

		if (subscrPriceLists.size() == 0) {
			subscrPriceLists = subscrPriceListService.selectDraftRmaPriceLists(EXCBT_RMA_SUBSCRIBER_ID, null);
		}

		assertTrue(subscrPriceLists.size() > 0);

		Long priceListId = subscrPriceLists.get(0).getId();

		RequestExtraInitializer params = (builder) -> {
			builder.param("subscriberIds", arrayToString(new long[] { EXCBT_SUBSCRIBER_ID }));
			// builder.param("activeIds", arrayToString(new long[] {}));
		};
		_testPostJson(String.format("/api/rma/%d/priceList/%d/subscr", EXCBT_RMA_SUBSCRIBER_ID, priceListId), params);
	}

	@Test
	@Ignore
	public void testSetActiveSubscrPriceList() throws Exception {

		//Long priceListId = getAnyRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID).getId();
		_testPutJson(String.format("/api/rma/%d/priceList/%d/activate", 67628679L, 85609507L));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRmaPriceItemsVo() throws Exception {
		Long priceListId = getAnyRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID).getId();

		_testGetJson(String.format("/api/rma/%d/priceList/%d/items", EXCBT_RMA_SUBSCRIBER_ID, priceListId));
	}

	@Test
	@Ignore
	public void testUpdateRmaPriceItemVOs() throws Exception {

		Long priceListId = getAnyRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID).getId();

		List<SubscrPriceItemVO> priceItemsVO = subscrPriceItemService.findPriceItemVOs(priceListId);

		if (priceItemsVO.size() == 0) {
			return;
		}

		SubscrPriceItemVO oldVO = priceItemsVO.get(0);

		oldVO.setPriceValue(
				oldVO.getPriceValue() != null ? oldVO.getPriceValue().multiply(BigDecimal.valueOf(0.9)) : null);
		// oldVO.setValue(null);

		_testUpdateJson(String.format("/api/rma/%d/priceList/%d/items", EXCBT_RMA_SUBSCRIBER_ID, priceListId),
				priceItemsVO);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	private SubscrPriceList getAnyRmaPriceList(Long rmaSubscriberId) {
		List<SubscrPriceList> preResult = subscrPriceListService.selectRmaPriceLists(rmaSubscriberId);
		assertTrue(preResult.size() > 0);
		return preResult.get(0);
	}

}
