package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeSmsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrContEventTypeSmsControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrContEventTypeSmsController subscrContEventTypeSmsController;

    @Autowired
    private SubscrContEventTypeSmsService subscrContEventTypeSmsService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContEventTypeSmsController = new SubscrContEventTypeSmsController(subscrContEventTypeSmsService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContEventTypeSmsController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	public void testAvailableContEventTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contEventSms/availableContEventTypes").testGet();
	}

	@Test
	public void testGetContEventTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contEventSms/contEventTypes").testGet();
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

        mockMvcRestWrapper.restRequest("/api/subscr/contEventSms/contEventTypes")
            .requestBuilder((builder) -> builder.param("contEventTypeId", contEventTypeId.toString()))
            .testPost(Arrays.asList(smsAddr));
	}
}
