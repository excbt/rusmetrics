package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ContObjectServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectServiceTest.class);

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private ContObjectRepository contObjectRepository;

	//@Autowired
	//private SubscrContObjectRepository subscrContObjectRepository;

    @Autowired
	private ObjectAccessService objectAccessService;

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

	/**
	 *
	 */
	@Test
	@Transactional
	public void testIzhevskCont() {
		List<ContObject> res = contObjectService.findContObjectsByFullName("%Ижевск%");
		// assertTrue(res.size() > 0);
		assertNotNull(res);
		logger.info("Found {} ContObjects from Izhevsk", res.size());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testCreateContObject() throws Exception {
		ContObject contObject = new ContObject();
		contObject.setComment("Created by Test");
		contObject.setTimezoneDefKeyname("MSK");
		contObject.setName("Cont Object TEST");
//		ContObject result = contObjectService.createContObject(contObject, currentSubscriberService.getSubscriberId(),
//				LocalDate.now(), null);
		ContObject result = contObjectService.automationCreate(contObject, portalUserIdsService.getCurrentIds().getSubscriberId(),
            java.time.LocalDate.now(), null);
		assertNotNull(result);
	}

	@Test
	@Transactional
	public void testGetWeatherForecast() throws Exception {
		WeatherForecast weatherForecast = contObjectService.selectWeatherForecast(114426490L,
				java.time.LocalDate.now());
		assertNotNull(weatherForecast);
		logger.info("current temp: {} at {}. weatherPlaceId {}", weatherForecast.getTemperatureValue(),
				weatherForecast.getForecastDateTime(), weatherForecast.getWeatherPlaceId());
	}

	@Test
	@Transactional
	public void testFindMeterPeriodSetting() throws Exception {
		List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds());
		List<ContObjectMeterPeriodSettingsDTO> resultList = contObjectService.findMeterPeriodSettings(ids);
		assertTrue(resultList != null);
	}

	@Test
	@Transactional
	public void testUpdateMeterPeriodSettingMulty() throws Exception {
		List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds());
		MeterPeriodSettingDTO settings = meterPeriodSettingService
				.save(MeterPeriodSettingDTO.builder().name("My MeterPeriodSetting").build());
		ContObjectMeterPeriodSettingsDTO contObjectsSettings = ContObjectMeterPeriodSettingsDTO.builder().contObjectIds(ids)
				.build();
		contObjectsSettings.putSetting(ContServiceTypeKey.CW.getKeyname(), settings.getId());
		contObjectService.updateMeterPeriodSettings(contObjectsSettings);
	}

}
