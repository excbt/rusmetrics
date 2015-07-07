package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;

public class SubscrContZPointControllerTest extends AnyControllerSubscriberTest {

	@Autowired
	private ContZPointService contZPointService;

	@Test
	public void testGetZPointEx() throws Exception {
		ContObject co = getFirstContObject();
		List<ContZPointEx> result = contZPointService.findContZPointsEx(co
				.getId());
		assertTrue(result.size() > 0);
		// assertNotNull(result.get(0).getLastDataDate());

		String url = String.format("/api/subscr/contObjects/%d/contZPointsEx",
				co.getId());
		testJsonGet(url);

	}

	@Test
	public void testGetZPointV_01() throws Exception {
		// http://localhost:8080/nmk-p/api/subscr/contObjects/20118678/contZPointsEx
		// Failed to load resource: the server responded with a status of 500
		// (Internal Server Error)
		testJsonGet("/api/subscr/contObjects/20118678/contZPointsEx ");
	}

	@Test
	public void testZPointsStatInfo() throws Exception {
		Long contObjectId = getFirstContObjectId();

		String url = String.format(
				"/api/subscr/contObjects/%d/contZPointsStatInfo", contObjectId);
		testJsonGet(url);

	}
}
