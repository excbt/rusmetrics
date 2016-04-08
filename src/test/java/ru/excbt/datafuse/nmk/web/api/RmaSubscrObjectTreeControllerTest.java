package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.api.RmaSubscrObjectTreeController.ObjectNameHolder;

public class RmaSubscrObjectTreeControllerTest extends RmaControllerTest {

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	@Test
	public void testSubscrObjectTreeGet() throws Exception {
		_testGetJson("/api/rma/subscrObjectTree/cont_object_tree_1/1");
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

		String ulr = "/api/rma/subscrObjectTree/contObjectTreeType1/" + id;

		_testDeleteJson(ulr);

	}

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
}
