package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportSheduleTypeKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;



@Transactional
public class ReportSheduleControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportSheduleControllerTest.class);

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
    @Transactional
	public void testGetSheduleActive() throws Exception {
		_testGetJson("/api/reportShedule/active");
	}

	@Test
    @Transactional
	public void testGetShedule() throws Exception {
		_testGetJson("/api/reportShedule");
	}

	@Test
    @Transactional
	public void testGetSheduleOne() throws Exception {
		List<ReportShedule> reportSheduleList = reportSheduleService
				.selectReportShedule(currentSubscriberService.getSubscriberId(), LocalDateTime.now());

		if (reportSheduleList.size() == 0) {
			logger.error("reportSheduleList is empty. Can't test testGetSheduleOne");
			return;
		}

		assertTrue(reportSheduleList.size() > 0);
		ReportShedule rs = reportSheduleList.get(0);
		String urlStr = "/api/reportShedule/" + rs.getId().toString();
		_testGetJson(urlStr);
	}

	@Test
    @Transactional
	public void testUndateShedule() throws Exception {
		List<ReportShedule> reportSheduleList = reportSheduleService
				.selectReportShedule(currentSubscriberService.getSubscriberId(), LocalDateTime.now());

		if (reportSheduleList.size() == 0) {
			logger.error("reportSheduleList is empty. Can't test testUndateShedule");
			return;
		}

		assertTrue(reportSheduleList.size() > 0);
		ReportShedule rs = reportSheduleList.get(0);
		String urlStr = "/api/reportShedule/" + rs.getId().toString();

		rs.setComment("WEB API TEST update " + System.currentTimeMillis());

		ResultActions resultActionsAll;
		String jsonBody = TestUtils.objectToJson(rs);

		try {
			resultActionsAll = mockMvc.perform(put(urlStr).contentType(MediaType.APPLICATION_JSON)
					.param("reportTemplateId", rs.getReportTemplate().getId().toString())
					.param("reportParamsetId", rs.getReportParamset().getId().toString())

					.content(jsonBody).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
    @Transactional
	public void testCreateShedule() throws Exception {

		List<ReportTemplate> reportTemplates = reportTemplateService.selectSubscriberReportTemplates(
				ReportTypeKey.COMMERCE_REPORT, true, currentSubscriberService.getSubscriberId());

		assertTrue(reportTemplates.size() > 0);

		ReportTemplate sReportTemplate = reportTemplates.get(0);

		logger.info("Found ReportTemplate (id={})", sReportTemplate.getId());

		ReportParamset sReportParamset = null;
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportParamset(sReportTemplate.getId(),
				DateTime.now());

		if (reportParamsetList.size() == 0) {
			sReportParamset = reportParamsetService.createReportParamsetEx(sReportTemplate.getId(),
					"Auto Genereate for TEST", ReportPeriodKey.CURRENT_MONTH, ReportOutputFileType.PDF,
					currentSubscriberService.getSubscriberId(), true);
		} else {
			sReportParamset = reportParamsetList.get(0);
		}

		ReportShedule rs = new ReportShedule();
		rs.setReportSheduleTypeKey(ReportSheduleTypeKey.DAILY);
		rs.setSheduleStartDate(new Date());
		rs.setSheduleTimeTemplate("0 11 * * *");
		String urlStr = "/api/reportShedule/";

		String jsonBody = TestUtils.objectToJson(rs);

		ResultActions resultAction = mockMvc.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
				.param("reportTemplateId", sReportTemplate.getId().toString())
				.param("reportParamsetId", sReportParamset.getId().toString()).content(jsonBody)
				.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse().getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);

		String urlStrDelete = "/api/reportShedule/" + String.valueOf(createdId);
		_testDeleteJson(urlStrDelete);
	}

}
