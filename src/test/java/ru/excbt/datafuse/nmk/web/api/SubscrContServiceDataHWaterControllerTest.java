package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvFileUtils;
import ru.excbt.datafuse.nmk.data.service.support.TimeZoneService;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class SubscrContServiceDataHWaterControllerTest extends
		AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContServiceDataHWaterControllerTest.class);

	public final static String API_SERVICE_URL = "/api/subscr";
	public final static String API_SERVICE_URL_TEMPLATE = API_SERVICE_URL
			+ "/%d/service/24h/%d";
	public final static long CONT_OBJECT_ID = 18811504;
	public final static long CONT_ZPOINT_ID = 18811557;
	public final static long CONT_OBJECT2_ID = 18811519;
	public final static long CONT_ZPOINT2_ID = 18811586;

	private final static long SRC_HW_CONT_ZPOINT_ID = 18811586;
	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	@Autowired
	private ContServiceDataHWaterService service;

	@Autowired
	private TimeZoneService timeZoneService;

	@Autowired
	private WebAppPropsService webAppPropsService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testHWater24h() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE, CONT_OBJECT_ID,
				CONT_ZPOINT_ID);

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-10-01")
				.param("endDate", "2013-10-31").with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testHWater24hPaged() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE
				+ "/paged?page=0&size=100", CONT_OBJECT_ID, CONT_ZPOINT_ID);

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-10-01")
				.param("endDate", "2013-10-31").with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testHWaterSummary() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE + "/summary",
				CONT_OBJECT2_ID, CONT_ZPOINT2_ID);

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2015-05-19")
				.param("endDate", "2015-05-25").with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testManualLoadData() throws Exception {

		// Prepare File
		LocalDatePeriod dp = LocalDatePeriod.lastWeek();
		List<ContServiceDataHWater> dataHWater = service.selectByContZPoint(
				SRC_HW_CONT_ZPOINT_ID, TimeDetailKey.TYPE_24H,
				dp.getDateTimeFrom(), dp.getDateTimeTo());

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWater.class,
				ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class)
				.withHeader();

		byte[] fileBytes = mapper.writer(schema).writeValueAsBytes(dataHWater);

		String srcFilename = webAppPropsService.getHWatersCsvOutputDir()
				+ webAppPropsService.getSubscriberCsvFilename(728L, 123L);

		File srcFile = new File(srcFilename);
		
		ByteArrayInputStream is = new ByteArrayInputStream(fileBytes);

		FileWriterUtils.writeFile(is, srcFile);

		// Processing POST

		MockMultipartFile firstFile = new MockMultipartFile("file",
				srcFilename, "text/plain", fileBytes);

		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/contZPoints/%d/service/24h/csv",
				MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				.fileUpload(url).file(firstFile).with(testSecurityContext()));

		resultActions.andDo(MockMvcResultHandlers.print());
		resultActions.andExpect(status().isOk());
		String resultContent = resultActions.andReturn().getResponse()
				.getContentAsString();

		logger.info("Uploaded MD5:{}", resultContent);

	}

	@Test
	public void testGetAvailableFiles() throws Exception {
		String url = apiSubscrUrl("/service/out/csv");
		testJsonGet(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDownloadCsvFile() throws Exception {

		List<File> files = HWatersCsvFileUtils.getOutFiles(webAppPropsService,
				currentSubscriberService.getSubscriberId());

		assertTrue(files.size() > 0);

		String filename = files.get(0).getName();

		
		File f = HWatersCsvFileUtils.getOutCsvFile(webAppPropsService,
				currentSubscriberService.getSubscriberId(), filename);
		
		assertNotNull(f);
		
		String url = apiSubscrUrl("/service/out/csv/" + filename);

		ResultActions resultActions = mockMvc.perform(get(url).contentType(
				MediaType.APPLICATION_JSON).with(testSecurityContext()));

		resultActions.andDo(MockMvcResultHandlers.print());		
		resultActions.andExpect(status().isOk());

	}
}
