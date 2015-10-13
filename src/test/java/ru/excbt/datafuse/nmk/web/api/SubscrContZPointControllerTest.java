package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;

public class SubscrContZPointControllerTest extends AnyControllerSubscriberTest {

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	@Autowired
	private ContZPointService contZPointService;

	@Test
	public void testGetZPointEx() throws Exception {
		ContObject co = getFirstContObject();
		List<ContZPointEx> result = contZPointService.findContObjectZPointsEx(co
				.getId());
		assertTrue(result.size() > 0);
		// assertNotNull(result.get(0).getLastDataDate());

		String url = String.format("/api/subscr/contObjects/%d/contZPointsEx",
				co.getId());
		_testJsonGet(url);

	}

	@Test
	public void testGetZPointV_01() throws Exception {
		// http://localhost:8080/nmk-p/api/subscr/contObjects/20118678/contZPointsEx
		// Failed to load resource: the server responded with a status of 500
		// (Internal Server Error)
		_testJsonGet("/api/subscr/contObjects/20118678/contZPointsEx ");
	}

	@Test
	public void testZPointsStatInfo() throws Exception {
		Long contObjectId = getFirstContObjectId();

		String url = String.format(
				"/api/subscr/contObjects/%d/contZPointsStatInfo", contObjectId);
		_testJsonGet(url);

	}

	@Test
	public void testGetZPoint() throws Exception {

		String url = apiSubscrUrl(String.format("/contObjects/%d/zpoints/%d",
				MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));
		_testJsonGet(url);
	}

	@Test
	public void testUpdateZPoint() throws Exception {

		String url = apiSubscrUrl(String.format("/contObjects/%d/zpoints/%d",
				MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));

		ContZPoint contZPoint = contZPointService
				.findOne(MANUAL_HW_CONT_ZPOINT_ID);
		contZPoint.setCustomServiceName("Сервис__"
				+ RandomStringUtils.randomNumeric(5));

		contZPoint.setIsManualLoading(true);

		_testJsonUpdate(url, contZPoint);
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContZPoints() throws Exception {
		_testJsonGet(apiSubscrUrl("/contObjects/zpoints"));
	} 
}
