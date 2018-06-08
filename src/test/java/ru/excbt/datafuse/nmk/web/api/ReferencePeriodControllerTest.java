package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class ReferencePeriodControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(ReferencePeriodControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalContObjectMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private ReferencePeriodController referencePeriodController;

	@Autowired
	private ReferencePeriodService referencePeriodService;

    @Autowired
    private CurrentSubscriberService currentSubscriberService;

    @Autowired
    private ContZPointService contZPointService;

    @Autowired
    private ObjectAccessService objectAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        referencePeriodController = new ReferencePeriodController(referencePeriodService, contZPointService, objectAccessService, portalUserIdsService);

	    this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(referencePeriodController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

	    mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
	}


	private Long getOId() {
		List<Long> vList = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());
		assertTrue(vList.size() > 0);
		return vList.get(0);
	}

	/*

	 */
	private Long getZPointId(Long oId) {
		List<Long> vList2 = contZPointService.selectContZPointIds(oId);
		assertTrue(vList2.size() > 0);
		return vList2.get(0);
	}

	/*

	 */
	@Test
    @Transactional
	public void testGetLast() throws Exception {

		Long oId = getOId();
		Long zpId = getZPointId(oId);
		mockMvcRestWrapper.restRequest("/api/subscr/contObjects/{id1}/zpoints/{id2}/referencePeriod", oId, zpId).testGet();
	}

	/*

	 */
	@Test
    @Transactional
	public void testCreateUpdateDelete() throws Exception {
		Long oId = getOId();
		Long zpId = getZPointId(oId);
		String urlStr = String.format("/api/subscr/contObjects/%d/zpoints/%d/referencePeriod", oId, zpId);

		// Create testing
		ReferencePeriod referencePeriod = new ReferencePeriod();
		referencePeriod.setBeginDate(new Date());
		referencePeriod.setPeriodDescription("Testing ReferencePeriod");
		referencePeriod.setTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname());

        Long createdId = mockMvcRestWrapper.restRequest("/api/subscr/contObjects/{id1}/zpoints/{id2}/referencePeriod", oId, zpId)
            .testPost(referencePeriod).getLastId();

		// Update testing
		referencePeriod.setId(Long.valueOf(createdId));

		referencePeriod.setPeriodDescription("Testing Update");

        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/{id1}/zpoints/{id2}/referencePeriod/{id3}",
            oId, zpId, createdId)
            .testPut(referencePeriod)
            .and()
            .restRequest("/api/subscr/contObjects/{id1}/zpoints/{id2}/referencePeriod/{id3}",
                oId, zpId, createdId)
            .testDelete();

	}
}
