package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeActionService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.SubscrControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrContEventTypeActionControllerTest extends PortalApiTest {


	@Autowired
	private SubscrContEventTypeActionService subscrContEventTypeActionService;

	@Autowired
	private SubscrActionUserService subscrActionUserService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrContEventTypeActionController subscrContEventTypeActionController;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContEventTypeActionController = new SubscrContEventTypeActionController(subscrContEventTypeActionService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContEventTypeActionController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	public void testAvailableContEventTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contEventType/actions/available").testGet();
//        _testGetJson(UrlUtils.apiSubscrUrl("/contEventType/actions/available"));
	}

    /**
     * No Aciton users
     * @throws Exception
     */
	@Test
    @Ignore
	public void testUpdateEventTypeAction() throws Exception {
		List<ContEventType> availList = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availList.size() > 0);
		Long contEventTypeId = availList.get(0).getId();

		List<SubscrActionUser> actionUsers = subscrActionUserService.findAll(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);

		assertTrue(actionUsers.size() > 1);

		SubscrContEventTypeAction newUserAction = new SubscrContEventTypeAction();
		newUserAction.setIsEmail(false);
		newUserAction.setIsSms(true);
		newUserAction.setSubscrActionUserId(actionUsers.get(0).getId());

		SubscrContEventTypeAction newUserAction2 = new SubscrContEventTypeAction();
		newUserAction2.setIsEmail(true);
		newUserAction2.setIsSms(true);
		newUserAction2.setSubscrActionUserId(actionUsers.get(2).getId());

//		String url = String.format("/api/subscr/contEventType/%d/actions", contEventTypeId);

        mockMvcRestWrapper.restRequest("/api/subscr/contEventType/{id}/actions", contEventTypeId).testPut(newUserAction2);
//		_testUpdateJson(url, Arrays.asList(newUserAction, newUserAction2));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContEventTypeAction() throws Exception {
		List<ContEventType> availList = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availList.size() > 0);
		Long contEventTypeId = availList.get(0).getId();

		String url = String.format("/api/subscr/contEventType/%d/actions", contEventTypeId);

        mockMvcRestWrapper.restRequest("/api/subscr/contEventType/{id}/actions", contEventTypeId).testGet();
//		_testGetJson(url);
	}

}
