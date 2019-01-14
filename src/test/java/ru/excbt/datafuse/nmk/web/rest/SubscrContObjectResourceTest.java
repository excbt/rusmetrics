package ru.excbt.datafuse.nmk.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
@Transactional
public class SubscrContObjectResourceTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContObjectResourceTest.class);

    private final static String contObjectDaDataFilename = "metadata_json/contObjectDaData.json";


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrContObjectResource subscrContObjectResource;

    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private ContGroupService contGroupService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ContObjectFiasService contObjectFiasService;
    @Autowired
    private MeterPeriodSettingService meterPeriodSettingService;
    @Autowired
    private ObjectAccessService objectAccessService;


    @Autowired
    private ContObjectRepository contObjectRepository;

    @Autowired
    private ContObjectFiasRepository contObjectFiasRepository;

    @Autowired
    private MeterPeriodSettingRepository meterPeriodSettingRepository;

    @Autowired
    private OrganizationRepository organizationRepository;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subscrContObjectResource = new SubscrContObjectResource(contObjectService, contGroupService, organizationService,
            contObjectFiasService, meterPeriodSettingService, objectAccessService, portalUserIdsService);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrContObjectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    /**
     * @return
     */
    private List<Long> findSubscriberContObjectIds() {
        log.debug("Finding objects for subscriberId:{}", portalUserIdsService.getCurrentIds().getSubscriberId());
        List<Long> result =  objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());
        assertFalse(result.isEmpty());
        return result;
    }


    @Test
    @Transactional
    public void testContObjectsGet() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }


    @Test
    @Transactional
    public void testContObjectsByMeterPeriodGet() throws Exception {
        List<ContObject> contObjects = objectAccessService.findContObjects(portalUserIdsService.getCurrentIds().getSubscriberId());
        MeterPeriodSetting meterPeriodSetting = new MeterPeriodSetting();
        meterPeriodSetting.setName("TEST PERIOD");
        meterPeriodSetting.setFromDay(1);
        meterPeriodSetting.setToDay(10);
        meterPeriodSetting.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        meterPeriodSettingRepository.saveAndFlush(meterPeriodSetting);
        assertNotNull(meterPeriodSetting.getId());
        contObjects.forEach(i -> {
            i.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriodSetting);
        });
        contObjectRepository.save(contObjects);
        contObjectRepository.flush();

//        RequestExtraInitializer param = builder -> {
//            builder.param("meterPeriodSettingIds", TestUtils.listToString(Arrays.asList(meterPeriodSetting.getId())));
//        };
//

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects")
                .param("meterPeriodSettingIds", TestUtils.listToString(Arrays.asList(meterPeriodSetting.getId()))))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    @Transactional
    public void testCmOrganizatoinsGet() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/cmOrganizations"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    @Transactional
    public void testOrganizatoinsGet() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/organizations"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    @Transactional
    public void testContObjectFiasGet() throws Exception {

        List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());


        List<ContObjectFias> fiasIds = contObjectFiasRepository.findByContObjectIds(ids);

        Optional<ContObjectFias> testObjectFias = fiasIds.stream().filter(i -> i.getFiasUUID() != null).findAny();


        String url = String.format(UrlUtils.apiSubscrUrl("/contObjects/%d/fias"),
            testObjectFias.isPresent() ? testObjectFias.get().getContObjectId() : fiasIds.get(0).getContObjectId());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get(url))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
