package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
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
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeValidationService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.api.SubscrObjectTreeController.ObjectNameVM;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class SubscrObjectTreeControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrObjectTreeService subscrObjectTreeService;

    private SubscrObjectTreeController subscrObjectTreeController;
    @Autowired
    private SubscrObjectTreeValidationService subscrObjectTreeValidationService;
    @Autowired
    private SubscrObjectTreeContObjectService subscrObjecetTreeContObjectService;
    @Autowired
    private SubscrContEventNotificationService subscrContEventNotificationService;
    @Autowired
    private SubscrContEventNotificationStatusService subscrContEventNotificationStatusService;
    @Autowired
    private SubscrContEventNotificationStatusV2Service subscrContEventNotificationStatusV2Service;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private ObjectAccessService obectAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrObjectTreeController = new SubscrObjectTreeController(
            subscrObjectTreeService,
            subscrObjectTreeValidationService,
            subscrObjecetTreeContObjectService, subscrContEventNotificationService, subscrContEventNotificationStatusService,
            subscrContEventNotificationStatusV2Service, contObjectService, obectAccessService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrObjectTreeController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
//    @Ignore
	public void testSubscrObjectTreeCRUD() throws Exception {

		ObjectNameVM tree = new ObjectNameVM();
		tree.setObjectName("Object 1");

        Long id = mockMvcRestWrapper.restRequest("/api/subscr/subscrObjectTree/contObjectTreeType1").testPost(tree).getLastId();
//		Long id = _testCreateJson("/api/subscr/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/subscr/subscrObjectTree/contObjectTreeType1/" + id;

        String content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//		String content = _testGetJson(url);

		SubscrObjectTree tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");
		SubscrObjectTree floor2 = subscrObjectTreeService.addChildObject(tree1, "Этаж 2");
		SubscrObjectTree floor3 = subscrObjectTreeService.addChildObject(tree1, "Этаж 3");

        mockMvcRestWrapper.restRequest(url).testPut(tree1);
//		_testUpdateJson(url, tree1);

        content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//		content = _testGetJson(url);
		tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		floor2 = subscrObjectTreeService.searchObject(tree1, "Этаж 2");
		assertNotNull(floor2);

		for (int i = 1; i <= 6; i++) {
			subscrObjectTreeService.addChildObject(floor2, "Кв " + i);
		}

		floor3 = subscrObjectTreeService.searchObject(tree1, "Этаж 3", 1);
		assertNotNull(floor3);

		for (int i = 7; i <= 12; i++) {
			subscrObjectTreeService.addChildObject(floor2, "Кв " + i);
		}

        mockMvcRestWrapper.restRequest(url).testPut(tree1);
//		_testUpdateJson(url, tree1);

//		_testDeleteJson(url);
        mockMvcRestWrapper.restRequest(url).testDelete();

	}

}
