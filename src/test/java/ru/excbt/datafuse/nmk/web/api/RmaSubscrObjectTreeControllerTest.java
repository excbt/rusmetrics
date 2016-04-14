package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.api.RmaSubscrObjectTreeController.ObjectNameHolder;

public class RmaSubscrObjectTreeControllerTest extends RmaControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrObjectTreeControllerTest.class);

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	@Autowired
	private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

	/**
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrObjectTreeGet() throws Exception {
		_testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1/1");
	}

	/**
	 * 
	 */
	@Test
	public void testSubscrObjectTreeListGet() throws Exception {
		_testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1");
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<SubscrObjectTreeTemplate> getTemplates() throws Exception {
		String content = _testGetJson("/api/rma/subscrObjectTreeTemplates");

		List<SubscrObjectTreeTemplate> templates = fromJSON(new TypeReference<List<SubscrObjectTreeTemplate>>() {
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
	public void testSubscrObjectTreeTemplateCRUD() throws Exception {

		Long templateId = getAnyTemplate();

		ObjectNameHolder tree = new ObjectNameHolder();
		tree.setObjectName("Object 1");
		tree.setTemplateId(templateId);

		Long id = _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		_testDeleteJson(url);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrObjectTreeCRUD() throws Exception {

		ObjectNameHolder tree = new ObjectNameHolder();
		tree.setObjectName("Object 1");

		Long id = _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = _testGetJson(url);

		SubscrObjectTree tree1 = fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");
		SubscrObjectTree floor2 = subscrObjectTreeService.addChildObject(tree1, "Этаж 2");
		SubscrObjectTree floor3 = subscrObjectTreeService.addChildObject(tree1, "Этаж 3");

		_testUpdateJson(url, tree1);

		content = _testGetJson(url);
		tree1 = fromJSON(new TypeReference<SubscrObjectTree>() {
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

		_testUpdateJson(url, tree1);

		_testDeleteJson(url);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
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

		ObjectNameHolder tree = new ObjectNameHolder();
		tree.setObjectName("Поэтажный план 1");
		tree.setTemplateId(template.getId());

		Long id = _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = _testGetJson(url);

		SubscrObjectTree tree1 = fromJSON(new TypeReference<SubscrObjectTree>() {
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

		_testUpdateJson(url, tree1);

		content = _testGetJson(url);
		tree1 = fromJSON(new TypeReference<SubscrObjectTree>() {
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

		_testUpdateJson(url, tree1);

		_testDeleteJson(url);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrObjectTreeTemplateCreateInvalidCheck() throws Exception {

		Long templateId = getAnyTemplate();

		ObjectNameHolder tree = new ObjectNameHolder();
		tree.setObjectName("Object 1");
		tree.setTemplateId(templateId);

		Long id = _testCreateJson("/api/rma/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		String content = _testGetJson(url);

		SubscrObjectTree tree1 = fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is4xxClientError());
		};

		_testUpdateJson(url, tree1, null, tester);

		_testDeleteJson(url);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrObjectTreeContObjects() throws Exception {

		String treeListContent = _testGetJson("/api/rma/subscrObjectTree/contObjectTreeType1");
		List<SubscrObjectTree> treeList = fromJSON(new TypeReference<List<SubscrObjectTree>>() {
		}, treeListContent);

		if (treeList.isEmpty()) {
			logger.warn("Tree List is empty. Skip test");
			return;
		}

		Long treeId = treeList.get(0).getId();

		String treeContent = _testGetJson(String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d", treeId));
		SubscrObjectTree tree = fromJSON(new TypeReference<SubscrObjectTree>() {
		}, treeContent);

		Long treeNodeId = tree.getChildObjectList().size() > 0 ? tree.getChildObjectList().get(0).getId() : null;

		assertNotNull(treeNodeId);

		String freeContent = _testGetJson(
				String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d/contObjects/free", treeId));

		List<ContObject> freeContObjects = fromJSON(new TypeReference<List<ContObject>>() {
		}, freeContent);

		String contObjectsUrl = String.format("/api/rma/subscrObjectTree/contObjectTreeType1/%d/node/%d/contObjects",
				treeId, treeNodeId);

		String content = _testGetJson(contObjectsUrl);

		List<ContObject> linkedContObjects = fromJSON(new TypeReference<List<ContObject>>() {
		}, content);

		assertNotNull(freeContObjects);

		if (!freeContObjects.isEmpty()) {

			List<Long> contObjectIds = new ArrayList<>();
			for (int i = 0; i < freeContObjects.size(); i++) {
				ContObject contObject = freeContObjects.get(i);
				contObjectIds.add(contObject.getId());
			}

			_testUpdateJson(contObjectsUrl, contObjectIds);

			logger.info("Found {} free contObjects", freeContObjects.size());
			logger.info("Saved {} contObjects", contObjectIds.size());

		} else {

			List<Long> contObjectIds = new ArrayList<>();
			for (int i = 0; i < linkedContObjects.size(); i++) {
				ContObject contObject = linkedContObjects.get(i);
				contObjectIds.add(contObject.getId());
			}
			_testDeleteJson(contObjectsUrl, contObjectIds);

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
		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/512134227";

		_testDeleteJson(url);
	}

	@Ignore
	@Test
	public void testDeleteContObjects2() throws Exception {
		String url = "/api/rma/subscrObjectTree/contObjectTreeType1/512133836/node/512133837/contObjects";

		List<Long> ids = Arrays.asList(488501788L);

		_testDeleteJson(url, ids);
	}

}
