package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrSmsLog;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrSmsLogService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrSmsLogControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrSmsLogController subscrSmsLogController;
    @Autowired
    private SubscrSmsLogService subscrSmsLogService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrSmsLogController = new SubscrSmsLogController(subscrSmsLogService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrSmsLogController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSmsLog() throws Exception {

        String content =  mockMvcRestWrapper.restRequest("/api/subscr/smsLog").requestBuilder((b) -> {
            LocalDatePeriod period = LocalDatePeriod.lastWeek();
            b.param("fromDate", period.getDateFromStr());
            b.param("toDate", period.getDateToStr());
        }).testGet().getStringContent();

        List<SubscrSmsLog> result = TestUtils.fromJSON(new TypeReference<List<SubscrSmsLog>>() {
        }, content);

		assertNotNull(result);
	}

}
