package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaContZPointControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointControllerTest.class);

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	private final static String[] RSO_ORGANIZATIONS = { "TEST_RSO", "RSO_1" };

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ContZPointService contZPointService;

	@Test
	//@Ignore
	public void testZPointCRUD() throws Exception {

		ContZPoint contZPoint = new ContZPoint();
		contZPoint.set_activeDeviceObjectId(65836845L);
		contZPoint.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.getKeyname());
		contZPoint.setStartDate(new Date());

		contZPoint.setRsoId(randomRsoOrganizationId());

		String url = apiRmaUrl(String.format("/contObjects/%d/zpoints", MANUAL_CONT_OBJECT_ID));

		Long contZPointId = _testCreateJson(url, contZPoint);

		_testGetJson(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));

		contZPoint = contZPointService.findOne(contZPointId);

		Long activeDeviceObjectId = contZPoint.get_activeDeviceObjectId();
		contZPoint.getDeviceObjects().clear();
		contZPoint.setContZPointComment("Modified by TEST");
		contZPoint.setRsoId(randomRsoOrganizationId());
		contZPoint.setContObject(null);
		contZPoint.setContServiceType(null);
		contZPoint.setRso(null);
		contZPoint.setDeviceObjects(null);
		contZPoint.set_activeDeviceObjectId(activeDeviceObjectId);
		contZPoint.setExCode("ex_code111");
		_testUpdateJson(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)),
				contZPoint);

		_testDeleteJson(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));
	}

	@Test
	@Ignore
	public void testTemporaryGet() throws Exception {
		_testGetJson(apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, 66183331L)));
	}

	@Test
	public void testRsoOrganizations() throws Exception {
		_testGetJson(apiRmaUrl("/contObjects/rsoOrganizations"));
	}

	private Long randomRsoOrganizationId() {
		int idx = ThreadLocalRandom.current().nextInt(0, 2);

		SubscriberParam param = SubscriberParam.builder().subscriberId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID)
				.rmaSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID).build();
		Organization o = organizationService.selectByKeyname(param, RSO_ORGANIZATIONS[idx]);
		return o == null ? null : o.getId();
	}

	@Ignore
	@Test
	public void testContZPointTemperatureChart() throws Exception {
		_testGetJson("/api/subscr/contObjects/488501788/contZPointsEx");
	}

	@Test
	public void testContZPointMetadata() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");
	}

	@Test
	public void testContZPointMetadataSrcProp() throws Exception {
		_testGetJson("/api/rma/contObjects/63030238/zpoints/63031662/metadata/srcProp");
	}

	@Test
	public void testContZPointMetadataDestProp() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destProp");
	}

	@Test
	public void testContZPointMetadataDestDb() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destDb");
	}

	@Test
	public void testContZPointMetadataCRUD() throws Exception {
		final String content = _testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");

		List<ContZPointMetadata> metadata = fromJSON(new TypeReference<List<ContZPointMetadata>>() {
		}, content);

		assertNotNull(metadata);

		logger.info("Found {} records", metadata.size());

		for (ContZPointMetadata m : metadata) {
			if (!m.isNew()) {
				m.setDevComment("Updated by REST at: " + System.currentTimeMillis());
			}
		}

		_testUpdateJson("/api/rma/contObjects/725/zpoints/512084866/metadata", metadata);

	}

}
