package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.support.JodaTimeParser;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceTemperatureSstService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.SubscrControllerTest;

public class LocalPlaceControllerTest extends SubscrControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceControllerTest.class);

	@Autowired
	private LocalPlaceTemperatureSstService localPlaceTemperatureSstService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesAll() throws Exception {
		_testGetJson(API_RMA + "/localPlaces/all");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesSst() throws Exception {
		Long localPlaceId = 490041190L;

		RequestExtraInitializer params = (builder) -> {
			builder.param("sstDateStr", "2016-03-01");
		};
		_testGetJson(API_RMA + "/localPlaces/" + localPlaceId + "/sst", params);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesSstUpdate() throws Exception {
		Long localPlaceId = 490041190L;

		JodaTimeParser<LocalDate> dateParser = JodaTimeParser.parseLocalDate("2016-03-01");
		//JodaTimeParser.parseLocalDate("2016-03-01");

		RequestExtraInitializer params = (builder) -> {
			builder.param("sstDateStr", dateParser.getStringValue());
		};

		List<LocalPlaceTemperatureSst> sstList = localPlaceTemperatureSstService.selectSstByLocalPlace(localPlaceId,
				dateParser.getDateValue());

		for (LocalPlaceTemperatureSst sst : sstList) {
			sst.setSstComment("Modified by ARRAY");
		}

		_testUpdateJson(API_RMA + "/localPlaces/" + localPlaceId + "/sst/array", sstList, params);

	}

}
