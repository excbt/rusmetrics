package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.SubscrControllerTest;

public class LocalPlaceControllerTest extends SubscrControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceControllerTest.class);

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
			builder.param("sstDateStr", "2016-01-01");
		};
		_testGetJson(API_RMA + "/localPlaces/" + localPlaceId + "/sst", params);
	}

}
