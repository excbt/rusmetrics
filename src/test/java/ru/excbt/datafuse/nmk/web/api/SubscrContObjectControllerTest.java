package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrContObjectControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectControllerTest.class);

	private final static String contObjectDaDataFilename = "metadata_json/contObjectDaData.json";

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContObjectService contObjectService;

	@Test
	public void testContObjectsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects");
	}

	@Test
	public void testCmOrganizatoinsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects/cmOrganizations");
	}

	@Test
	public void testOrganizatoinsGet() throws Exception {
		_testGetJson("/api/subscr/contObjects/organizations");
	}

	@Test
	public void testContObjectFiasGet() throws Exception {
		ContObject testCO = findFirstContObject();

		String url = String.format(apiSubscrUrl("/contObjects/%d/fias"), testCO.getId());
		_testGetSuccessful(url);
	}

	@Test
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
	public void testSettingModeTypeGet() throws Exception {
		_testGetJson(apiSubscrUrl("/contObjects/settingModeType"));
	}

	@Test
	public void testSettingModeUpdate() throws Exception {

		List<ContObject> contObjects = currentSubscriberService.getSubscriberContObjects();

		assertNotNull(contObjects);
		assertTrue(contObjects.size() > 0);

		List<Long> contObjectIds = new ArrayList<Long>();
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
		// List<ContObject> subscriberContObject = subscriberService
		// .selectSubscriberContObjects(currentSubscriberService.getSubscriberId());
		//
		// assertTrue(subscriberContObject.size() > 0);

		ContObject testCO = contObjectService.findContObject(20118693L);
		assertNotNull(testCO);
		return testCO;
	}

	@Test
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

		//		ContObjectDaData contObjectDaData = testCO.getContObjectDaData();
		//
		//		if (contObjectDaData == null) {
		//			contObjectDaData = new ContObjectDaData();
		//			testCO.setContObjectDaData(contObjectDaData);
		//
		//		}
		//
		//		contObjectDaData.setSraw(daDataJson);

		_testUpdateJson(urlStr, testCO);

	}

}
