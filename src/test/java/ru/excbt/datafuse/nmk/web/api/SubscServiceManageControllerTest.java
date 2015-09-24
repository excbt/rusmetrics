package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscServiceManageControllerTest extends AnyControllerTest {

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
}
