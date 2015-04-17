package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

public class ReportParamsetControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetControllerTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;
	private static final long TEMPLATE_PARAMSET_ID = 28344056;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Test
	public void testCommParamset() throws Exception {
		testJsonGet("/api/reportParamset/commerce");
	}

	@Test
	public void testCreateCommerce() throws Exception {
		List<ReportTemplate> commTemplates = reportTemplateService
				.selectDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true);

		assertTrue(commTemplates.size() > 0);

		ReportTemplate rt = commTemplates.get(0);

		ReportParamset rp = new ReportParamset();
		rp.set_active(true);
		rp.setActiveStartDate(new Date());
		rp.setName("Created by REST");
		rp.setOutputFileType(ReportOutputFileType.PDF);
		rp.setReportPeriodKey(ReportPeriodKey.LAST_MONTH);

		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(rp);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		ResultActions resultAction = mockMvc.perform(post(
				"/api/reportParamset/commerce")
				.contentType(MediaType.APPLICATION_JSON)
				.param("reportTemplateId", rt.getId().toString())
				.content(jsonBody).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);

	}

	@Test
	public void testUpdateUnitParamset() throws Exception {

		long[] objectIds = {18811504L, 18811505L};

		logger.info("Array of {}", arrayToString (objectIds));
		
		ResultActions resultAction = mockMvc
				.perform(put(
						String.format("/api/reportParamset/%d/contObjects",
								TEMPLATE_PARAMSET_ID))
						.contentType(MediaType.APPLICATION_JSON)
						.param("contObjectIds", arrayToString(objectIds))
						.with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isAccepted());

	}

}
