package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class TariffPlanControllerTest extends AnyControllerTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(TariffPlanControllerTest.class);
	
	@Autowired
	private TariffPlanService tariffPlanService;
	
	@Test
	public void testOption() throws Exception{
		testJsonGet("/api/subscr/tariff/option");
	}

	@Test
	public void testType() throws Exception{
		testJsonGet("/api/subscr/tariff/type");
	}

	@Test
	public void testDefault() throws Exception{
		testJsonGet("/api/subscr/tariff/default");
	}
	
	
	@Test
	public void testUpdate() throws Exception {
		
		List<TariffPlan> tariffPlanList = tariffPlanService.getDefaultTariffPlanList();
		if (tariffPlanList.size() == 0) {
			logger.warn("Skip Tarif Plan List");
			//return;
		}
		assertTrue(tariffPlanList.size() > 0);
		
		TariffPlan testRec = tariffPlanList.get(0);
		testRec.setTariffPlanValue(testRec.getTariffPlanValue().add(BigDecimal.valueOf(0.1)));
		String urlStr = "/api/subscr/tariff/" + testRec.getId();
		String jsonBody = OBJECT_MAPPER.writeValueAsString(testRec);
		
		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(post(urlStr)
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
