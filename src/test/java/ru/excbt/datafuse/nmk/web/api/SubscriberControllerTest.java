package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

@Transactional
public class SubscriberControllerTest extends AnyControllerTest {

	@Test
	public void testSubscriberContObjectCount() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/info/subscriberContObjectCount"));
	}
}
