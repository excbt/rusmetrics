package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import javax.transaction.Transactional;

public class ReferencePeriodControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ReferencePeriodControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	private Long getOId() {
		List<Long> vList = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());
		assertTrue(vList.size() > 0);
		return vList.get(0);
	}

	/*

	 */
	private Long getZPointId(Long oId) {
		List<Long> vList2 = contZPointService.selectContZPointIds(oId);
		assertTrue(vList2.size() > 0);
		return vList2.get(0);
	}

	/*

	 */
	@Test
    @Transactional
	public void testGetLast() throws Exception {

		Long oId = getOId();
		Long zpId = getZPointId(oId);
		_testGetJson(String.format("/api/subscr/contObjects/%d/zpoints/%d/referencePeriod", oId, zpId));
	}

	/*

	 */
	@Test
    @Transactional
	public void testCreateUpdateDelete() throws Exception {
		Long oId = getOId();
		Long zpId = getZPointId(oId);
		String urlStr = String.format("/api/subscr/contObjects/%d/zpoints/%d/referencePeriod", oId, zpId);

		// Create testing
		ReferencePeriod referencePeriod = new ReferencePeriod();
		referencePeriod.setBeginDate(new Date());
		referencePeriod.setPeriodDescription("Testing ReferencePeriod");
		referencePeriod.setTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname());

		Long createdId = _testCreateJson(urlStr, referencePeriod);

		// Update testing
		referencePeriod.setId(Long.valueOf(createdId));

		referencePeriod.setPeriodDescription("Testing Update");

		_testUpdateJson(urlStr + "/" + createdId, referencePeriod);

		// Delete testing
		_testDeleteJson(urlStr + "/" + createdId);

	}
}
