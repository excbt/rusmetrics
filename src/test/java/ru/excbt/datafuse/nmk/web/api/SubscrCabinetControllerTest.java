package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrCabinetControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetControllerTest.class);

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscriberId() {
		return 512156297L;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscrUserId() {
		return 512156325L;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContObjectInfoList() throws Exception {
		_testGetJson("/api/subscr/subscrCabinet/contObjectCabinetInfo");
	}
}
