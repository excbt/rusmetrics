package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscServiceManageControllerTest extends AnyControllerTest {

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPackGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/packs"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testItemsGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/items"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPricesGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/prices"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testManualSubscriberAccessGet() throws Exception {
		_testJsonGet(apiSubscrUrl(String.format("/%d/manage/service/access", MANUAL_SUBSCRIBER_ID)));
	}
}
