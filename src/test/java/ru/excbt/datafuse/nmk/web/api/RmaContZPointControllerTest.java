package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Ignore;
import org.junit.Test;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaContZPointControllerTest extends AnyControllerTest {

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	@Test
	public void testZPointCRUD() throws Exception {

		ContZPoint contZPoint = new ContZPoint();
		contZPoint.setActiveDeviceObjectId(65836845L);
		contZPoint.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.getKeyname());
		contZPoint.setStartDate(new Date());

		String url = apiRmaUrl(String.format("/contObjects/%d/zpoints", MANUAL_CONT_OBJECT_ID));

		Long contZPointId = _testJsonCreate(url, contZPoint);

		_testJsonGet(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));

		_testJsonDelete(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));
	}

	@Test
	@Ignore
	public void testTemporaryGet() throws Exception {
		_testJsonGet(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, 66183331L)));
	}
}
