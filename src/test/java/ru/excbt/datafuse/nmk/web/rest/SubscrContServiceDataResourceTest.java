package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataImpulseControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@RunWith(SpringRunner.class)
public class SubscrContServiceDataResourceTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataResourceTest.class);


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;


    @Autowired
    private ContServiceDataHWaterService hwaterService;

    @Autowired
    private ImpulseCsvService impulseCsvService;


    private SubscrContServiceDataHWaterResource subscrContServiceDataHWaterResource;

    private SubscrContServiceDataResource subscrContServiceDataResource;

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

    @Autowired
    private SubscriberAccessService subscriberAccessService;


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

        subscrContServiceDataResource = new SubscrContServiceDataResource(webAppPropsService,
                                                                          subscrContServiceDataHWaterResource,
                                                                          subscrContServiceDataImpulseController);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrContServiceDataHWaterResource,
                                                                            subscrContServiceDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    /**
     * TODO fix. There is no such conZPointId
     * @throws Exception
     */
    @Test
    @Transactional
    @Ignore
    public void testManualLoadDataMultipleFilesHWater() throws Exception {

        subscriberAccessService.grantContZPointAccess(new ContZPoint().id(128729227L), new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));

        // Prepare File
        MockMultipartFile[] mockMFiles = SubscrContServiceDataHWaterResourceTest.makeMultipartFileCsv(hwaterService, hWatersCsvService,
            "AK-SERIAL-777_1_abracadabra.csv", "AK-SERIAL-777_1_abracadabra2.csv");

        // Processing POST

        ResultActions resultActions =
            restPortalContObjectMockMvc.perform(
            fileUpload("/api/subscr/service-data/cont-objects/import").file(mockMFiles[0]).file(mockMFiles[1]))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().is2xxSuccessful());

        String resultContent = resultActions.andReturn().getResponse().getContentAsString();

        log.info("Uploaded FileInfoMD5:{}", resultContent);

    }

    @Test
    @Transactional
    public void testManualLoadDataMultipleFilesImpulse() throws Exception {

        // Prepare File
        MockMultipartFile[] mockMFiles = SubscrContServiceDataImpulseControllerTest.makeMultipartFileCsv(impulseCsvService,
            "clients1.csv", "clients2.csv");
        // Processing POST

        String url = "/api/subscr/service-data/cont-objects/import";

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(status().is2xxSuccessful());
        String resultContent = resultActions.andReturn().getResponse().getContentAsString();

        log.info("Uploaded FileInfoMD5:{}", resultContent);
    }


}
