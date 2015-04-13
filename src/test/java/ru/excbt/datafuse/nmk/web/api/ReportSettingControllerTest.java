package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportSettingControllerTest extends AnyControllerTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testGetReportTypes() throws Exception {
		testJsonGet("/api/reportSettings/reportType");
	}

	@Test
	public void testGetReportPeriod() throws Exception {
		testJsonGet("/api/reportSettings/reportPeriod");
	}

	@Test
	public void testGetReportTemplates() throws Exception {
		testJsonGet("/api/reportSettings/reportTemplate/commerce");
	}

	@Test
	public void testUpdate() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.getSubscriberReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.COMMERCE_REPORT, DateTime.now());

		assertTrue(subscriberReportTemplates.size() > 0);
		ReportTemplate rt = subscriberReportTemplates.get(0);

		rt.setComment("TEST AutoUpdate " + System.currentTimeMillis());
		String jsonBody = OBJECT_MAPPER.writeValueAsString(rt);
		String urlStr = "/api/reportSettings/reportTemplate/commerce/"
				+ rt.getId();

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
}
