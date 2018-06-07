package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ReportTemplateControllerTest extends PortalApiTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

	@Autowired
	private ReportTemplateController reportTemplateController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(reportTemplateController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
    @Transactional
	public void testGetCommReportTemplates() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/commerce").testGet();
	}

	@Test
    @Transactional
	public void testGetConsT1ReportTemplates() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/cons_t1").testGet();
	}

	@Test
    @Transactional
	public void testGetConsT2ReportTemplates() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/cons_t2").testGet();
	}

	@Test
    @Transactional
	public void testGetCommReportTemplatesArch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/archive/commerce").testGet();
	}

	@Test
    @Transactional
	public void testGetConsT1ReportTemplatesArch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/archive/cons_t1").testGet();
	}

	@Test
    @Transactional
	public void testGetConsT2ReportTemplatesArch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportTemplate/archive/cons_t2").testGet();
	}

	@Ignore
	@Test
    @Transactional
	public void testUpdate() throws Exception {
		List<ReportTemplate> subscriberReportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());

		assertTrue(subscriberReportTemplates.size() > 0);
		ReportTemplate rt = subscriberReportTemplates.get(0);

		rt.setComment("TEST AutoUpdate " + System.currentTimeMillis());
		String jsonBody = TestUtils.objectToJson(rt);
		String urlStr = "/api/reportTemplate/commerce/" + rt.getId();

		ResultActions resultActionsAll;
		try {
			resultActionsAll = restPortalMockMvc.perform(put(urlStr)
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
    @Transactional
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
			resultActionsAll = restPortalMockMvc.perform(put(urlStr)
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
    @Transactional
    @Ignore
	public void testGetAvailableContbjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportParamset/0/contObject/available").testGet();
	}

}
