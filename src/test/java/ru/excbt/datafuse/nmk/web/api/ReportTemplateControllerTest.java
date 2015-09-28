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

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportTemplateControllerTest extends AnyControllerTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testGetCommReportTemplates() throws Exception {
		_testJsonGet("/api/reportTemplate/commerce");
	}

	@Test
	public void testGetConsT1ReportTemplates() throws Exception {
		_testJsonGet("/api/reportTemplate/cons_t1");
	}

	@Test
	public void testGetConsT2ReportTemplates() throws Exception {
		_testJsonGet("/api/reportTemplate/cons_t2");
	}

	@Test
	public void testGetCommReportTemplatesArch() throws Exception {
		_testJsonGet("/api/reportTemplate/archive/commerce");
	}

	@Test
	public void testGetConsT1ReportTemplatesArch() throws Exception {
		_testJsonGet("/api/reportTemplate/archive/cons_t1");
	}

	@Test
	public void testGetConsT2ReportTemplatesArch() throws Exception {
		_testJsonGet("/api/reportTemplate/archive/cons_t2");
	}

	@Test
	public void testUpdate() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());

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

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testMoveToArchive() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());

		assertTrue(subscriberReportTemplates.size() > 0);

		ReportTemplate rt = null;
		for (ReportTemplate checkTemplate : subscriberReportTemplates) {
			List<ReportParamset> checkList = reportParamsetService
					.selectReportParamset(checkTemplate.getId(), true);
			if (checkList.size() == 0) {
				rt = checkTemplate;
			}
		}

		if (rt == null) {
			return;
		}

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

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetAvailableContbjects() throws Exception {
		_testJsonGet("/api/reportParamset/0/contObject/available");
	}

}
