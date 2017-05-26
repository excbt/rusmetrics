package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;

import org.junit.Test;

import com.google.common.collect.Lists;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

@Transactional
public class SubscrVCookieControllerTest extends RmaControllerTest {

	@Test
	public void testCRUDSubscrVCookie() throws Exception {

		MeasureUnit entity = new MeasureUnit();
		entity.setKeyname("TEST");
		entity.setMeasureCategory("CAT");
		entity.setUnitName("My name is TEST");

		SubscrVCookie vc = new SubscrVCookie();
		vc.setVcKey("TEST_MU");
		vc.setVcMode("TEST_MODE");
		vc.setVcValue(TestUtils.objectToJsonStr(entity));

		_testPutJson("/api/subscr/vcookie/list", Lists.newArrayList(vc));

		vc.setDevComment("Override at " + new Date());

		_testPutJson("/api/subscr/vcookie/list", Lists.newArrayList(vc));

	}

	@Test
	public void testGetSubscrVCookie() throws Exception {
		_testGetJson("/api/subscr/vcookie");
	}

	@Test
	public void testCRUDSubscrUserVCookie() throws Exception {

		MeasureUnit entity = new MeasureUnit();
		entity.setKeyname("TEST");
		entity.setMeasureCategory("CAT");
		entity.setUnitName("My name is TEST");

		SubscrVCookie vc = new SubscrVCookie();
		vc.setVcKey("TEST_MU");
		vc.setVcMode("TEST_MODE");
		vc.setVcValue(TestUtils.objectToJsonStr(entity));

		_testPutJson("/api/subscr/vcookie/user/list", Lists.newArrayList(vc));

		vc.setDevComment("Override at " + new Date());

		_testPutJson("/api/subscr/vcookie/user/list", Lists.newArrayList(vc));

	}

	@Test
	public void testGetSubscrUserVCookie() throws Exception {
		_testGetJson("/api/subscr/vcookie/user");

		RequestExtraInitializer param = (builder) -> {
			builder.param("vcMode", "TEST_MODE");
		};

		_testGet("/api/subscr/vcookie/user", param);

	}

	@Test
	public void testWidgetsList() throws Exception {
		_testGetJson("/api/subscr/vcookie/widgets/list");
	}

}
