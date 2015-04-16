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

import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportSheduleControllerTest extends AnyControllerTest {

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testGetSheduleActive() throws Exception {
		testJsonGet("/api/reportShedule/active");
	}

	@Test
	public void testGetSheduleOne() throws Exception {
		List<ReportShedule> reportSheduleList = reportSheduleService
				.selectReportShedule(DateTime.now(),
						currentSubscriberService.getSubscriberId());
		assertTrue(reportSheduleList.size() > 0);
		ReportShedule rs = reportSheduleList.get(0);
		String urlStr = "/api/reportShedule/" + rs.getId().toString();
		testJsonGet(urlStr);
	}
	
	@Test
	public void testUndateShedule() throws Exception {
		List<ReportShedule> reportSheduleList = reportSheduleService
				.selectReportShedule(DateTime.now(),
						currentSubscriberService.getSubscriberId());
		assertTrue(reportSheduleList.size() > 0);
		ReportShedule rs = reportSheduleList.get(0);
		String urlStr = "/api/reportShedule/" + rs.getId().toString();

		rs.setComment("WEB API TEST update " + System.currentTimeMillis());
		
		ResultActions resultActionsAll;
		String jsonBody = OBJECT_MAPPER.writeValueAsString(rs);
		
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
