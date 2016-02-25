package ru.excbt.datafuse.nmk.web.api;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrDeviceObjectPkeControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectPkeControllerTest.class);

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectPkeTypes() throws Exception {
		String url = "/api/subscr/deviceObjects/pke/types";
		_testGetJson(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectPkeWarn() throws Exception {
		long deviceObjectId = 447631920;
		String url = String.format("/api/subscr/deviceObjects/pke/%d/warn", deviceObjectId);
		List<String> pkeTypes = Arrays.asList("U23_ABOVE_LIMIT");
		logger.info("URL: {}", url);

		RequestExtraInitializer params = (b) -> {
			b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
			b.param("pkeTypeKeynames", listToString(pkeTypes));
		};

		_testGetJson(url, params);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContZPointPkeWarn() throws Exception {
		long contZPointId = 447697474;
		String url = String.format("/api/subscr/deviceObjects/pke/byContZPoint/%d/warn", contZPointId);
		List<String> pkeTypes = Arrays.asList("FREQUENCY_BELOW_NORMAL");
		logger.info("URL: {}", url);

		RequestExtraInitializer params = (b) -> {
			b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
			b.param("pkeTypeKeynames", listToString(pkeTypes));
		};

		_testGetJson(url, params);
	}

}
