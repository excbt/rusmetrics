package ru.excbt.datafuse.nmk.web.rest;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

@Transactional
public class SubscrContObjectResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContObjectResourceTest.class);

    private final static String contObjectDaDataFilename = "metadata_json/contObjectDaData.json";

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private CurrentSubscriberService currentSubscriberService;

    @Autowired
    private ContObjectService contObjectService;

    @Autowired
    private ContObjectRepository contObjectRepository;

    @Autowired
    private ContObjectFiasRepository contObjectFiasRepository;

    @Autowired
    private MeterPeriodSettingService meterPeriodSettingService;

    @Autowired
    private MeterPeriodSettingRepository meterPeriodSettingRepository;

    @Autowired
    private ObjectAccessService objectAccessService;

    /**
     * @return
     */
    private List<Long> findSubscriberContObjectIds() {
        log.debug("Finding objects for subscriberId:{}", getSubscriberId());
        List<Long> result =  objectAccessService.findContObjectIds(getSubscriberId());
        assertFalse(result.isEmpty());
        return result;
    }


    @Test
    @Transactional
    public void testContObjectsGet() throws Exception {
        _testGetJson("/api/subscr/contObjects");
    }


    @Test
    @Transactional
    public void testContObjectsByMeterPeriodGet() throws Exception {
        List<ContObject> contObjects = objectAccessService.findContObjects(getSubscriberId());
        MeterPeriodSetting meterPeriodSetting = new MeterPeriodSetting();
        meterPeriodSetting.setName("TEST PERIOD");
        meterPeriodSetting.setFromDay(1);
        meterPeriodSetting.setToDay(10);
        meterPeriodSetting.setSubscriberId(getSubscriberId());
        meterPeriodSettingRepository.saveAndFlush(meterPeriodSetting);
        assertNotNull(meterPeriodSetting.getId());
        contObjects.forEach(i -> {
            i.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriodSetting);
        });
        contObjectRepository.save(contObjects);
        contObjectRepository.flush();

        RequestExtraInitializer param = builder -> {
            builder.param("meterPeriodSettingIds", TestUtils.listToString(Arrays.asList(meterPeriodSetting.getId())));
        };

        _testGetJson("/api/subscr/contObjects", param);
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

        List<Long> ids = objectAccessService.findContObjectIds(getSubscriberId());


        List<ContObjectFias> fiasIds = contObjectFiasRepository.findByContObjectIds(ids);

        Optional<ContObjectFias> testObjectFias = fiasIds.stream().filter(i -> i.getFiasUUID() != null).findAny();


        String url = String.format(UrlUtils.apiSubscrUrl("/contObjects/%d/fias"),
            testObjectFias.isPresent() ? testObjectFias.get().getContObjectId() : fiasIds.get(0).getContObjectId());
        _testGetSuccessful(url);
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdate() throws Exception {

        ContObject testCO = findFirstContObject();
        log.info("Found ContObject (id={})", testCO.getId());
        testCO.setComment("Updated by REST test at " + DateTime.now().toString());

        String urlStr = "/api/subscr/contObjects/" + testCO.getId();

        RequestExtraInitializer param = (builder) -> {
            builder.param("cmOrganizationId", testCO.get_activeContManagement().getOrganization().getId().toString());
        };

        _testUpdateJson(urlStr, testCO, param);
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testSettingModeTypeGet() throws Exception {
        _testGetJson(UrlUtils.apiSubscrUrl("/contObjects/settingModeType"));
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testSettingModeUpdate() throws Exception {

        List<ContObject> contObjects = objectAccessService.findContObjects(currentSubscriberService.getSubscriberId());

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
        _testUpdateJson(UrlUtils.apiSubscrUrl("/contObjects/settingModeType"), null, extraInitializer);

    }

    /**
     * @return
     */
    private ContObject findFirstContObject() {
        List<Long> ids = objectAccessService.findContObjectIds(getSubscriberId());
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
        String urlStr = "/api/subscr/contObjects/" + testCO.getId();

        testCO.set_daDataSraw(daDataJson);

        _testUpdateJson(urlStr, testCO);

    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetContObjectsGrouped() throws Exception {
        _testGetJson("/api/subscr/contObjects/?contGroupId=488528511");
    }

    /**
     * @return
     */
    @Override
    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    /**
     * @return
     */
    @Override
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }

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
        _testUpdateJson(UrlUtils.apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId), coSetting);

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
        _testGetJsonResultActions(UrlUtils.apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId))
            .andDo((result) -> {
            });
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
        _testGetJsonResultActions(UrlUtils.apiRmaUrlTemplate("/contObjects/meterPeriodSettings")).andDo((result) -> {
        });
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

        _testPutJson(UrlUtils.apiRmaUrlTemplate("/contObjects/meterPeriodSettings"), contObjectSettings).andDo((result) -> {
        });
    }

}
