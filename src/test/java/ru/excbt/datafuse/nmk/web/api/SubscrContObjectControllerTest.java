package ru.excbt.datafuse.nmk.web.api;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class SubscrContObjectControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectControllerTest.class);

	private final static String contObjectDaDataFilename = "metadata_json/contObjectDaData.json";

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private ContObjectFiasRepository contObjectFiasRepository;;

	@Test
	@Transactional
	public void testContObjectsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects");
	}

	@Test
	@Transactional
	public void testCmOrganizatoinsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects/cmOrganizations");
	}

	@Test
	@Transactional
	public void testOrganizatoinsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects/organizations");
	}

	@Test
	@Transactional	
	public void testContObjectFiasGet() throws Exception {
		
		List<Long> ids = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
		
		
		List<ContObjectFias> fiasIds = contObjectFiasRepository.selectByContObjectIds(ids);
		
		Optional<ContObjectFias> testObjectFias = fiasIds.stream().filter(i -> i.getFiasUUID() != null).findAny();
		
		
		String url = String.format(apiSubscrUrl("/contObjects/%d/fias"),
				testObjectFias.isPresent() ? testObjectFias.get().getContObjectId() : fiasIds.get(0).getContObjectId());
		_testGetSuccessful(url);
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {

		ContObject testCO = findFirstContObject();
		logger.info("Found ContObject (id={})", testCO.getId());
		testCO.setComment("Updated by REST test at " + DateTime.now().toString());

		String urlStr = "/api/subscr/contObjects/" + testCO.getId();

		RequestExtraInitializer param = (builder) -> {
			builder.param("cmOrganizationId", testCO.get_activeContManagement().getOrganization().getId().toString());
		};

		_testUpdateJson(urlStr, testCO, param);

		// String jsonBody = OBJECT_MAPPER.writeValueAsString(testCO);
		//
		//
		//
		// ResultActions resultActionsAll;
		// try {
		// resultActionsAll =
		// mockMvc.perform(put(urlStr).contentType(MediaType.APPLICATION_JSON).content(jsonBody)
		// .with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));
		//
		// resultActionsAll.andDo(MockMvcResultHandlers.print());
		//
		// resultActionsAll.andExpect(status().isOk());
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// fail(e.toString());
		// }
	}

	@Test
	@Transactional
	public void testSettingModeTypeGet() throws Exception {
		_testGetJson(apiSubscrUrl("/contObjects/settingModeType"));
	}

	@Test
	@Transactional
	public void testSettingModeUpdate() throws Exception {

		List<ContObject> contObjects = subscrContObjectService
				.selectSubscriberContObjects(currentSubscriberService.getSubscriberParam());

		assertNotNull(contObjects);
		assertTrue(contObjects.size() > 0);

		List<Long> contObjectIds = new ArrayList<>();
		contObjectIds.add(contObjects.get(0).getId());

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {
			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("contObjectIds", listToString(contObjectIds));
				builder.param("currentSettingMode", "summer");
			}
		};
		_testUpdateJson(apiSubscrUrl("/contObjects/settingModeType"), null, extraInitializer);

	}

	/**
	 * 
	 * @return
	 */
	private ContObject findFirstContObject() {
		List<Long> ids = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
		ContObject testCO = ids.isEmpty() ? null : contObjectService.findContObject(ids.get(0));
		assertNotNull(testCO);
		return testCO;
	}

	@Test
	@Transactional
	public void testContObjectDaData() throws Exception {
		Long id = 725L;

		byte[] encoded = Files.readAllBytes(Paths.get(contObjectDaDataFilename));
		String daDataJson = new String(encoded, StandardCharsets.UTF_8);
		assertNotNull(daDataJson);

		ObjectMapper mapper = new ObjectMapper();

		Object json = mapper.readValue(daDataJson, Object.class);

		logger.info("daDataJson: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));

		ContObject testCO = contObjectService.findContObject(id);
		String urlStr = "/api/subscr/contObjects/" + testCO.getId();

		testCO.set_daDataSraw(daDataJson);

		_testUpdateJson(urlStr, testCO);

	}

	@Test
	@Transactional
	public void testGetContObjectsGrouped() throws Exception {
		_testGetJson("/api/subscr/contObjects/?contGroupId=488528511");
	}

	@Override
	public long getSubscriberId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
