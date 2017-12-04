package ru.excbt.datafuse.nmk.web.rest;

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

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;


@Transactional
public class RmaContZPointResourceTest extends RmaControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointResourceTest.class);

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	private final static String[] RSO_ORGANIZATIONS = { "TEST_RSO", "RSO_1" };

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ContZPointService contZPointService;

    @Autowired
	private ContZPointMapper contZPointMapper;


	@Test
	//@Ignore
    @Transactional
	public void testZPointCRUD() throws Exception {

        ContZPointFullVM contZPointFullVM0 = new ContZPointFullVM();
        contZPointFullVM0.setDeviceObjectId(65836845L);
        contZPointFullVM0.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.getKeyname());
        contZPointFullVM0.setStartDate(new Date());

        contZPointFullVM0.setRsoId(randomRsoOrganizationId());

		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints", MANUAL_CONT_OBJECT_ID));

		Long contZPointId = _testCreateJson(url, contZPointFullVM0);

		_testGetJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));

        ContZPointFullVM contZPointFullVM;

        {
            ContZPoint contZPoint = contZPointService.findOne(contZPointId);

            contZPointFullVM = contZPointMapper.toFullVM(contZPoint);
        }

		Long activeDeviceObjectId = contZPointFullVM.getDeviceObject() != null ?
            contZPointFullVM.getDeviceObject().getId() : null;
//        contZPointFullVM.getDeviceObjects().clear();
        contZPointFullVM.setContZPointComment("Modified by TEST");
        contZPointFullVM.setRsoId(randomRsoOrganizationId());
        //contZPointFullVM.setContObject(null);
        contZPointFullVM.setContServiceType(null);
        contZPointFullVM.setRso(null);
        contZPointFullVM.setDeviceObject(null);
        contZPointFullVM.setDeviceObjectId(activeDeviceObjectId);
        contZPointFullVM.setExCode("ex_code111");
		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)),
            contZPointFullVM);

		_testDeleteJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));
	}

	@Test
	@Ignore
	public void testTemporaryGet() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, 66183331L)));
	}

	@Test
    @Transactional
	public void testRsoOrganizations() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/rsoOrganizations"));
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
    @Transactional
	public void testContZPointTemperatureChart() throws Exception {
		_testGetJson("/api/subscr/contObjects/488501788/contZPointsEx");
	}

	@Test
    @Transactional
	public void testContZPointMetadata() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");
	}

	@Test
    @Transactional
	public void testContZPointMetadataSrcProp() throws Exception {
		_testGetJson("/api/rma/contObjects/63030238/zpoints/63031662/metadata/srcProp");
	}

    // TODO access_denied
	@Test
    @Transactional
	public void testContZPointMetadataDestProp() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destProp");
	}

    // TODO access_denied
	@Test
    @Transactional
	public void testContZPointMetadataDestDb() throws Exception {
		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destDb");
	}

	// TODO access_denied
	@Ignore
	@Test
    @Transactional
	public void testContZPointMetadataCRUD() throws Exception {
		final String content = _testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");

		List<ContZPointMetadata> metadata = TestUtils.fromJSON(new TypeReference<List<ContZPointMetadata>>() {
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
