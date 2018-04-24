package ru.excbt.datafuse.nmk.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class SubscrContServiceDataHWaterResourceTest extends PortalApiTest {

	private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataHWaterResourceTest.class);

	public final static String API_SERVICE_URL = "/api/subscr";
	public final static String API_SERVICE_URL_TEMPLATE = API_SERVICE_URL + "/%d/service/24h/%d";
	public final static long CONT_OBJECT_ID = 18811504;
	public final static long CONT_ZPOINT_ID = 18811557;
	public final static long CONT_OBJECT2_ID = 18811519;
	public final static long CONT_ZPOINT2_ID = 18811586;

	private final static long SRC_HW_CONT_ZPOINT_ID = 18811586;
	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;



    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrContServiceDataHWaterResource subscrContServiceDataHWaterResource;

    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private HWatersCsvService hWatersCsvService;
    @Autowired
    private WebAppPropsService webAppPropsService;
    @Autowired
    private CurrentSubscriberService currentSubscriberService;
    @Autowired
    private ContServiceDataHWaterService contServiceDataHWaterService;
    @Autowired
    private ContServiceDataHWaterDeltaService contObjectHWaterDeltaService;
    @Autowired
    private ContServiceDataHWaterImportService contServiceDataHWaterImportService;
    @Autowired
    private SubscrDataSourceService subscrDataSourceService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscrContServiceDataImpulseResource subscrContServiceDataImpulseController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContServiceDataHWaterResource = new SubscrContServiceDataHWaterResource(
            contZPointService,
            hWatersCsvService,
            webAppPropsService,
            currentSubscriberService,
            contServiceDataHWaterService,
            contObjectHWaterDeltaService,
            contServiceDataHWaterImportService,
            subscrDataSourceService,
            objectAccessService,
            portalUserIdsService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrContServiceDataHWaterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testHWater24h() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE, CONT_OBJECT_ID, CONT_ZPOINT_ID);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr)
            .param("beginDate", "2013-10-01")
            .param("endDate", "2013-10-31"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testHWater24hPaged() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE + "/paged?page=0&size=100", CONT_OBJECT_ID,
				CONT_ZPOINT_ID);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr)
            .param("beginDate", "2013-10-01")
            .param("endDate", "2013-10-31")
            .param("dataDateSort", "asc"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));


	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testHWaterSummary() throws Exception {

		String urlStr = String.format(API_SERVICE_URL_TEMPLATE + "/summary", CONT_OBJECT2_ID, CONT_ZPOINT2_ID);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr)
                .param("beginDate", "2015-05-19")
                .param("endDate", "2015-05-25"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManualLoadData() throws Exception {

		// Prepare File
		// LocalDatePeriod dp = LocalDatePeriod.lastWeek();
		LocalDatePeriod dp = LocalDatePeriod.builder().dateFrom("2014-04-01").dateTo("2014-04-30").build()
				.buildEndOfDay();
		List<ContServiceDataHWater> dataHWater = contServiceDataHWaterService.selectByContZPoint(SRC_HW_CONT_ZPOINT_ID,
				TimeDetailKey.TYPE_24H, dp);

		byte[] fileBytes = hWatersCsvService.writeDataHWaterToCsv(dataHWater);

		String srcFilename = webAppPropsService.getHWatersCsvOutputDir()
				+ webAppPropsService.getSubscriberCsvFilename(728L, 123L);

		// Processing POST

		MockMultipartFile firstFile = new MockMultipartFile("file", srcFilename, "text/plain", fileBytes);

		String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/contZPoints/%d/service/24h/csv", MANUAL_CONT_OBJECT_ID,
				MANUAL_HW_CONT_ZPOINT_ID));

		ResultActions resultActions = restPortalContObjectMockMvc
				.perform(MockMvcRequestBuilders.fileUpload(url).file(firstFile).with(testSecurityContext()));

		resultActions.andDo(MockMvcResultHandlers.print());
		resultActions.andExpect(status().is2xxSuccessful());
		String resultContent = resultActions.andReturn().getResponse().getContentAsString();

		log.info("Uploaded FileInfoMD5:{}", resultContent);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetAvailableFiles() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/service/out/csv");

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(url))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
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

		File f = HWatersCsvFileUtils.getOutCsvFile(webAppPropsService, currentSubscriberService.getSubscriberId(),
				filename);

		assertNotNull(f);

		String url = UrlUtils.apiSubscrUrl("/service/out/csv/" + filename);

        restPortalContObjectMockMvc.perform(get(url))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManualDeleteData() throws Exception {

		LocalDatePeriod datePeriod = LocalDatePeriod.lastWeek();

		String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/contZPoints/%d/service/24h/csv", MANUAL_CONT_OBJECT_ID,
				MANUAL_HW_CONT_ZPOINT_ID));

		log.info("beginDate={}, endDate={}", datePeriod.getDateFromStr(), datePeriod.getDateToStr());

		ResultActions resultAction = restPortalContObjectMockMvc.perform(
				delete(url).contentType(MediaType.APPLICATION_JSON).param("beginDate", datePeriod.getDateFromStr())
						.param("endDate", datePeriod.getDateToStr()).with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());
		resultAction.andExpect(status().is2xxSuccessful());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContObjectServiceTypeInfo() throws Exception {

		String urlStr = UrlUtils.apiSubscrUrl("/service/hwater/contObjects/serviceTypeInfo");

//		RequestExtraInitializer requestExtraInitializer = (requestBuilder) -> {
//			requestBuilder.contentType(MediaType.APPLICATION_JSON).param("dateFrom", "2015-07-01").param("dateTo",
//					"2015-07-31");
//		};

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr)
                .param("dateFrom", "2015-07-01")
                .param("dateTo","2015-07-31"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testOneCityContObjectServiceTypeInfo() throws Exception {

		String urlStr = UrlUtils.apiSubscrUrl("/service/hwater/contObjects/serviceTypeInfo/city");

//		RequestExtraInitializer requestExtraInitializer = (requestBuilder) -> {
//			requestBuilder.contentType(MediaType.APPLICATION_JSON).param("dateFrom", "2015-07-01")
//					.param("dateTo", "2015-07-31").param("cityFias", "deb1d05a-71ce-40d1-b726-6ba85d70d58f");
//		};
//
        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr).param("dateFrom", "2015-07-01")
                .param("dateTo", "2015-07-31")
                .param("cityFias", "deb1d05a-71ce-40d1-b726-6ba85d70d58f"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

//
//		_testGet(urlStr, requestExtraInitializer);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContObjectServiceTypeInfoOne() throws Exception {

		List<Long> ids = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());
		assertTrue(ids.size() > 0);

		String urlStr = UrlUtils.apiSubscrUrl("/service/hwater/contObjects/serviceTypeInfo/" + ids.get(0));

//		RequestExtraInitializer requestExtraInitializer = (requestBuilder) -> {
//			requestBuilder.contentType(MediaType.APPLICATION_JSON).param("dateFrom", "2015-07-01").param("dateTo",
//					"2015-07-31");
//		};

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(urlStr)
                .param("dateFrom", "2015-07-01")
                .param("dateTo", "2015-07-31"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

	}


	public static MockMultipartFile[] makeMultipartFileCsv(ContServiceDataHWaterService hWaterService,
                                                        HWatersCsvService hWatersCsvService,
                                                        String ... fileNames) throws JsonProcessingException {
        LocalDatePeriod dp = LocalDatePeriod.builder().dateFrom("2014-04-01").dateTo("2014-04-30").build()
            .buildEndOfDay();
        List<ContServiceDataHWater> dataHWater = hWaterService.selectByContZPoint(SRC_HW_CONT_ZPOINT_ID,
            TimeDetailKey.TYPE_24H, dp);

        byte[] fileBytes = hWatersCsvService.writeDataHWaterToCsv(dataHWater);

        MockMultipartFile[] result = new MockMultipartFile[fileNames.length];
        int idx = 0;
        for (String fileName : fileNames) {
            MockMultipartFile mmFile = new MockMultipartFile("files", fileName, "text/plain", fileBytes);
            result[idx++] = mmFile;
        }

        return result;

    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManualLoadDataMultipleFiles() throws Exception {

		// Prepare File
        MockMultipartFile[] mockMFiles = makeMultipartFileCsv(contServiceDataHWaterService, hWatersCsvService,
            "AK-SERIAL-777_1_abracadabra.csv", "AK-SERIAL-777_1_abracadabra2.csv");

		// Processing POST
		String url = "/api/subscr/service/datahwater/contObjects/importData";

		ResultActions resultActions = restPortalContObjectMockMvc.perform(
				MockMvcRequestBuilders.fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

		resultActions.andDo(MockMvcResultHandlers.print());
		resultActions.andExpect(status().is2xxSuccessful());
		String resultContent = resultActions.andReturn().getResponse().getContentAsString();

		log.info("Uploaded FileInfoMD5:{}", resultContent);

	}

	@Test
	public void testManualLoadDataMultipleFilesInvalid() throws Exception {

		// Prepare File

        MockMultipartFile[] mockMFiles = makeMultipartFileCsv(contServiceDataHWaterService, hWatersCsvService,
            "AK-SERIAL-xxx1_abracadabra.csv", "AK-SERIALS-xxx_1_abracadabra2.csv");

		String url = "/api/subscr/service/datahwater/contObjects/importData";
		//				apiSubscrUrl(String.format("/contObjects/%d/contZPoints/%d/service/24h/csv", MANUAL_CONT_OBJECT_ID,
		//				MANUAL_HW_CONT_ZPOINT_ID));

		ResultActions resultActions = restPortalContObjectMockMvc.perform(
				MockMvcRequestBuilders.fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

		resultActions.andDo(MockMvcResultHandlers.print());
		resultActions.andExpect(status().is4xxClientError());
	}

}
