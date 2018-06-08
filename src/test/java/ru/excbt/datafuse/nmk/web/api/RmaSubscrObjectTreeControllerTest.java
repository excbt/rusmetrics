package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;
import ru.excbt.datafuse.nmk.web.api.SubscrObjectTreeController.ObjectNameVM;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class RmaSubscrObjectTreeControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrObjectTreeControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrObjectTreeService subscrObjectTreeService;

    @Autowired
    private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

    @Autowired
    private RmaSubscrObjectTreeController rmaSubscrObjectTreeController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscrObjectTreeController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrObjectTreeGet() throws Exception {
//		_testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1/1");
	}

	/**
	 *
	 */
	@Test
	public void testSubscrObjectTreeListGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1").testGet();
//		_testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1");
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	private List<SubscrObjectTreeTemplate> getTemplates() throws Exception {

		String content = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTreeTemplates").testGet().getStringContent();
//            _testGetJson("/api/rma/subscrObjectTreeTemplates");

		List<SubscrObjectTreeTemplate> templates = TestUtils.fromJSON(new TypeReference<List<SubscrObjectTreeTemplate>>() {
		}, content);

		assertNotNull(templates);

		return templates;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	private Long getAnyTemplate() throws Exception {
		List<SubscrObjectTreeTemplate> templates = getTemplates();
		return templates.isEmpty() ? null : templates.get(0).getId();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSubscrObjectTreeTemplateCRUD() throws Exception {

		Long templateId = getAnyTemplate();

		ObjectNameVM tree = new ObjectNameVM();
		tree.setObjectName("Object 1");
		tree.setTemplateId(templateId);

		Long id = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1").testPost(tree).getLastId();
//            _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;
        mockMvcRestWrapper.restRequest(url).testDelete();

//		_testDeleteJson(url);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSubscrObjectTreeCRUD() throws Exception {

		ObjectNameVM tree = new ObjectNameVM();
		tree.setObjectName("Object 1");

		Long id = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1").testPost(tree).getLastId();
//            _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//            _testGetJson(url);


		SubscrObjectTree tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");
		SubscrObjectTree floor2 = subscrObjectTreeService.addChildObject(tree1, "Этаж 2");
		SubscrObjectTree floor3 = subscrObjectTreeService.addChildObject(tree1, "Этаж 3");

        mockMvcRestWrapper.restRequest(url).testPut(tree1);
//		_testUpdateJson(url, tree1);

		content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//            _testGetJson(url);
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

        mockMvcRestWrapper.restRequest(url)
            .testPut(tree1)
            .testDelete();
//		_testUpdateJson(url, tree1);

//		_testDeleteJson(url);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSubscrObjectTreeTemplate2CRUD() throws Exception {
		List<SubscrObjectTreeTemplate> templates = getTemplates();

		SubscrObjectTreeTemplate template = null;

		List<SubscrObjectTreeTemplateItem> items = new ArrayList<>();

		for (SubscrObjectTreeTemplate t : templates) {
			items = subscrObjectTreeTemplateService.selectSubscrObjectTreeTemplateItems(t.getId());
			if (items.size() > 1) {
				template = t;
				break;
			}
		}

		assertTrue(template != null);
		assertTrue(items.size() > 0);

		ObjectNameVM tree = new ObjectNameVM();
		tree.setObjectName("Поэтажный план 1");
		tree.setTemplateId(template.getId());

		Long id = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1").testPost(tree).getLastId();
//            _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//            _testGetJson(url);

		SubscrObjectTree tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTreeTemplateItem itemlevel1 = items.stream().filter(i -> i.getItemLevel().equals(1)).findFirst()
				.get();
		SubscrObjectTreeTemplateItem itemlevel2 = items.stream().filter(i -> i.getItemLevel().equals(2)).findFirst()
				.get();

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");
		floor1.setTemplateItemId(itemlevel1.getId());
		SubscrObjectTree floor2 = subscrObjectTreeService.addChildObject(tree1, "Этаж 2");
		floor2.setTemplateItemId(itemlevel1.getId());
		SubscrObjectTree floor3 = subscrObjectTreeService.addChildObject(tree1, "Этаж 3");
		floor3.setTemplateItemId(itemlevel1.getId());

        mockMvcRestWrapper.restRequest(url).testPut(tree1);
//		_testUpdateJson(url, tree1);

		content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//        _testGetJson(url);
		tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		floor2 = subscrObjectTreeService.searchObject(tree1, "Этаж 2");
		assertNotNull(floor2);

		for (int i = 1; i <= 2; i++) {
			SubscrObjectTree apt = subscrObjectTreeService.addChildObject(floor2, "Кв " + i);
			apt.setTemplateItemId(itemlevel2.getId());
		}

		floor3 = subscrObjectTreeService.searchObject(tree1, "Этаж 3", 1);
		assertNotNull(floor3);

		for (int i = 3; i <= 4; i++) {
			SubscrObjectTree apt = subscrObjectTreeService.addChildObject(floor2, "Кв " + i);
			apt.setTemplateItemId(itemlevel2.getId());
		}

        mockMvcRestWrapper.restRequest(url)
            .testPut(tree1)
            .testDelete();
//		_testUpdateJson(url, tree1);

//		_testDeleteJson(url);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSubscrObjectTreeTemplateCreateInvalidCheck() throws Exception {

		Long templateId = getAnyTemplate();

		ObjectNameVM tree = new ObjectNameVM();
		tree.setObjectName("Object 1");
		tree.setTemplateId(templateId);

		Long id = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1")
            .testPost(tree).getLastId();
//            _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//		_testGetJson(url);

		SubscrObjectTree tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is4xxClientError());
		};

        mockMvcRestWrapper.restRequest(url).testPutAndReturn(tree1)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is4xxClientError());
