package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.TariffPlanConstant.TariffOptionKey;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class TariffPlanControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(TariffPlanControllerTest.class);

	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Test
	public void testOption() throws Exception {
		testJsonGet("/api/subscr/tariff/option");
	}

	@Test
	public void testType() throws Exception {
		testJsonGet("/api/subscr/tariff/type");
	}

	@Test
	public void testRso() throws Exception {
		testJsonGet("/api/subscr/tariff/rso");
	}

	@Test
	public void testDefault() throws Exception {
		testJsonGet("/api/subscr/tariff/default");
	}

	@Test
	public void testUpdate() throws Exception {

		List<TariffPlan> tariffPlanList = tariffPlanService
				.getDefaultTariffPlanList();
		if (tariffPlanList.size() == 0) {
			logger.warn("Skip Tarif Plan List");
			// return;
		}
		assertTrue(tariffPlanList.size() > 0);

		TariffPlan testRec = tariffPlanList.get(0);
		if (testRec.getTariffPlanValue() != null) {
			testRec.setTariffPlanValue(testRec.getTariffPlanValue().add(
					BigDecimal.valueOf(0.1)));
		} else {
			testRec.setTariffPlanValue(BigDecimal.valueOf(0.1));
		}
		String urlStr = "/api/subscr/tariff/" + testRec.getId();
		String jsonBody = OBJECT_MAPPER.writeValueAsString(testRec);

		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(put(urlStr)
					.contentType(MediaType.APPLICATION_JSON).content(jsonBody)
					.with(testSecurityContext())
					.accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isAccepted());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testCreate() throws Exception {
		TariffPlan tariffPlan = new TariffPlan();
		tariffPlan.setTariffOptionKey(TariffOptionKey.PREFERENTIAL);
		tariffPlan.setTariffPlanValue(BigDecimal.valueOf(1.1));
		tariffPlan.setTariffPlanComment("Created by REST");
		tariffPlan.setStartDate(LocalDate.now().toDate());

		String urlStr = "/api/subscr/tariff";
		String jsonBody = OBJECT_MAPPER.writeValueAsString(tariffPlan);

		Iterable<Organization> orgList = subscriberRepository
				.selectRsoOrganizations(currentSubscriberService
						.getSubscriberId());

		assertTrue(orgList.iterator().hasNext());
		Organization org = orgList.iterator().next();

		List<TariffType> tariffTypeList = tariffTypeRepository
				.findByContServiceType("cw");

		assertTrue(tariffTypeList.size() > 0);
		TariffType tt = tariffTypeList.get(0);

		ResultActions resultAction = mockMvc
				.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
						.param("rsoOrganizationId", org.getId().toString())
						.param("tariffTypeId", tt.getId().toString())
						.content(jsonBody).with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

	}
}
