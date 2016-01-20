package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeSmsService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.SubscrControllerTest;

public class SubscrContEventTypeSmsControllerTest extends SubscrControllerTest {

	@Autowired
	private SubscrContEventTypeSmsService subscrContEventTypeSmsService;

	@Test
	@Ignore
	public void testAvailableContEventTypes() throws Exception {
		_testJsonGet(apiSubscrUrl("/contEventSms/availableContEventTypes"));
	}

	@Test
	@Ignore
	public void testGetContEventTypes() throws Exception {
		_testJsonGet(apiSubscrUrl("/contEventSms/contEventTypes"));
	}

	@Test
	@Ignore
	public void testCreateSms() throws Exception {
		List<ContEventType> availList = subscrContEventTypeSmsService.selectAvailableContEventTypes();
		assertTrue(availList.size() > 0);
		Long contEventTypeId = availList.get(0).getId();

		SubscrContEventTypeSmsAddr smsAddr = new SubscrContEventTypeSmsAddr();
		smsAddr.setAddressSms("+1 (234) 567-78-90");
		smsAddr.setAddressName("Test sms address");

		RequestExtraInitializer params = (builder) -> {
			builder.param("contEventTypeId", contEventTypeId.toString());
		};

		_testJsonCreate(apiSubscrUrl("/contEventSms/contEventTypes"), Arrays.asList(smsAddr), params);
	}
}
