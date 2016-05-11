package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContGroup;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrContGroupControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContGroupControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContGroupService contGroupService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContGroupObjects() throws Exception {
		_testGetJson("/api/subscr/contGroup/0/contObject/available");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContGroup() throws Exception {
		_testGetJson("/api/subscr/contGroup");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateContGroup() throws Exception {

		ContGroup group = new ContGroup();
		group.setContGroupName("Новая группа");

		long[] objectIds = { 18811504L, 18811505L };

		RequestExtraInitializer params = (builder) -> {
			builder.param("contObjectIds", arrayToString(objectIds));
		};

		Long id = _testCreateJson("/api/subscr/contGroup", group, params);

		testDeleteContGroup(id);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateContGroup() throws Exception {
		List<ContGroup> contGroups = contGroupService
				.selectSubscriberGroups(currentSubscriberService.getSubscriberParam());

		assertTrue(contGroups.size() > 0);
		ContGroup cg;
		if (contGroups.size() > 2) {
			cg = contGroups.get(1);
		} else {
			cg = contGroups.get(0);
		}

		long[] objectIds = { 18811522L, 18811533L };

		cg.setContGroupComment("TEST AutoUpdate " + System.currentTimeMillis());
		String urlStr = "/api/subscr/contGroup/" + cg.getId();

		RequestExtraInitializer params = (builder) -> {
			builder.param("contObjectIds", arrayToString(objectIds));
		};

		_testUpdateJson(urlStr, cg, params);
	}

	/**
	 * 
	 * @param contGroupId
	 * @throws Exception
	 */

	public void testDeleteContGroup(Long contGroupId) throws Exception {
		_testDeleteJson("/api/subscr/contGroup/" + contGroupId);
	}

}
