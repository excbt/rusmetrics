package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaContZPointControllerTest extends AnyControllerTest {

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	private final static String[] RSO_ORGANIZATIONS = { "TEST_RSO", "RSO_1" };

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ContZPointService contZPointService;

	@Test
	public void testZPointCRUD() throws Exception {

		ContZPoint contZPoint = new ContZPoint();
		contZPoint.setActiveDeviceObjectId(65836845L);
		contZPoint.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.getKeyname());
		contZPoint.setStartDate(new Date());

		contZPoint.setRsoId(randomRsoOrganizationId());

		String url = apiRmaUrl(String.format("/contObjects/%d/zpoints", MANUAL_CONT_OBJECT_ID));

		Long contZPointId = _testJsonCreate(url, contZPoint);

		_testJsonGet(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));

		contZPoint = contZPointService.findOne(contZPointId);
		contZPoint.getDeviceObjects().clear();
		contZPoint.setActiveDeviceObjectId(65836845L);
		contZPoint.setContZPointComment("Modified by TEST");
		contZPoint.setRsoId(randomRsoOrganizationId());
		_testJsonUpdate(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)),
				contZPoint);

		_testJsonDelete(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));
	}

	@Test
	@Ignore
	public void testTemporaryGet() throws Exception {
		_testJsonGet(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, 66183331L)));
	}

	@Test
	public void testRsoOrganizations() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects/rsoOrganizations"));
	}

	private Long randomRsoOrganizationId() {
		int idx = ThreadLocalRandom.current().nextInt(0, 2);

		Organization o = organizationService.selectByKeyname(RSO_ORGANIZATIONS[idx]);
		return o == null ? null : o.getId();
	}

}
