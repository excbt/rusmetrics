package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.report.model.ReportWizardParam;
import ru.excbt.datafuse.nmk.report.service.ReportWizardService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ReportWizardControllerTest extends AnyControllerTest {

	@Autowired
	private ReportWizardService reportWizardService;
	
	
	@Test
	public void testCommerceReportColumnSettings() throws Exception {
		testJsonGet("/api/reportWizard/columnSettings/commerce");
	}
	
	
	@Test
	public void testCreateWizard() throws JsonProcessingException {

		///
		ReportWizardParam reportWizardParam = new ReportWizardParam();		
		reportWizardParam.setReportColumnSettings(reportWizardService
				.getReportColumnSettings());
		/// 
		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setComment("Created By Wizard");
		reportTemplate.setActiveStartDate(new Date());
		reportTemplate.set_active(true);
		//
		reportWizardParam.setReportTemplate(reportTemplate);

		String jsonBody = OBJECT_MAPPER.writeValueAsString(reportWizardParam);
		String urlStr = "/api/reportWizard/commerce";

		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(post(urlStr)
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
}
