package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class SubscrVCookieControllerTest extends RmaControllerTest {

	@Test
	public void testCRUDSubscrUserVCookie() throws Exception {

		MeasureUnit entity = new MeasureUnit();
		entity.setKeyname("TEST");
		entity.setMeasureCategory("CAT");
		entity.setUnitName("My name is TEST");

		SubscrVCookie vc = new SubscrVCookie();
		vc.setVcKey("TEST_MU");
		vc.setVcMode("TEST_MODE");
		vc.setVcValue(objectToJsonStr(entity));

		_testPutJson("/api/subscr/vcookie/list", Lists.newArrayList(vc));

		vc.setDevComment("Override at " + new Date());

		_testPutJson("/api/subscr/vcookie/list", Lists.newArrayList(vc));

	}

}
