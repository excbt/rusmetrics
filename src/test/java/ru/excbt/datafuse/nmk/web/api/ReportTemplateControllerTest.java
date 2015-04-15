package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportTemplateControllerTest extends AnyControllerTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testGetCommReportTemplates() throws Exception {
		testJsonGet("/api/reportTemplate/commerce");
	}

	@Test
	public void testGetConsReportTemplates() throws Exception {
		testJsonGet("/api/reportTemplate/cons");
	}

	@Test
	public void testGetCommReportTemplatesArch() throws Exception {
		testJsonGet("/api/reportTemplate/archive/commerce");
	}

	@Test
	public void testGetConsReportTemplatesArch() throws Exception {
		testJsonGet("/api/reportTemplate/archive/cons");
	}

	@Test
	public void testUpdate() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.COMMERCE_REPORT, true);

		assertTrue(subscriberReportTemplates.size() > 0);
		ReportTemplate rt = subscriberReportTemplates.get(0);

		rt.setComment("TEST AutoUpdate " + System.currentTimeMillis());
		String jsonBody = OBJECT_MAPPER.writeValueAsString(rt);
		String urlStr = "/api/reportTemplate/commerce/" + rt.getId();

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
	public void testMoveToArchive() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.COMMERCE_REPORT, true);

		assertTrue(subscriberReportTemplates.size() > 0);
		ReportTemplate rt = subscriberReportTemplates.get(0);

		// rt.setComment("TEST AutoUpdate " + System.currentTimeMillis());
		// String jsonBody = OBJECT_MAPPER.writeValueAsString(rt);
		String urlStr = "/api/reportTemplate/archive/move";

		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(put(urlStr)
					.param("reportTemplateId", rt.getId().toString())
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
	public void testGetAvailableContbjects() throws Exception {
		testJsonGet("/api/reportParamset/0/contObject/available");
	}

}
