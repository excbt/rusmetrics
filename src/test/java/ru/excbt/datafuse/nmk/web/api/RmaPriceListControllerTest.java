package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaPriceListControllerTest extends RmaControllerTest {

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testSubscribers() throws Exception {
		_testJsonGet("/api/rma/priceList/subscribers");
	}

	@Test
	public void testSubscrPriceList() throws Exception {
		_testJsonGet("/api/rma/priceList/" + currentSubscriberService.getSubscriberId());
	}
}
