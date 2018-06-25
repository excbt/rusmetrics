package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
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
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SessionDetailTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrSessionTaskService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class RmaSubscrSessionTaskControllerTest extends PortalApiTest {


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private RmaSubscrSessionTaskController rmaSubscrSessionTaskController;
    @Autowired
    private SubscrSessionTaskService subscrSessionTaskService;
    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private SessionDetailTypeService sessionDetailTypeService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaSubscrSessionTaskController = new RmaSubscrSessionTaskController(subscrSessionTaskService, contZPointService, sessionDetailTypeService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscrSessionTaskController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }




	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateSubscrSessionTask() throws Exception {

		SubscrSessionTask task = new SubscrSessionTask();
		task.setDeviceObjectId(719L);
		task.setSessionDetailTypes(new String[] { TimeDetailKey.TYPE_1DAY.getKeyname() });
		//task.setContZpointId(contZpointId);
        Long id = mockMvcRestWrapper.restRequest("/api/rma/subscrSessionTask").testPost(task).getLastId();
//		Long id = _testCreateJson("/api/rma/subscrSessionTask", task);
		assertNotNull(id);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetSubscrSessionTask() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrSessionTask/{id1}", 127990560).testGet2xx();
//		_testGetJson();
	}

	@Test
	public void testGetSubscrSessionTaskSession() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrSessionTask/{id}/logSessions", 127990560).testGet();
//		_testGetJson("/api/rma/subscrSessionTask/" + 127990560 + "/logSessions");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetContZPointSessionDetailType() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrSessionTask/contZPointSessionDetailType/byDeviceObject/{id}",719).testGet();
//		_testGetJson("/api/rma/subscrSessionTask/contZPointSessionDetailType/byDeviceObject/" + 719);
	}

	@Test
	public void testGetSessionDetailTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrSessionTask/sessionDetailTypes/byDeviceObject/{id}",719).testGet();
//		_testGetJson("/api/rma/subscrSessionTask/sessionDetailTypes/byDeviceObject/" + 719);
	}

}
