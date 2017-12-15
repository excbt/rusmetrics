package ru.excbt.datafuse.nmk.web.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Transactional
public class SubscrContZPointResourceTest extends AnyControllerSubscriberTest {

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	@Autowired
	private ContZPointService contZPointService;

    @Autowired
	private ObjectAccessService objectAccessService;

    @Autowired
    private ContZPointMapper contZPointMapper;

//	@Test
//	public void testGetZPointEx() throws Exception {
//		ContObject co = getFirstContObject();
//		List<ContZPointEx> result = contZPointService.findContObjectZPointsEx(co.getId());
//		assertTrue(result.size() > 0);
//		// assertNotNull(result.get(0).getLastDataDate());
//
//		String url = String.format("/api/subscr/contObjects/%d/contZPointsEx", co.getId());
//		_testGetJson(url);
//
//	}

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

	//@Ignore
//	@Test
//	public void testGetElConsZPointEx() throws Exception {
//        List<Long> ids = objectAccessService.findContObjectIds(getSubscriberId());
//		String url = String.format("/api/subscr/contObjects/%d/contZPointsEx", ids.get(0));
//		_testGetJson(url);
//	}

	@Test
	public void testGetElConsZPointVo() throws Exception {
        List<Long> ids = objectAccessService.findContObjectIds(getSubscriberId());
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/vo", ids.get(0));
		_testGetJson(url);
	}

	@Test
    @Ignore
	public void testGetZPointsV_01() throws Exception {
		_testGetJson("/api/subscr/contObjects/20118678/contZPointsEx ");
	}

	@Test
    //@Ignore
	public void testGetZPointsVO() throws Exception {
        Long contObjectId = getFirstContObjectId();
        String url = String.format("/api/subscr/contObjects/%d/contZPoints/vo", contObjectId);

		_testGetJson(url);
	}

	@Test
	public void testZPointsStatInfo() throws Exception {
		Long contObjectId = getFirstContObjectId();

		String url = String.format("/api/subscr/contObjects/%d/contZPointsStatInfo", contObjectId);
		_testGetJson(url);

	}

	@Test
	public void testGetZPoint() throws Exception {

		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));
		_testGetJson(url);
	}

	@Test
	public void testUpdateZPoint() throws Exception {

		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));

		ContZPoint contZPoint = contZPointService.findOne(MANUAL_HW_CONT_ZPOINT_ID);
		contZPoint.setCustomServiceName("Сервис__" + RandomStringUtils.randomNumeric(5));

		contZPoint.setIsManualLoading(true);

        ContZPointDTO contZPointDTO = contZPointMapper.toDto(contZPoint);
        contZPointDTO.setTagNames(new HashSet<>(Arrays.asList("MY-TAG-1", "MY-TAG-2")));

		_testUpdateJson(url, contZPointDTO);

		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContZPoints() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/zpoints"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContZPointsShortInfo() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/zpoints/shortInfo"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testServiceTypes() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/contServiceTypes"));
	}
}
