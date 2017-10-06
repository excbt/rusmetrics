package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

@Transactional
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

		SubscrContGroup group = new SubscrContGroup();
		group.setContGroupName("Новая группа");

		long[] objectIds = { 18811504L, 18811505L };

		RequestExtraInitializer params = (builder) -> {
			builder.param("contObjectIds", TestUtils.arrayToString(objectIds));
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
		List<SubscrContGroup> contGroups = contGroupService
				.selectSubscriberGroups(currentSubscriberService.getSubscriberParam());

		assertTrue(contGroups.size() > 0);
		SubscrContGroup cg;
		if (contGroups.size() > 2) {
			cg = contGroups.get(1);
		} else {
			cg = contGroups.get(0);
		}

		long[] objectIds = { 18811522L, 18811533L };

		cg.setContGroupComment("TEST AutoUpdate " + System.currentTimeMillis());
		String urlStr = "/api/subscr/contGroup/" + cg.getId();

		RequestExtraInitializer params = (builder) -> {
			builder.param("contObjectIds", TestUtils.arrayToString(objectIds));
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
