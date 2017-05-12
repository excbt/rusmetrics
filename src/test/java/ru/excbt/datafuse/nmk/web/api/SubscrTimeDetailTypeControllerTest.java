package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

@Transactional
public class SubscrTimeDetailTypeControllerTest extends AnyControllerTest {

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testTimeDetailType1h24h() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/timeDetailType/1h24h"));
	}
}
