package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceListService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class RmaPriceListControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @MockBean
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrPriceListService subscrPriceListService;

    @Autowired
    private SubscrPriceItemService subscrPriceItemService;

    @Autowired
    private RmaPriceListController rmaPriceListController;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaPriceListController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


    /*

     */
	@Test
    @Transactional
	public void testSubscribers() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/priceList/subscribers").testGet();
	}

    /*
        TODO access denied. User is not SYSTEM
     */
    @Ignore
    @Test
    @Transactional
	public void testRmaList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/priceList/rma").testGet();
	}

    /*

     */
    @Test
    @Transactional
	public void testRmaPriceList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID).testGet();
	}

	/*

	 */
	@Test
    @Transactional
	public void testMasterPriceList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList", 0).testGet();
	}

	/*

	 */
	@Test
    @Transactional
	public void testSubscrPriceList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList", TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID).testGet();
	}

    /*

     */
    @Ignore
	@Test
    @Transactional
	public void testMakeDraft() throws Exception {
		List<SubscrPriceList> priceList = subscrPriceListService.selectActiveRmaPriceList(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
				null);
		assertTrue(priceList.size() > 0);
		RequestExtraInitializer param = (builder) -> {
			builder.param("srcPriceListId", priceList.get(0).getId().toString());
		};
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID)
            .requestBuilder(b -> b.param("srcPriceListId", priceList.get(0).getId().toString()))
            .testPostEmpty();
//		_testPostJson(String.format("/api/rma/%d/priceList", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID), param);
	}

    /*

     */
	@Test
    @Transactional
	public void testUpdate() throws Exception {
		List<SubscrPriceList> priceLists = subscrPriceListService.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID,
				null);
		assertNotNull(priceLists);
		assertTrue(priceLists.size() > 0);
		SubscrPriceList priceList = priceLists.get(0);
		priceList.setPriceListName(priceList.getPriceListName() + "(mod)");
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList/{id2}", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceList.getId()).testPut(priceList);
//		_testUpdateJson(String.format("/api/rma/%d/priceList/%d", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceList.getId()),
//				priceList);
	}

    /*

     */
	@Test
    @Transactional
	public void testCreateSubscrPriceLists() throws Exception {

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService
				.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);

		if (subscrPriceLists.size() == 0) {
			subscrPriceLists = subscrPriceListService.selectDraftRmaPriceLists(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, null);
		}

		assertTrue(subscrPriceLists.size() > 0);

		Long priceListId = subscrPriceLists.get(0).getId();

		RequestExtraInitializer params = (builder) -> {
			builder.param("subscriberIds", TestUtils.arrayToString(new long[] { TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID }));
			// requestBuilder.param("activeIds", arrayToString(new long[] {}));
		};
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList/{id2}/subscr", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId)
            .requestBuilder(b -> b.param("subscriberIds", TestUtils.arrayToString(new long[] { TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID }))).testPostEmpty();
//		_testPostJson(String.format("/api/rma/%d/priceList/%d/subscr", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId), params);
	}

    /*

     */
	@Test
	@Ignore
    @Transactional
	public void testSetActiveSubscrPriceList() throws Exception {

		//Long priceListId = getAnyRmaPriceList(EXCBT_RMA_SUBSCRIBER_ID).getId();
        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList/{id2}/activate", 67628679L, 85609507L).testPutEmpty();
//		_testPutJson(String.format("/api/rma/%d/priceList/%d/activate", 67628679L, 85609507L));
	}

    /*

     */
    @Test
    @Transactional
	public void testGetRmaPriceItemsVo() throws Exception {
		Long priceListId = getAnyRmaPriceList(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID).getId();

        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList/{id2}/items", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId).testGet();
//		_testGetJson(String.format("/api/rma/%d/priceList/%d/items", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId));
	}

    /*

     */
	@Test
	@Ignore
    @Transactional
	public void testUpdateRmaPriceItemVOs() throws Exception {

		Long priceListId = getAnyRmaPriceList(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID).getId();

		List<SubscrPriceItemVO> priceItemsVO = subscrPriceItemService.findPriceItemVOs(priceListId);

		if (priceItemsVO.size() == 0) {
			return;
		}

		SubscrPriceItemVO oldVO = priceItemsVO.get(0);

		oldVO.setPriceValue(
				oldVO.getPriceValue() != null ? oldVO.getPriceValue() * 0.9D : null);
		// oldVO.setValue(null);

        mockMvcRestWrapper.restRequest("/api/rma/{id1}/priceList/{id2}/items", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId).testPut(priceItemsVO);
//		_testUpdateJson(String.format("/api/rma/%d/priceList/%d/items", TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, priceListId),
//				priceItemsVO);
	}

	/*

	 */
	private SubscrPriceList getAnyRmaPriceList(Long rmaSubscriberId) {
		List<SubscrPriceList> preResult = subscrPriceListService.selectRmaPriceLists(rmaSubscriberId);
		assertTrue(preResult.size() > 0);
		return preResult.get(0);
	}

}
