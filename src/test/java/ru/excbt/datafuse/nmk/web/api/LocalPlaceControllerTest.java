package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.web.SubscrControllerTest;

public class LocalPlaceControllerTest extends SubscrControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceControllerTest.class);

	@Test
	public void testLocalPlacesAll() throws Exception {
		_testGetJson("/api/subscr/localPlaces/all");
	}

}
