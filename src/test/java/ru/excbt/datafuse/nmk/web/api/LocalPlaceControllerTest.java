package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.support.JodaTimeParser;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceService;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceTemperatureSstService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

@RunWith(SpringRunner.class)
@Transactional
public class LocalPlaceControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalContObjectMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;


    @Autowired
    private LocalPlaceTemperatureSstService localPlaceTemperatureSstService;

    @Autowired
    private LocalPlaceService localPlaceService;

	private LocalPlaceController localPlaceController;

	private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        localPlaceController = new LocalPlaceController(localPlaceService, localPlaceTemperatureSstService);

	    this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(localPlaceController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

	    this.mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesAll() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/localPlaces/all").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesSst() throws Exception {
		Long localPlaceId = 490041190L;

		RequestExtraInitializer params = (builder) -> {
			builder.param("sstDateStr", "2016-03-01");
		};
		mockMvcRestWrapper.restRequest("/api/rma/localPlaces/{localPlaceId}/sst", localPlaceId)
            .requestBuilder(b -> b.param("sstDateStr", "2016-03-01")).testGet();
//		_testGetJson(API_RMA + "/localPlaces/" + localPlaceId + "/sst", params);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testLocalPlacesSstUpdate() throws Exception {
		Long localPlaceId = 490041190L;

		JodaTimeParser<LocalDate> dateParser = JodaTimeParser.parseLocalDate("2016-03-01");
		//JodaTimeParser.parseLocalDate("2016-03-01");

		RequestExtraInitializer params = (builder) -> {
			builder.param("sstDateStr", dateParser.getStringValue());
		};

		List<LocalPlaceTemperatureSst> sstList = localPlaceTemperatureSstService.selectSstByLocalPlace(localPlaceId,
				dateParser.getDateValue());

		for (LocalPlaceTemperatureSst sst : sstList) {
			sst.setSstComment("Modified by ARRAY");
		}

		mockMvcRestWrapper.restRequest("/api/rma/localPlaces/{localPlaceId}/sst/array", localPlaceId)
            .requestBuilder(b -> b.param("sstDateStr", dateParser.getStringValue())).testPut(sstList);
//		_testUpdateJson(API_RMA + "/localPlaces/" + localPlaceId + "/sst/array", sstList, params);

	}

}