//		_testUpdateJson(url, tree1, null, tester);

        mockMvcRestWrapper.restRequest(url).testDelete();
//		_testDeleteJson(url);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrObjectTreeContObjects() throws Exception {

		String treeListContent = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1").testGet().getStringContent();
//            _testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1");
		List<SubscrObjectTree> treeList = TestUtils.fromJSON(new TypeReference<List<SubscrObjectTree>>() {
		}, treeListContent);

		if (treeList.isEmpty()) {
			logger.warn("Tree List is empty. Skip test");
			return;
		}

		Long treeId = null;
		Long treeNodeId = null;
		for (SubscrObjectTree t : treeList) {
			treeId = t.getId();
			String treeContent = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1/{id1}", treeId).testGet().getStringContent();
//                _testGetJson(
//					String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d", treeId));
			SubscrObjectTree tree = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
			}, treeContent);

			treeNodeId = tree.getChildObjectList().size() > 0 ? tree.getChildObjectList().get(0).getId() : null;

			if (treeNodeId != null) {
				break;
			}

		}

		assertNotNull("There is no available nodes", treeNodeId);

		String freeContent = mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1/{id1}/contObjects/free", treeId)
            .testGet().getStringContent();
//            _testGetJson(
//				String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d/contObjects/free", treeId));

		List<ContObject> freeContObjects = TestUtils.fromJSON(new TypeReference<List<ContObject>>() {
		}, freeContent);

		String contObjectsUrl = String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d/node/%d/contObjects",
				treeId, treeNodeId);

		String contObjectsUrlAdd = contObjectsUrl + "/add";
		String contObjectsUrlRemove = contObjectsUrl + "/remove";

		String content = mockMvcRestWrapper.restRequest(contObjectsUrl).testGet().getStringContent();
//            _testGetJson(contObjectsUrl);

		List<ContObject> linkedContObjects = TestUtils.fromJSON(new TypeReference<List<ContObject>>() {
		}, content);

		assertNotNull(freeContObjects);

		if (!freeContObjects.isEmpty()) {

			List<Long> contObjectIds = new ArrayList<>();
			for (int i = 0; i < freeContObjects.size(); i++) {
				ContObject contObject = freeContObjects.get(i);
				contObjectIds.add(contObject.getId());
			}

            mockMvcRestWrapper.restRequest(contObjectsUrlAdd).testPut(contObjectIds);
//			_testUpdateJson(contObjectsUrlAdd, contObjectIds);

			logger.info("Found {} free contObjects", freeContObjects.size());
			logger.info("Saved {} contObjects", contObjectIds.size());

		} else {

			List<Long> contObjectIds = new ArrayList<>();
			for (int i = 0; i < linkedContObjects.size(); i++) {
				ContObject contObject = linkedContObjects.get(i);
				contObjectIds.add(contObject.getId());
			}
            mockMvcRestWrapper.restRequest(contObjectsUrlRemove).testPut(contObjectIds);
//			_testUpdateJson(contObjectsUrlRemove, contObjectIds);

			logger.info("Found {} linked contObjects", linkedContObjects.size());
			logger.info("Deleted {} contObjects", contObjectIds.size());

		}

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testDeleteChildNode() throws Exception {
//		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/512134227";
        mockMvcRestWrapper.restRequest("/api/rma/subscrObjectTree/contObjectTreeType1/{id1}", 512134227).testDelete();
//		_testDeleteJson(url);
	}

	@Ignore
	@Test
	public void testDeleteContObjects2() throws Exception {
		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/512133836/node/512133837/contObjects";

		List<Long> ids = Arrays.asList(488501788L);

        mockMvcRestWrapper.restRequest(url).testDelete(ids);
//		_testDeleteJson(url, ids);
	}

}
