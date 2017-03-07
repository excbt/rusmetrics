package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.api.SubscrObjectTreeController.ObjectNameHolder;

@WithMockUser(username = "ex1-cab-admin", password = "exbt_123456", roles = { "CONT_OBJECT_ADMIN", "SUBSCR_ADMIN",
		"SUBSCR_CREATE_CABINET", "SUBSCR_CREATE_CHILD", "SUBSCR_USER", "ZPOINT_ADMIN", })
public class SubscrObjectTreeControllerTest extends AnyControllerTest {

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetSubscrObjectTree() throws Exception {

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrObjectTreeCRUD() throws Exception {

		ObjectNameHolder tree = new ObjectNameHolder();
		tree.setObjectName("Object 1");

		Long id = _testCreateJson("/api/subscr/subscrObjectTree/contObjectTreeType1", tree);

		String url = "/api/subscr/subscrObjectTree/contObjectTreeType1/" + id;

		String content = _testGetJson(url);

		SubscrObjectTree tree1 = TestUtils.fromJSON(new TypeReference<SubscrObjectTree>() {
		}, content);

		SubscrObjectTree floor1 = subscrObjectTreeService.addChildObject(tree1, "Этаж 1");
		SubscrObjectTree floor2 = subscrObjectTreeService.addChildObject(tree1, "Этаж 2");
		SubscrObjectTree floor3 = subscrObjectTreeService.addChildObject(tree1, "Этаж 3");

		_testUpdateJson(url, tree1);

		content = _testGetJson(url);
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

		_testUpdateJson(url, tree1);

		_testDeleteJson(url);

	}

	/**
	 *
	 */
	@Override
	public long getSubscriberId() {
		return 512156297L;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return 512156325L;
	}
}
