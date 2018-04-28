package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

/**
 *
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.06.2016
 *
 */
@RunWith(SpringRunner.class)
public class RmaSubscrLogSessionControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrLogSessionControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

	@Autowired
	private RmaSubscrLogSessionController rmaSubscrLogSessionController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscrLogSessionController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testLogSession() throws Exception {
//		RequestExtraInitializer params = (b) -> {
//			LocalDatePeriod period = LocalDatePeriod.lastWeek();
//			b.param("fromDate", period.getDateFromStr());
//			b.param("toDate", period.getDateToStr());
//		};
        LocalDatePeriod period = LocalDatePeriod.lastWeek();
        String content = mockMvcRestWrapper.restRequest("/api/rma/logSessions")
            .requestBuilder(b -> b
                .param("fromDate", period.getDateFromStr())
                .param("toDate", period.getDateToStr()))
            .testGet().getStringContent();
//		String content = _testGetJson("/api/rma/logSessions", params);
		List<LogSession> result = TestUtils.fromJSON(new TypeReference<List<LogSession>>() {
		}, content);

		assertNotNull(result);

		assertFalse(result.isEmpty());

		int cnt = 0;
		for (LogSession logSession : result) {
            mockMvcRestWrapper.restRequest("/api/rma/logSessions/{id1}/steps", logSession.getId()).testGet();
//			_testGetJson(String.format());
			if (cnt++ > 10) {
			    break;
            }
		}

	}

	@Test
    @Ignore
	public void testLogSessionObject() throws Exception {

		//127858526

//		RequestExtraInitializer params = (b) -> {
//
//			b.param("fromDate", period.getDateFromStr());
//			b.param("toDate", period.getDateToStr());
//			b.param("contObjectIds", TestUtils.listToString(Arrays.asList(127858526L)));
//		};

        LocalDatePeriod period = LocalDatePeriod.lastWeek();
        String content = mockMvcRestWrapper.restRequest("/api/rma/logSessions")
            .requestBuilder(b -> b
                .param("fromDate", period.getDateFromStr())
                .param("toDate", period.getDateToStr())
                .param("contObjectIds", TestUtils.listToString(Arrays.asList(127858526L))))
            .testGet().getStringContent();
//		String content = _testGetJson("/api/rma/logSessions", params);
		List<LogSession> result = TestUtils.fromJSON(new TypeReference<List<LogSession>>() {
		}, content);

		assertNotNull(result);

		//assertFalse(result.isEmpty());

	}

}
