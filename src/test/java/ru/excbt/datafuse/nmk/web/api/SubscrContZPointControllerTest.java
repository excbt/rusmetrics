package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
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
		List<ContZPointEx> result = contZPointService.findContObjectZPointsEx(co.getId());
		assertTrue(result.size() > 0);
		// assertNotNull(result.get(0).getLastDataDate());

		String url = String.format("/api/subscr/contObjects/%d/contZPointsEx", co.getId());
		_testGetJson(url);

	}

	@Test
	public void testGetZPointsTimeDetailLastDateMap() throws Exception {
		Long coId = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/timeDetailLastDate", coId);
		_testGetJson(url);
	}

	@Test
	public void testGetZPointTimeDetailLastDate() throws Exception {
		Long coId = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/%d/timeDetailLastDate", coId, 20118714);
		_testGetJson(url);
	}

	@Ignore
	@Test
	public void testGetElConsZPointEx() throws Exception {
		String url = "/api/subscr/contObjects/77921790/contZPointsEx";
		_testGetJson(url);

	}

	@Test
	public void testGetZPointsV_01() throws Exception {
		_testGetJson("/api/subscr/contObjects/20118678/contZPointsEx ");
	}

	@Test
	public void testGetZPointsVO() throws Exception {
		_testGetJson("/api/subscr/contObjects/20118678/contZPoints/vo ");
	}

	@Test
	public void testZPointsStatInfo() throws Exception {
		Long contObjectId = getFirstContObjectId();

		String url = String.format("/api/subscr/contObjects/%d/contZPointsStatInfo", contObjectId);
		_testGetJson(url);

	}

	@Test
	public void testGetZPoint() throws Exception {

		String url = apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));
		_testGetJson(url);
	}

	@Test
	public void testUpdateZPoint() throws Exception {

		String url = apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));

		ContZPoint contZPoint = contZPointService.findOne(MANUAL_HW_CONT_ZPOINT_ID);
		contZPoint.setCustomServiceName("Сервис__" + RandomStringUtils.randomNumeric(5));

		contZPoint.setIsManualLoading(true);

		_testUpdateJson(url, contZPoint);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContZPoints() throws Exception {
		_testGetJson(apiSubscrUrl("/contObjects/zpoints"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContZPointsShortInfo() throws Exception {
		_testGetJson(apiSubscrUrl("/contObjects/zpoints/shortInfo"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServiceTypes() throws Exception {
		_testGetJson(apiSubscrUrl("/contObjects/contServiceTypes"));
	}
}