//
//
//        _testGetSuccessful(url);
    }


    private Organization newCMOrganization (String orgName) {
        Organization organization = new Organization();
        organization.setOrganizationName(orgName);
        organization.setFlagCm(true);
        return organization;
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdate() throws Exception {

        Organization organization = organizationRepository.saveAndFlush(newCMOrganization("My Organization"));

        ContObject testCO = findFirstContObject();
        log.info("Found ContObject (id={})", testCO.getId());
        testCO.setComment("Updated by REST test at " + DateTime.now().toString());

        String urlStr = "/api/subscr/contObjects/" + testCO.getId();

        RequestExtraInitializer param = (builder) -> builder.param("cmOrganizationId", organization.getId().toString());


        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            put(urlStr)
                .param("cmOrganizationId", organization.getId().toString())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(testCO)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

//
//        _testUpdateJson(urlStr, testCO, param);
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testSettingModeTypeGet() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/settingModeType"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

//
//        _testGetJson(UrlUtils.apiSubscrUrl("/contObjects/settingModeType"));
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testSettingModeUpdate() throws Exception {

        List<ContObject> contObjects = objectAccessService.findContObjects(portalUserIdsService.getCurrentIds().getSubscriberId());

        assertNotNull(contObjects);
        assertTrue(contObjects.size() > 0);

        List<Long> contObjectIds = new ArrayList<>();
        contObjectIds.add(contObjects.get(0).getId());

        RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {
            @Override
            public void doInit(MockHttpServletRequestBuilder builder) {
                builder.param("contObjectIds", TestUtils.listToString(contObjectIds));
                builder.param("currentSettingMode", "summer");
            }
        };

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/settingModeType")
                .param("contObjectIds", TestUtils.listToString(contObjectIds))
                .param("currentSettingMode", "summer"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

//
//        _testUpdateJson(UrlUtils.apiSubscrUrl("/contObjects/settingModeType"), null, extraInitializer);

    }

    /**
     * @return
     */
    private ContObject findFirstContObject() {
        List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());
        ContObject testCO = ids.isEmpty() ? null : contObjectService.findContObjectChecked(ids.get(0));
        assertNotNull(testCO);
        return testCO;
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testContObjectDaData() throws Exception {
        Long id = 725L;

        byte[] encoded = Files.readAllBytes(Paths.get(contObjectDaDataFilename));
        String daDataJson = new String(encoded, StandardCharsets.UTF_8);
        assertNotNull(daDataJson);

        ObjectMapper mapper = new ObjectMapper();

        Object json = mapper.readValue(daDataJson, Object.class);

        log.info("daDataJson: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));

        ContObject testCO = contObjectService.findContObjectChecked(id);

        testCO.set_daDataSraw(daDataJson);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/{contObjectId}", testCO.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(testCO)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
//
//
//        _testUpdateJson(urlStr, testCO);

    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetContObjectsGrouped() throws Exception {

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/?contGroupId=488528511"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
//
//
//        _testGetJson("/api/subscr/contObjects/?contGroupId=488528511");
    }

//    /**
//     * @return
//     */
//    @Override
//    public long getSubscriberId() {
//        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
//    }
//
//    /**
//     * @return
//     */
//    @Override
//    public long getSubscrUserId() {
//        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
//    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdateContObjectMeterSettingsDTO() throws Exception {
        Long contObjectId = findSubscriberContObjectIds().get(0);
        MeterPeriodSettingDTO setting = meterPeriodSettingService
            .save(MeterPeriodSettingDTO.builder().name("MySetting").build());
        ContObjectMeterPeriodSettingsDTO coSetting = ContObjectMeterPeriodSettingsDTO.builder()
            .contObjectId(contObjectId).build();
        coSetting.putSetting(ContServiceTypeKey.CW.getKeyname(), setting.getId());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/{contObjectId}/meterPeriodSettings", contObjectId)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coSetting)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
        //_testUpdateJson(UrlUtils.apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId), coSetting);

        ContObject contObject = contObjectService.findContObjectChecked(contObjectId);
        assertTrue(contObject.getMeterPeriodSettings() != null);
        MeterPeriodSetting meterPeriod = contObject.getMeterPeriodSettings().get(ContServiceTypeKey.CW.getKeyname());
        assertTrue(meterPeriod != null);
        assertTrue(meterPeriod.getId().equals(setting.getId()));
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetOneContObjectMeterSettingsDTO() throws Exception {
        Long contObjectId = findSubscriberContObjectIds().get(0);
        MeterPeriodSettingDTO setting = meterPeriodSettingService
            .save(MeterPeriodSettingDTO.builder().name("MySetting").build());
        ContObject contObject = contObjectRepository.findOne(contObjectId);
        MeterPeriodSetting meterPeriod = new MeterPeriodSetting().id(setting.getId());

        contObject.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriod);
        contObjectRepository.saveAndFlush(contObject);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/{contObjectId}/meterPeriodSettings", contObjectId))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

//        _testGetJsonResultActions(UrlUtils.apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId))
//            .andDo((result) -> {
//            });
    }

    @Test
    @Transactional
    public void testGetAllContObjectMeterSettingsDTO() throws Exception {
        Long contObjectId = findSubscriberContObjectIds().get(0);
        MeterPeriodSettingDTO setting = meterPeriodSettingService
            .save(MeterPeriodSettingDTO.builder().name("MySetting").build());
        ContObject contObject = contObjectRepository.findOne(contObjectId);
        MeterPeriodSetting meterPeriod = new MeterPeriodSetting().id(setting.getId());

        contObject.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriod);
        contObjectRepository.saveAndFlush(contObject);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/contObjects/meterPeriodSettings"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

//
//        _testGetJsonResultActions(UrlUtils.apiRmaUrlTemplate("/contObjects/meterPeriodSettings")).andDo((result) -> {
//        });
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdateAllContObjectMeterSettingsDTO() throws Exception {
        MeterPeriodSettingDTO setting = meterPeriodSettingService
            .save(MeterPeriodSettingDTO.builder().name("MySetting").build());

        ContObjectMeterPeriodSettingsDTO contObjectSettings = new ContObjectMeterPeriodSettingsDTO()
            .contObjectIds(findSubscriberContObjectIds()).putSetting("cw", setting.getId());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/meterPeriodSettings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contObjectSettings)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

//
//        _testPutJson(UrlUtils.apiRmaUrlTemplate("/contObjects/meterPeriodSettings"), contObjectSettings).andDo((result) -> {
//        });
    }


    @Test
    public void getContObjectShortInfo() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-objects/short-info"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }
}
