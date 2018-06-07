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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrContGroupControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContGroupControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContGroupService contGroupService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private SubscrContGroupController subscrContGroupController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContGroupController = new SubscrContGroupController(contGroupService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContGroupController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContGroupObjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contGroup/0/contObject/available").testGet();
//		_testGetJson("/api/subscr/contGroup/0/contObject/available");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContGroup() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contGroup").testGet();
//		_testGetJson("/api/subscr/contGroup");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateContGroup() throws Exception {

		SubscrContGroup group = new SubscrContGroup();
		group.setContGroupName("Новая группа");

		long[] objectIds = { 18811504L, 18811505L };

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("contObjectIds", TestUtils.arrayToString(objectIds));
		};

		Long id = mockMvcRestWrapper.restRequest("/api/subscr/contGroup")
            .requestBuilder(params)
            .testPost(group).getLastId();
//            _testCreateJson("/api/subscr/contGroup", group, params);

        mockMvcRestWrapper.restRequest("/api/subscr/contGroup/{id}", id).testDelete();

//		testDeleteContGroup(id);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateContGroup() throws Exception {
		List<SubscrContGroup> contGroups = contGroupService
				.selectSubscriberGroups(currentSubscriberService.getSubscriberParam());

		assertTrue(contGroups.size() > 0);
		SubscrContGroup contGroup;
		if (contGroups.size() > 2) {
			contGroup = contGroups.get(1);
		} else {
			contGroup = contGroups.get(0);
		}

		long[] objectIds = { 18811522L, 18811533L };

		contGroup.setContGroupComment("TEST AutoUpdate " + System.currentTimeMillis());
		String urlStr = "/api/subscr/contGroup/" + contGroup.getId();

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("contObjectIds", TestUtils.arrayToString(objectIds));
		};

        mockMvcRestWrapper.restRequest(urlStr)
            .requestBuilder(params)
            .testPut(contGroup);
//		_testUpdateJson(urlStr, cg, params);
	}
//
//	/**
//	 *
//	 * @param contGroupId
//	 * @throws Exception
//	 */
//
//	public void testDeleteContGroup(Long contGroupId) throws Exception {
//		_testDeleteJson("/api/subscr/contGroup/" + contGroupId);
//	}
//
}
