package ru.excbt.datafuse.nmk.data.service;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

public class ContObjectServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectServiceTest.class);

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

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
		ContObject result = contObjectService.createContObject(contObject, currentSubscriberService.getSubscriberId(),
				LocalDate.now(), null);
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
		List<ContObjectMeterPeriodSettingsDTO> resultList = contObjectService.findMeterPeriodSettings(getSubscriberParam());
		assertTrue(resultList != null);
	}
	
}
