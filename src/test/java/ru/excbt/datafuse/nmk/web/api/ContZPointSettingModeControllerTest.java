package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingModeService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingsModeServiceTest;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.transaction.Transactional;

public class ContZPointSettingModeControllerTest extends AnyControllerTest {

	private static final String URL_TEMPLATE = "/api/subscr/contObjects/%s/"
			+ "zpoints/%s/settingMode/%s";

	private static final Logger logger = LoggerFactory
			.getLogger(ContZPointSettingModeControllerTest.class);

	@Autowired
	private ContZPointSettingModeService settingModeService;

	@Autowired
	private ContZPointService contZPointService;

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

			String jsonBody = TestUtils.objectToJson(settingMode);
            logger.info("Testing JSON: {}", jsonBody);


            _testPutJson(urlStr, jsonBody);

		}
	}

	@Test
    @Transactional
	public void testAAA() throws Exception {
		_testGetJson("/api/subscr/contObjects/18811505/zpoints/18811559/settingMode");

	}

}
