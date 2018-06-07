package ru.excbt.datafuse.nmk.web.api;

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
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingModeService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingsModeServiceTest;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringRunner.class)
public class ContZPointSettingModeControllerTest extends PortalApiTest {

	private static final String URL_TEMPLATE = "/api/subscr/contObjects/%s/"
			+ "zpoints/%s/settingMode/%s";

	private static final Logger logger = LoggerFactory
			.getLogger(ContZPointSettingModeControllerTest.class);


	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalContObjectMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private SubscrContZPointSettingModeController subscrContZPointSettingModeController;

    @Autowired
    private ContZPointSettingModeService settingModeService;

    @Autowired
    private ContZPointService contZPointService;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContZPointSettingModeController = new SubscrContZPointSettingModeController(settingModeService, contZPointService);

	    this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrContZPointSettingModeController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

	    this.mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
	}


	/**
	 *
	 */
	@Test
	@Transactional
	public void testPut() throws Exception {
		List<ContZPointSettingMode> settingModes = settingModeService
				.findSettingByContZPointId(ContZPointSettingsModeServiceTest.TEST_ZPOINT_ID);

		String urlStr = "";

		for (ContZPointSettingMode settingMode : settingModes) {

			if (settingMode == null) {
				logger.error("settingMode is null");
				continue;
			}

			long contZPointId = settingMode.getContZPoint().getId();
			long contObjectId = contZPointService.findOne(contZPointId)
					.getContObject().getId();

			urlStr = String.format(URL_TEMPLATE, contObjectId, contZPointId,
					settingMode.getId());

			logger.info("Testing URL: {}", urlStr);

			settingMode
					.setOv_BalanceM_ctrl(settingMode.getOv_BalanceM_ctrl() + 0.1);


            mockMvcRestWrapper.restRequest(urlStr).testPut(settingMode);

		}
	}

	@Test
    @Transactional
	public void testAAA() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/18811505/zpoints/18811559/settingMode")
            .testGet();
	}

}
