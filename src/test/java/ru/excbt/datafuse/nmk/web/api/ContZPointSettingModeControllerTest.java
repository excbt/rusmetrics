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
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;

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
	public void testPut() {
		List<ContZPointSettingMode> settingModes = settingModeService
				.findSettingByContZPointId(ContZPointSettingsModeServiceTest.TEST_ZPOINT_ID);

		String urlStr = "";

		for (ContZPointSettingMode settingMode : settingModes) {

			if (settingMode == null) {
				logger.error("settingMode is null");
				continue;
			}
			
			long contZPointId = settingMode.getContZPointId();
			long contObjectId = contZPointService.findOne(contZPointId)
					.getContObject().getId();

			urlStr = String.format(URL_TEMPLATE, contObjectId, contZPointId,
					settingMode.getId());

			logger.info("Testing URL: {}", urlStr);

			settingMode
					.setOv_BalanceM_ctrl(settingMode.getOv_BalanceM_ctrl() + 0.1);

			String jsonBody = null;
			try {
				jsonBody = OBJECT_MAPPER.writeValueAsString(settingMode);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				fail(e.toString());
			}

			try {
				logger.info("Testing JSON: {}",
						OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
								.writeValueAsString(settingMode));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ResultActions resultActionsAll;
			try {
				resultActionsAll = mockMvc.perform(put(urlStr)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody).with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

				resultActionsAll.andDo(MockMvcResultHandlers.print());

				resultActionsAll.andExpect(status().isAccepted());

			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}

		}
	}

	@Test
	public void testAAA() throws Exception {
		testJsonGet("/api/subscr/contObjects/18811505/zpoints/18811559/settingMode");
		
	}
	
}
