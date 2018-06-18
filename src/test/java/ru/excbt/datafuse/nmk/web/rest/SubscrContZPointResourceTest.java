package ru.excbt.datafuse.nmk.web.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrContZPointResourceTest extends PortalApiTest {

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	@Autowired
	private ContZPointService contZPointService;

    @Autowired
	private ObjectAccessService objectAccessService;

    @Autowired
    private ContZPointMapper contZPointMapper;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @MockBean
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrContZPointResource subscrContZPointResource;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrContZPointResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


    protected Long getFirstContObjectId() {
        List<Long> contObjectIds = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());
        assertTrue(contObjectIds.size() > 0);
        return contObjectIds.get(0);
    }


	@Test
	public void testGetZPointsTimeDetailLastDateMap() throws Exception {
		Long coId = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/timeDetailLastDate", coId);
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
	public void testGetZPointTimeDetailLastDate() throws Exception {
		Long coId = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/%d/timeDetailLastDate", coId, 20118714);
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
	public void testGetElConsZPointVo() throws Exception {
//        List<Long> ids = objectAccessService.findContObjectIds(getSubscriberId());
        Long id = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPoints/vo", id);
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
    @Ignore
	public void testGetZPointsV_01() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/{id1}/contZPointsEx", 20118678).testGet();
	}

	@Test
    //@Ignore
	public void testGetZPointsVO() throws Exception {
        Long contObjectId = getFirstContObjectId();
        String url = String.format("/api/subscr/contObjects/%d/contZPoints/vo", contObjectId);

        mockMvcRestWrapper.restRequest(url).testGet();
	}

	@Test
	public void testZPointsStatInfo() throws Exception {
		Long contObjectId = getFirstContObjectId();
		String url = String.format("/api/subscr/contObjects/%d/contZPointsStatInfo", contObjectId);
        mockMvcRestWrapper.restRequest(url).testGet();
	}

	@Test
	public void testGetZPoint() throws Exception {

		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));
        mockMvcRestWrapper.restRequest(url).testGet();
	}

	@Test
	public void testUpdateZPoint() throws Exception {

		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, MANUAL_HW_CONT_ZPOINT_ID));

		ContZPoint contZPoint = contZPointService.findOne(MANUAL_HW_CONT_ZPOINT_ID);
		contZPoint.setCustomServiceName("Сервис__" + RandomStringUtils.randomNumeric(5));

		contZPoint.setIsManualLoading(true);

        ContZPointDTO contZPointDTO = contZPointMapper.toDto(contZPoint);
        contZPointDTO.setTagNames(new HashSet<>(Arrays.asList("MY-TAG-1", "MY-TAG-2")));

        mockMvcRestWrapper.restRequest(url)
            .testPut(contZPointDTO)
            .testGet();
//		_testUpdateJson(url, contZPointDTO);

//		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContZPoints() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/zpoints").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/zpoints"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContZPointsShortInfo() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/zpoints/shortInfo").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/zpoints/shortInfo"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testServiceTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/contServiceTypes").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/contServiceTypes"));
	}
}
